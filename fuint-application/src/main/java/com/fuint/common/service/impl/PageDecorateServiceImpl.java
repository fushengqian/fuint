package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.dto.decorate.PageComponentDto;
import com.fuint.common.dto.decorate.PageDecorationDto;
import com.fuint.common.dto.decorate.TabbarDto;
import com.fuint.common.dto.decorate.ThemeDto;
import com.fuint.common.dto.decorate.UserPageDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.PagePage;
import com.fuint.common.service.PageDecorateService;
import com.fuint.common.service.SettingService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtPageItemMapper;
import com.fuint.repository.mapper.MtPageMapper;
import com.fuint.repository.model.MtPage;
import com.fuint.repository.model.MtPageItem;
import com.fuint.repository.model.MtSetting;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 页面装修业务接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class PageDecorateServiceImpl extends ServiceImpl<MtPageMapper, MtPage> implements PageDecorateService {

    private static final Logger logger = LoggerFactory.getLogger(PageDecorateServiceImpl.class);

    private MtPageMapper mtPageMapper;

    private MtPageItemMapper mtPageItemMapper;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 分页查询装修页面列表
     */
    @Override
    public PaginationResponse<MtPage> queryPageListByPagination(PagePage pagePage) {
        Page<MtPage> pageHelper = PageHelper.startPage(pagePage.getPage(), pagePage.getPageSize());
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtPage::getStatus, StatusEnum.DISABLE.getKey());

        String pageName = pagePage.getPageName();
        if (StringUtils.isNotBlank(pageName)) {
            lambdaQueryWrapper.like(MtPage::getPageName, pageName);
        }
        String pageType = pagePage.getPageType();
        if (StringUtils.isNotBlank(pageType)) {
            lambdaQueryWrapper.eq(MtPage::getPageType, pageType);
        }
        String status = pagePage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtPage::getStatus, status);
        }
        Integer merchantId = pagePage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtPage::getMerchantId, merchantId);
        }
        Integer storeId = pagePage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.and(wq -> wq.eq(MtPage::getStoreId, 0).or().eq(MtPage::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByDesc(MtPage::getUpdateTime);
        List<MtPage> dataList = mtPageMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(pagePage.getPage(), pagePage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtPage> paginationResponse = new PaginationResponse(pageImpl, MtPage.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 根据ID查询装修页面
     */
    @Override
    public MtPage queryPageById(Integer id) {
        return mtPageMapper.selectById(id);
    }

    /**
     * 获取页面装修详情（含组件明细）
     */
    @Override
    public PageDecorationDto getPageDetail(Integer id) {
        MtPage mtPage = queryPageById(id);
        if (mtPage == null) {
            return null;
        }
        return buildDecorationDto(mtPage);
    }

    /**
     * 获取默认装修页面（含组件明细）
     */
    @Override
    public PageDecorationDto getDefaultPage(Integer merchantId, Integer storeId, String pageType) {
        // 优先取指定店铺，其次取商户级（storeId=0）
        PageDecorationDto pageDto = getDefaultPageByStore(merchantId, storeId, pageType);
        if (pageDto == null && storeId != null && storeId > 0) {
            pageDto = getDefaultPageByStore(merchantId, 0, pageType);
        }
        return pageDto;
    }

    /**
     * 按店铺查询默认装修页面
     */
    private PageDecorationDto getDefaultPageByStore(Integer merchantId, Integer storeId, String pageType) {
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtPage::getStatus, StatusEnum.ENABLED.getKey());
        lambdaQueryWrapper.eq(MtPage::getPageType, StringUtil.isBlank(pageType) ? "index" : pageType);
        lambdaQueryWrapper.eq(MtPage::getIsDefault, "Y");
        lambdaQueryWrapper.eq(MtPage::getStoreId, storeId == null ? 0 : storeId);
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtPage::getMerchantId, merchantId);
        }
        lambdaQueryWrapper.last("limit 1");
        MtPage mtPage = mtPageMapper.selectOne(lambdaQueryWrapper);
        if (mtPage == null) {
            return null;
        }
        return buildDecorationDto(mtPage);
    }

    /**
     * 保存装修页面（新增或更新，组件全量覆盖）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtPage savePage(PageDecorationDto pageDto, AccountInfo accountInfo) throws BusinessCheckException {
        if (StringUtil.isBlank(pageDto.getPageName())) {
            throw new BusinessCheckException("页面名称不能为空");
        }
        if (StringUtil.isBlank(pageDto.getPageType())) {
            pageDto.setPageType("index");
        }

        MtPage mtPage = new MtPage();
        if (pageDto.getId() != null && pageDto.getId() > 0) {
            mtPage = queryPageById(pageDto.getId());
            if (mtPage == null) {
                throw new BusinessCheckException("数据不存在");
            }
            if (accountInfo != null && accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0
                    && !mtPage.getMerchantId().equals(accountInfo.getMerchantId())) {
                throw new BusinessCheckException("不同商户，无权限操作");
            }
        }

        if (accountInfo != null) {
            pageDto.setMerchantId(accountInfo.getMerchantId());
            pageDto.setStoreId(accountInfo.getStoreId());
        }
        mtPage.setMerchantId(pageDto.getMerchantId() == null ? 0 : pageDto.getMerchantId());
        mtPage.setStoreId(pageDto.getStoreId() == null ? 0 : pageDto.getStoreId());
        mtPage.setPageName(pageDto.getPageName());
        mtPage.setPageType(pageDto.getPageType());
        mtPage.setShareTitle(pageDto.getShareTitle());
        mtPage.setShareLogo(pageDto.getShareLogo());
        if (pageDto.getStatus() != null) {
            mtPage.setStatus(pageDto.getStatus());
        } else {
            mtPage.setStatus(StatusEnum.ENABLED.getKey());
        }
        if (accountInfo != null) {
            mtPage.setOperator(accountInfo.getAccountName());
        }
        mtPage.setUpdateTime(new Date());

        if (pageDto.getId() != null && pageDto.getId() > 0) {
            // 更新
            mtPageMapper.updateById(mtPage);
        } else {
            // 新增
            mtPage.setCreateTime(new Date());
            if ("Y".equals(pageDto.getIsDefault())) {
                clearDefault(mtPage.getMerchantId(), mtPage.getStoreId(), mtPage.getPageType());
            } else if (isFirstPage(mtPage.getMerchantId(), mtPage.getStoreId(), mtPage.getPageType())) {
                mtPage.setIsDefault("Y");
            }
            mtPageMapper.insert(mtPage);
        }

        // 全量覆盖组件明细
        mtPageItemMapper.delete(Wrappers.lambdaQuery(MtPageItem.class).eq(MtPageItem::getPageId, mtPage.getId()));

        List<PageComponentDto> components = pageDto.getComponents();
        if (components != null && components.size() > 0) {
            int sort = 0;
            for (PageComponentDto component : components) {
                MtPageItem item = new MtPageItem();
                item.setPageId(mtPage.getId());
                item.setComponentType(component.getType());
                item.setComponentName(component.getName());
                item.setSort(sort++);
                item.setStatus(StatusEnum.ENABLED.getKey());
                item.setStyle(toJson(component.getStyle()));
                item.setParams(toJson(component.getParams()));
                item.setData(toJson(component.getData()));
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());
                mtPageItemMapper.insert(item);
            }
        }

        return mtPage;
    }

    /**
     * 设为默认页面
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultPage(Integer id, AccountInfo accountInfo) throws BusinessCheckException {
        MtPage mtPage = queryPageById(id);
        if (mtPage == null) {
            throw new BusinessCheckException("数据不存在");
        }
        if (accountInfo != null && accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0
                && !mtPage.getMerchantId().equals(accountInfo.getMerchantId())) {
            throw new BusinessCheckException("不同商户，无权限操作");
        }
        clearDefault(mtPage.getMerchantId(), mtPage.getStoreId(), mtPage.getPageType());
        mtPage.setIsDefault(YesOrNoEnum.YES.getKey());
        mtPage.setUpdateTime(new Date());
        mtPageMapper.updateById(mtPage);
        return true;
    }

    /**
     * 删除装修页面
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePage(Integer id, AccountInfo accountInfo) throws BusinessCheckException {
        MtPage mtPage = queryPageById(id);
        if (mtPage == null) {
            throw new BusinessCheckException("数据不存在");
        }
        if (accountInfo != null && accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0
                && !mtPage.getMerchantId().equals(accountInfo.getMerchantId())) {
            throw new BusinessCheckException("不同商户，无权限操作");
        }
        mtPage.setStatus(StatusEnum.DISABLE.getKey());
        mtPage.setUpdateTime(new Date());
        mtPageMapper.updateById(mtPage);
        mtPageItemMapper.delete(Wrappers.lambdaQuery(MtPageItem.class).eq(MtPageItem::getPageId, id));
        return true;
    }

    /**
     * 获取主题配置
     */
    @Override
    public ThemeDto getTheme(Integer merchantId, Integer storeId) {
        String value = getSettingValue(merchantId, storeId, SettingTypeEnum.THEME.getKey());
        if (StringUtil.isBlank(value)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, new TypeReference<ThemeDto>() {});
        } catch (Exception e) {
            logger.error("解析主题配置失败", e);
            return null;
        }
    }

    /**
     * 保存主题配置
     */
    @Override
    public boolean saveTheme(ThemeDto themeDto, AccountInfo accountInfo) throws BusinessCheckException {
        saveSetting(accountInfo, SettingTypeEnum.THEME.getKey(), themeDto);
        return true;
    }

    /**
     * 获取底部导航配置
     */
    @Override
    public TabbarDto getTabbar(Integer merchantId, Integer storeId) {
        String value = getSettingValue(merchantId, storeId, SettingTypeEnum.TABBAR.getKey());
        if (StringUtil.isBlank(value)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, new TypeReference<TabbarDto>() {});
        } catch (Exception e) {
            logger.error("解析底部导航配置失败", e);
            return null;
        }
    }

    /**
     * 保存底部导航配置
     */
    @Override
    public boolean saveTabbar(TabbarDto tabbarDto, AccountInfo accountInfo) throws BusinessCheckException {
        saveSetting(accountInfo, SettingTypeEnum.TABBAR.getKey(), tabbarDto);
        return true;
    }

    /**
     * 获取个人中心配置
     */
    @Override
    public UserPageDto getUserPage(Integer merchantId, Integer storeId) {
        String value = getSettingValue(merchantId, storeId, SettingTypeEnum.USER_PAGE.getKey());
        if (StringUtil.isBlank(value)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, new TypeReference<UserPageDto>() {});
        } catch (Exception e) {
            logger.error("解析个人中心配置失败", e);
            return null;
        }
    }

    /**
     * 保存个人中心配置
     */
    @Override
    public boolean saveUserPage(UserPageDto userPageDto, AccountInfo accountInfo) throws BusinessCheckException {
        saveSetting(accountInfo, SettingTypeEnum.USER_PAGE.getKey(), userPageDto);
        return true;
    }

    /**
     * 获取页面列表
     */
    @Override
    public List<PageDecorationDto> getPageList(Integer merchantId, Integer storeId, String pageType) {
        List<PageDecorationDto> result = new ArrayList<>();
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtPage::getStatus, StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotBlank(pageType)) {
            lambdaQueryWrapper.eq(MtPage::getPageType, pageType);
        }
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtPage::getMerchantId, merchantId);
        }
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.and(wq -> wq.eq(MtPage::getStoreId, 0).or().eq(MtPage::getStoreId, storeId));
        }
        lambdaQueryWrapper.orderByDesc(MtPage::getUpdateTime);
        List<MtPage> pageList = mtPageMapper.selectList(lambdaQueryWrapper);
        for (MtPage mtPage : pageList) {
            result.add(buildDecorationDto(mtPage));
        }
        return result;
    }

    /**
     * 构建页面装修 DTO（含组件明细）
     */
    private PageDecorationDto buildDecorationDto(MtPage mtPage) {
        PageDecorationDto pageDto = new PageDecorationDto();
        BeanUtils.copyProperties(mtPage, pageDto);

        LambdaQueryWrapper<MtPageItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtPageItem::getPageId, mtPage.getId());
        lambdaQueryWrapper.eq(MtPageItem::getStatus, StatusEnum.ENABLED.getKey());
        lambdaQueryWrapper.orderByAsc(MtPageItem::getSort);
        List<MtPageItem> items = mtPageItemMapper.selectList(lambdaQueryWrapper);

        List<PageComponentDto> components = new ArrayList<>();
        for (MtPageItem item : items) {
            PageComponentDto component = new PageComponentDto();
            component.setId(item.getId());
            component.setType(item.getComponentType());
            component.setName(item.getComponentName());
            component.setSort(item.getSort());
            component.setStyle(parseJson(item.getStyle()));
            component.setParams(parseJson(item.getParams()));
            component.setData(parseJson(item.getData()));
            components.add(component);
        }
        pageDto.setComponents(components);
        return pageDto;
    }

    /**
     * 清除同商户同店铺同类型下的默认标记
     */
    private void clearDefault(Integer merchantId, Integer storeId, String pageType) {
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtPage::getMerchantId, merchantId == null ? 0 : merchantId);
        lambdaQueryWrapper.eq(MtPage::getStoreId, storeId == null ? 0 : storeId);
        lambdaQueryWrapper.eq(MtPage::getPageType, StringUtil.isBlank(pageType) ? "index" : pageType);
        lambdaQueryWrapper.eq(MtPage::getIsDefault, "Y");
        List<MtPage> pageList = mtPageMapper.selectList(lambdaQueryWrapper);
        for (MtPage mtPage : pageList) {
            mtPage.setIsDefault("N");
            mtPage.setUpdateTime(new Date());
            mtPageMapper.updateById(mtPage);
        }
    }

    /**
     * 判断是否为第一个页面
     */
    private boolean isFirstPage(Integer merchantId, Integer storeId, String pageType) {
        LambdaQueryWrapper<MtPage> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtPage::getMerchantId, merchantId == null ? 0 : merchantId);
        lambdaQueryWrapper.eq(MtPage::getStoreId, storeId == null ? 0 : storeId);
        lambdaQueryWrapper.eq(MtPage::getPageType, StringUtil.isBlank(pageType) ? "index" : pageType);
        lambdaQueryWrapper.ne(MtPage::getStatus, StatusEnum.DISABLE.getKey());
        return mtPageMapper.selectCount(lambdaQueryWrapper) == 0;
    }

    /**
     * 读取配置值
     */
    private String getSettingValue(Integer merchantId, Integer storeId, String type) {
        MtSetting mtSetting = settingService.querySettingByName(merchantId, storeId == null ? 0 : storeId, type, type);
        if (mtSetting != null) {
            return mtSetting.getValue();
        }
        return null;
    }

    /**
     * 保存配置
     */
    private void saveSetting(AccountInfo accountInfo, String type, Object data) throws BusinessCheckException {
        MtSetting mtSetting = new MtSetting();
        mtSetting.setType(type);
        mtSetting.setName(type);
        if (accountInfo != null) {
            mtSetting.setMerchantId(accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId());
            mtSetting.setStoreId(accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId());
            mtSetting.setOperator(accountInfo.getAccountName());
        } else {
            mtSetting.setMerchantId(0);
            mtSetting.setStoreId(0);
        }
        mtSetting.setValue(toJson(data));
        mtSetting.setStatus(StatusEnum.ENABLED.getKey());
        mtSetting.setCreateTime(new Date());
        mtSetting.setUpdateTime(new Date());
        settingService.saveSetting(mtSetting);
    }

    /**
     * 对象转 JSON 字符串
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("对象转JSON失败", e);
            return null;
        }
    }

    /**
     * JSON 字符串转 Map
     */
    private Map<String, Object> parseJson(String json) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }
}
