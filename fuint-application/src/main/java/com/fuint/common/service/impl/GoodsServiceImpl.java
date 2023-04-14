package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.GoodsDto;
import com.fuint.common.dto.GoodsSpecValueDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.CateService;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.mapper.MtGoodsSpecMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<MtGoodsMapper, MtGoods> implements GoodsService {

    @Resource
    private MtGoodsMapper mtGoodsMapper;

    @Resource
    private MtGoodsSpecMapper mtGoodsSpecMapper;

    @Resource
    private MtGoodsSkuMapper mtGoodsSkuMapper;

    @Autowired
    private SettingService settingService;

    @Autowired
    private CateService cateService;

    @Autowired
    private StoreService storeService;

    /**
     * 分页查询商品列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<GoodsDto> queryGoodsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtGoods> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtGoods> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtGoods::getStatus, StatusEnum.DISABLE.getKey());

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtGoods::getName, name);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtGoods::getStatus, status);
        }
        String goodsNo = paginationRequest.getSearchParams().get("goodsNo") == null ? "" : paginationRequest.getSearchParams().get("goodsNo").toString();
        if (StringUtils.isNotBlank(goodsNo)) {
            lambdaQueryWrapper.eq(MtGoods::getGoodsNo, goodsNo);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtGoods::getStoreId, storeId);
        }
        String type = paginationRequest.getSearchParams().get("type") == null ? "" : paginationRequest.getSearchParams().get("type").toString();
        if (StringUtils.isNotBlank(type)) {
            lambdaQueryWrapper.eq(MtGoods::getType, type);
        }

        lambdaQueryWrapper.orderByAsc(MtGoods::getSort);
        List<MtGoods> goodsList = mtGoodsMapper.selectList(lambdaQueryWrapper);
        List<GoodsDto> dataList = new ArrayList<>();
        String basePath = settingService.getUploadBasePath();
        for (MtGoods mtGoods : goodsList) {
            MtGoodsCate cateInfo = null;
            if (mtGoods.getCateId() != null) {
                cateInfo = cateService.queryCateById(mtGoods.getCateId());
            }
            GoodsDto item = new GoodsDto();
            item.setId(mtGoods.getId());
            item.setInitSale(mtGoods.getInitSale());
            if (StringUtil.isNotEmpty(mtGoods.getLogo())) {
                item.setLogo(basePath + mtGoods.getLogo());
            }
            item.setStoreId(mtGoods.getStoreId());
            if (mtGoods.getStoreId() != null) {
                MtStore storeInfo = storeService.queryStoreById(mtGoods.getStoreId());
                item.setStoreInfo(storeInfo);
            }
            item.setName(mtGoods.getName());
            item.setGoodsNo(mtGoods.getGoodsNo());
            item.setCateId(mtGoods.getCateId());
            item.setCateInfo(cateInfo);
            item.setPrice(mtGoods.getPrice());
            item.setLinePrice(mtGoods.getLinePrice());
            item.setSalePoint(mtGoods.getSalePoint());
            item.setDescription(mtGoods.getDescription());
            item.setCreateTime(mtGoods.getCreateTime());
            item.setUpdateTime(mtGoods.getUpdateTime());
            item.setStatus(mtGoods.getStatus());
            item.setOperator(mtGoods.getOperator());
            dataList.add(item);
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<GoodsDto> paginationResponse = new PaginationResponse(pageImpl, GoodsDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存商品信息
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存商品信息")
    public MtGoods saveGoods(MtGoods reqDto) {
        MtGoods mtGoods = new MtGoods();
        if (reqDto.getId() > 0) {
            mtGoods = this.queryGoodsById(reqDto.getId());
        }
        if (reqDto.getStoreId() != null) {
            mtGoods.setStoreId(reqDto.getStoreId() >= 0 ? reqDto.getStoreId() : 0);
        }
        if (StringUtil.isNotEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec(reqDto.getIsSingleSpec());
        }
        if (reqDto.getId() <= 0 && StringUtil.isEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec(YesOrNoEnum.YES.getKey());
        }
        if (StringUtil.isNotEmpty(reqDto.getName())) {
            mtGoods.setName(reqDto.getName());
        }
        if (StringUtil.isNotEmpty(reqDto.getStatus())) {
            mtGoods.setStatus(reqDto.getStatus());
        }
        if (StringUtil.isNotEmpty(reqDto.getLogo())) {
            mtGoods.setLogo(reqDto.getLogo());
        }
        if (StringUtil.isNotEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec(reqDto.getIsSingleSpec());
        }
        if (StringUtil.isNotEmpty(reqDto.getDescription())) {
            mtGoods.setDescription(reqDto.getDescription());
        }
        if (StringUtil.isNotEmpty(reqDto.getOperator())) {
            mtGoods.setOperator(reqDto.getOperator());
        }
        if (StringUtil.isNotEmpty(reqDto.getType())) {
            mtGoods.setType(reqDto.getType());
        }
        if (reqDto.getCateId() != null && reqDto.getCateId() > 0) {
            mtGoods.setCateId(reqDto.getCateId());
        }
        if (reqDto.getServiceTime() != null && reqDto.getServiceTime() > 0) {
            mtGoods.setServiceTime(reqDto.getServiceTime());
        }
        if (StringUtil.isNotEmpty(reqDto.getGoodsNo())) {
            mtGoods.setGoodsNo(reqDto.getGoodsNo());
        }
        if (reqDto.getSort() != null) {
            mtGoods.setSort(reqDto.getSort());
        }
        if (reqDto.getId() == null && (mtGoods.getSort().equals("") || mtGoods.getSort() == null )) {
            mtGoods.setSort(0);
        }
        if (reqDto.getPrice() != null) {
            mtGoods.setPrice(reqDto.getPrice());
        }
        if (reqDto.getPrice() == null && reqDto.getId() <= 0) {
            mtGoods.setPrice(new BigDecimal("0.00"));
        }
        if (reqDto.getLinePrice() != null) {
            mtGoods.setLinePrice(reqDto.getLinePrice());
        }
        if (reqDto.getLinePrice() == null && reqDto.getId() <= 0) {
            mtGoods.setLinePrice(new BigDecimal("0.00"));
        }
        if (StringUtil.isNotEmpty(reqDto.getCouponIds())) {
            mtGoods.setCouponIds(reqDto.getCouponIds());
        }
        if (reqDto.getWeight() != null) {
            mtGoods.setWeight(reqDto.getWeight());
        }
        if (reqDto.getInitSale() != null) {
            mtGoods.setInitSale(reqDto.getInitSale());
        }
        if (reqDto.getStock() != null) {
            mtGoods.setStock(reqDto.getStock());
        }
        if (StringUtil.isNotEmpty(reqDto.getSalePoint())) {
            mtGoods.setSalePoint(reqDto.getSalePoint());
        }
        if (StringUtil.isEmpty(reqDto.getSalePoint()) && reqDto.getId() <= 0) {
            reqDto.setSalePoint("");
        }
        if (StringUtil.isNotEmpty(reqDto.getCanUsePoint())) {
            mtGoods.setCanUsePoint(reqDto.getCanUsePoint());
        }
        if (StringUtil.isNotEmpty(reqDto.getIsMemberDiscount())) {
            mtGoods.setIsMemberDiscount(reqDto.getIsMemberDiscount());
        }
        if (StringUtil.isNotEmpty(reqDto.getImages())) {
            mtGoods.setImages(reqDto.getImages());
        }

        mtGoods.setUpdateTime(new Date());
        if (reqDto.getId() == null || reqDto.getId() <= 0) {
            mtGoods.setCreateTime(new Date());
            this.save(mtGoods);
        } else {
            this.updateById(mtGoods);
        }

        return mtGoods;
    }

    /**
     * 根据ID获取商品信息
     *
     * @param  id 商品ID
     * @throws BusinessCheckException
     */
    @Override
    public MtGoods queryGoodsById(Integer id) {
       MtGoods mtGoods = mtGoodsMapper.selectById(id);
       if (mtGoods == null) {
           return null;
       }
       return mtGoods;
    }

    /**
     * 根据编码获取商品信息
     *
     * @param  goodsNo
     * @throws BusinessCheckException
     */
    @Override
    public MtGoods queryGoodsByGoodsNo(String goodsNo) {
        MtGoods mtGoods = mtGoodsMapper.getByGoodsNo(goodsNo);
        return mtGoods;
    }

    /**
     * 根据条码获取sku信息
     *
     * @param skuNo skuNo
     * @throws BusinessCheckException
     * */
    @Override
    public MtGoodsSku getSkuInfoBySkuNo(String skuNo) {
        List<MtGoodsSku> mtGoodsSkuList = mtGoodsSkuMapper.getBySkuNo(skuNo);

        if (mtGoodsSkuList.size() > 0) {
            return mtGoodsSkuList.get(0);
        }

        return null;
    }

    /**
     * 根据ID获取商品详情
     *
     * @param  id 商品ID
     * @throws BusinessCheckException
     */
    @Override
    public GoodsDto getGoodsDetail(Integer id, boolean getDeleteSpec) {
        if (id == null || id < 1) {
            return null;
        }

        MtGoods mtGoods = mtGoodsMapper.selectById(id);
        GoodsDto goodsInfo = new GoodsDto();

        if (mtGoods != null) {
            try {
                BeanUtils.copyProperties(mtGoods, goodsInfo);
            } catch (Exception e) {
                goodsInfo.setId(mtGoods.getId());
                goodsInfo.setType(mtGoods.getType());
                goodsInfo.setStoreId(mtGoods.getStoreId());
                goodsInfo.setName(mtGoods.getName());
                goodsInfo.setCateId(mtGoods.getCateId());
                goodsInfo.setGoodsNo(mtGoods.getGoodsNo());
                goodsInfo.setIsSingleSpec(mtGoods.getIsSingleSpec());
                goodsInfo.setLogo(mtGoods.getLogo());
                goodsInfo.setImages(mtGoods.getImages());
                goodsInfo.setStatus(mtGoods.getStatus());
                goodsInfo.setSort(mtGoods.getSort());
                goodsInfo.setPrice(mtGoods.getPrice());
                goodsInfo.setLinePrice(mtGoods.getLinePrice());
                goodsInfo.setServiceTime(mtGoods.getServiceTime());
                goodsInfo.setCouponIds(mtGoods.getCouponIds());
            }
        }

        String basePath = settingService.getUploadBasePath();
        if (StringUtil.isNotEmpty(goodsInfo.getLogo())) {
            goodsInfo.setLogo(basePath + goodsInfo.getLogo());
        }

        // 规格列表
        Map<String, Object> param = new HashMap<>();
        param.put("goods_id", id.toString());
        if (getDeleteSpec == false) {
            param.put("status", StatusEnum.ENABLED.getKey());
        }
        List<MtGoodsSpec> goodsSpecList = mtGoodsSpecMapper.selectByMap(param);
        goodsInfo.setSpecList(goodsSpecList);

        // sku列表
        if (goodsInfo.getIsSingleSpec().equals(YesOrNoEnum.NO.getKey())) {
            List<MtGoodsSku> goodsSkuList = mtGoodsSkuMapper.selectByMap(param);
            goodsInfo.setSkuList(goodsSkuList);
            // 多规格商品的价格、库存数量
            if (goodsSkuList.size() > 0) {
                goodsInfo.setPrice(goodsSkuList.get(0).getPrice());
                goodsInfo.setLinePrice(goodsSkuList.get(0).getLinePrice());
                Integer stock = 0;
                for (MtGoodsSku mtGoodsSku : goodsSkuList) {
                     stock = stock + mtGoodsSku.getStock();
                }
                goodsInfo.setStock(stock);
            } else {
                goodsInfo.setStock(0);
            }
        } else {
            goodsInfo.setSkuList(new ArrayList<>());
        }

        return goodsInfo;
    }

    /**
     * 根据ID删除商品信息
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除商品信息")
    public void deleteGoods(Integer id, String operator) {
        MtGoods cateInfo = this.queryGoodsById(id);
        if (null == cateInfo) {
            return;
        }

        cateInfo.setStatus(StatusEnum.DISABLE.getKey());
        cateInfo.setUpdateTime(new Date());

        mtGoodsMapper.updateById(cateInfo);
    }

    /**
     * 获取店铺的商品列表
     * @param storeId
     * @param keyword
     * @return
     * */
    @Override
    public List<MtGoods> getStoreGoodsList(Integer storeId, String keyword) {
        List<MtGoods> goodsList = new ArrayList<>();
        List<MtGoodsSku> skuList = new ArrayList<>();
        if (StringUtil.isNotEmpty(keyword)) {
            skuList = mtGoodsSkuMapper.getBySkuNo(keyword);
        }
        if (skuList != null && skuList.size() > 0) {
            MtGoods goods = mtGoodsMapper.selectById(skuList.get(0).getGoodsId());
            goodsList.add(goods);
        } else {
            if (keyword != null && StringUtil.isNotEmpty(keyword)) {
                goodsList = mtGoodsMapper.searchStoreGoodsList(storeId, keyword);
            } else {
                goodsList = mtGoodsMapper.getStoreGoodsList(storeId);
            }
        }
        List<MtGoods> dataList = new ArrayList<>();
        if (goodsList.size() > 0) {
            for (MtGoods mtGoods : goodsList) {
                // 多规格商品价格、库存数量
                if (mtGoods.getIsSingleSpec().equals(YesOrNoEnum.NO.getKey())) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("goods_id", mtGoods.getId().toString());
                    param.put("status", StatusEnum.ENABLED.getKey());
                    List<MtGoodsSku> goodsSkuList = mtGoodsSkuMapper.selectByMap(param);
                    if (goodsSkuList.size() > 0) {
                        mtGoods.setPrice(goodsSkuList.get(0).getPrice());
                        mtGoods.setLinePrice(goodsSkuList.get(0).getLinePrice());
                        Integer stock = 0;
                        for (MtGoodsSku mtGoodsSku : goodsSkuList) {
                             stock = stock + mtGoodsSku.getStock();
                        }
                        mtGoods.setStock(stock);
                    } else {
                        mtGoods.setStock(0);
                    }
                }
                dataList.add(mtGoods);
            }
        }
        return dataList;
    }

    @Override
    public List<GoodsSpecValueDto> getSpecListBySkuId(Integer skuId) {
        if (skuId < 0 || skuId == null) {
            return new ArrayList<>();
        }
        List<GoodsSpecValueDto> result = new ArrayList<>();

        MtGoodsSku goodsSku = mtGoodsSkuMapper.selectById(skuId);
        if (goodsSku == null) {
            return result;
        }

        String specIds = goodsSku.getSpecIds();
        String specIdArr[] = specIds.split("-");
        for (String specId : specIdArr) {
            MtGoodsSpec mtGoodsSpec = mtGoodsSpecMapper.selectById(Integer.parseInt(specId));
            GoodsSpecValueDto dto = new GoodsSpecValueDto();
            dto.setSpecValueId(mtGoodsSpec.getId());
            dto.setSpecName(mtGoodsSpec.getName());
            dto.setSpecValue(mtGoodsSpec.getValue());
            result.add(dto);
        }

        return result;
    }

    @Override
    public MtGoodsSpec getSpecDetail(Integer specId) {
        MtGoodsSpec mtGoodsSpec = mtGoodsSpecMapper.selectById(specId);
        return mtGoodsSpec;
    }

    /**
     * 更新已售数量
     * */
    @Override
    public Boolean updateInitSale(Integer goodsId) {
        return mtGoodsMapper.updateInitSale(goodsId);
    }
}
