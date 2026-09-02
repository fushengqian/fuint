package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.decorate.PageDecorationDto;
import com.fuint.common.dto.decorate.TabbarDto;
import com.fuint.common.dto.decorate.ThemeDto;
import com.fuint.common.dto.decorate.UserPageDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.PagePage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtPage;
import java.util.List;

/**
 * 页面装修业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface PageDecorateService extends IService<MtPage> {

    /**
     * 分页查询装修页面列表
     *
     * @param pagePage 分页参数
     * @return
     */
    PaginationResponse<MtPage> queryPageListByPagination(PagePage pagePage);

    /**
     * 根据ID查询装修页面
     *
     * @param id 页面ID
     * @return
     */
    MtPage queryPageById(Integer id);

    /**
     * 获取页面装修详情（含组件明细）
     *
     * @param id 页面ID
     * @return
     */
    PageDecorationDto getPageDetail(Integer id);

    /**
     * 获取默认装修页面（含组件明细）
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID，0表示商户级
     * @param pageType 页面类型：index首页
     * @return
     */
    PageDecorationDto getDefaultPage(Integer merchantId, Integer storeId, String pageType);

    /**
     * 保存装修页面（新增或更新，组件全量覆盖）
     *
     * @param pageDto 页面装修数据
     * @param accountInfo 操作人
     * @return
     */
    MtPage savePage(PageDecorationDto pageDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 设为默认页面
     *
     * @param id 页面ID
     * @param accountInfo 操作人
     * @return
     */
    boolean setDefaultPage(Integer id, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 删除装修页面
     *
     * @param id 页面ID
     * @param accountInfo 操作人
     * @return
     */
    boolean deletePage(Integer id, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取主题配置
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     */
    ThemeDto getTheme(Integer merchantId, Integer storeId);

    /**
     * 保存主题配置
     *
     * @param themeDto 主题配置
     * @param accountInfo 操作人
     * @return
     */
    boolean saveTheme(ThemeDto themeDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取底部导航配置
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     */
    TabbarDto getTabbar(Integer merchantId, Integer storeId);

    /**
     * 保存底部导航配置
     *
     * @param tabbarDto 底部导航配置
     * @param accountInfo 操作人
     * @return
     */
    boolean saveTabbar(TabbarDto tabbarDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取个人中心配置
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     */
    UserPageDto getUserPage(Integer merchantId, Integer storeId);

    /**
     * 保存个人中心配置
     *
     * @param userPageDto 个人中心配置
     * @param accountInfo 操作人
     * @return
     */
    boolean saveUserPage(UserPageDto userPageDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取页面组件明细列表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param pageType 页面类型：index首页
     * @return
     */
    List<PageDecorationDto> getPageList(Integer merchantId, Integer storeId, String pageType);
}
