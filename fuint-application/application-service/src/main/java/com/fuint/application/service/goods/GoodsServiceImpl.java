package com.fuint.application.service.goods;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtGoodsRepository;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import com.fuint.application.dao.repositories.MtGoodsSpecRepository;
import com.fuint.application.dto.ConfirmLogDto;
import com.fuint.application.dto.GoodsDto;
import com.fuint.application.dto.GoodsSpecValueDto;
import com.fuint.application.service.store.StoreService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 商品业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private MtGoodsRepository goodsRepository;

    @Autowired
    private MtGoodsSpecRepository goodsSpecRepository;

    @Autowired
    private MtGoodsSkuRepository goodsSkuRepository;

    @Autowired
    private Environment env;

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
        PaginationResponse<MtGoods> paginationResponse = goodsRepository.findResultsByPagination(paginationRequest);

        List<GoodsDto> content = new ArrayList<>();
        List<MtGoods> dataList = paginationResponse.getContent();

        String basePath = env.getProperty("images.upload.url");

        for (MtGoods mtGoods : dataList) {
            MtGoodsCate cateInfo = null;
            if (mtGoods.getCateId() != null) {
                cateInfo = cateService.queryCateById(mtGoods.getCateId());
            }
            GoodsDto item = new GoodsDto();
            item.setId(mtGoods.getId());
            item.setInitSale(mtGoods.getInitSale());
            if (StringUtils.isNotEmpty(mtGoods.getLogo())) {
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
            content.add(item);
        }

        PageRequest pageRequest = new PageRequest((paginationRequest.getCurrentPage() +1), paginationRequest.getPageSize());
        Page page = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<GoodsDto> result = new PaginationResponse(page, ConfirmLogDto.class);
        result.setTotalPages(paginationResponse.getTotalPages());
        result.setContent(content);

        return result;
    }

    /**
     * 添加商品
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存商品")
    public MtGoods saveGoods(MtGoods reqDto) {
        MtGoods mtGoods = new MtGoods();
        if (reqDto.getId() > 0) {
            mtGoods = this.queryGoodsById(reqDto.getId());
        }
        if (reqDto.getStoreId() != null) {
            mtGoods.setStoreId(reqDto.getStoreId() >= 0 ? reqDto.getStoreId() : 0);
        }
        if (StringUtils.isNotEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec(reqDto.getIsSingleSpec());
        }
        if (reqDto.getId() <= 0 && StringUtils.isEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec("Y");
        }
        if (StringUtils.isNotEmpty(reqDto.getName())) {
            mtGoods.setName(reqDto.getName());
        }
        if (StringUtils.isNotEmpty(reqDto.getStatus())) {
            mtGoods.setStatus(reqDto.getStatus());
        }
        if (StringUtils.isNotEmpty(reqDto.getLogo())) {
            mtGoods.setLogo(reqDto.getLogo());
        }
        if (StringUtils.isNotEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec(reqDto.getIsSingleSpec());
        }
        if (StringUtils.isNotEmpty(reqDto.getDescription())) {
            mtGoods.setDescription(reqDto.getDescription());
        }
        if (StringUtils.isNotEmpty(reqDto.getOperator())) {
            mtGoods.setOperator(reqDto.getOperator());
        }
        if (reqDto.getCateId() > 0) {
            mtGoods.setCateId(reqDto.getCateId());
        }
        if (StringUtils.isNotEmpty(reqDto.getGoodsNo())) {
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
        if (reqDto.getLinePrice() != null) {
            mtGoods.setLinePrice(reqDto.getLinePrice());
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
        if (StringUtils.isNotEmpty(reqDto.getSalePoint())) {
            mtGoods.setSalePoint(reqDto.getSalePoint());
        }
        if (StringUtils.isNotEmpty(reqDto.getCanUsePoint())) {
            mtGoods.setCanUsePoint(reqDto.getCanUsePoint());
        }
        if (StringUtils.isNotEmpty(reqDto.getIsMemberDiscount())) {
            mtGoods.setIsMemberDiscount(reqDto.getIsMemberDiscount());
        }
        if (StringUtils.isNotEmpty(reqDto.getImages())) {
            mtGoods.setImages(reqDto.getImages());
        }

        mtGoods.setUpdateTime(new Date());
        if (reqDto.getId() <= 0) {
            mtGoods.setCreateTime(new Date());
        }

        return goodsRepository.save(mtGoods);
    }

    /**
     * 根据ID获取商品信息
     *
     * @param id 商品ID
     * @throws BusinessCheckException
     */
    @Override
    public MtGoods queryGoodsById(Integer id) {
       MtGoods mtGoods = goodsRepository.findOne(id);
       if (mtGoods == null) {
           return null;
       }
       MtGoods goodsInfo = new MtGoods();
       org.springframework.beans.BeanUtils.copyProperties(mtGoods, goodsInfo);
       return goodsInfo;
    }

    /**
     * 根据条码获取sku信息
     *
     * @param skuNo skuNo
     * @throws BusinessCheckException
     * */
    @Override
    public MtGoodsSku getSkuInfoBySkuNo(String skuNo) {
        List<MtGoodsSku> mtGoodsSkuList = goodsSkuRepository.getBySkuNo(skuNo);

        if (mtGoodsSkuList.size() > 0) {
            return mtGoodsSkuList.get(0);
        }

        return null;
    }

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @throws BusinessCheckException
     */
    @Override
    public GoodsDto getGoodsDetail(Integer id, boolean getDeleteSpec) {
        if (id < 1) {
            return null;
        }
        MtGoods mtGoods = goodsRepository.findOne(id);

        GoodsDto goodsInfo = new GoodsDto();

        if (mtGoods != null) {
            try {
                BeanUtils.copyProperties(goodsInfo, mtGoods);
            } catch (Exception e) {
                goodsInfo.setId(mtGoods.getId());
                goodsInfo.setStoreId(mtGoods.getStoreId());
                goodsInfo.setName(mtGoods.getName());
                goodsInfo.setCateId(mtGoods.getCateId());
                goodsInfo.setGoodsNo(mtGoods.getGoodsNo());
                goodsInfo.setIsSingleSpec(mtGoods.getIsSingleSpec());
                goodsInfo.setLogo(mtGoods.getLogo());
                goodsInfo.setImages(mtGoods.getImages());
                goodsInfo.setStatus(mtGoods.getStatus());
                goodsInfo.setSort(mtGoods.getSort());
            }
        }

        String basePath = env.getProperty("images.upload.url");
        if (StringUtils.isNotEmpty(goodsInfo.getLogo())) {
            goodsInfo.setLogo(basePath + goodsInfo.getLogo());
        }

        // 规格列表
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", id.toString());
        if (getDeleteSpec == false) {
            param.put("EQ_status", StatusEnum.ENABLED.getKey());
        }
        Specification<MtGoodsSpec> specification1 = goodsSpecRepository.buildSpecification(param);
        Sort sort1 = new Sort(Sort.Direction.ASC, "id");
        List<MtGoodsSpec> goodsSpecList = goodsSpecRepository.findAll(specification1, sort1);
        goodsInfo.setSpecList(goodsSpecList);

        // sku列表
        if (goodsInfo.getIsSingleSpec().equals("N")) {
            Specification<MtGoodsSku> specification2 = goodsSkuRepository.buildSpecification(param);
            Sort sort2 = new Sort(Sort.Direction.ASC, "id");
            List<MtGoodsSku> goodsSkuList = goodsSkuRepository.findAll(specification2, sort2);
            goodsInfo.setSkuList(goodsSkuList);
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
    @OperationServiceLog(description = "删除商品")
    public void deleteGoods(Integer id, String operator) throws BusinessCheckException {
        MtGoods cateInfo = this.queryGoodsById(id);
        if (null == cateInfo) {
            return;
        }

        cateInfo.setStatus(StatusEnum.DISABLE.getKey());
        cateInfo.setUpdateTime(new Date());

        goodsRepository.save(cateInfo);
    }

    @Override
    public List<MtGoods> queryGoodsListByParams(Map<String, Object> params) {
        Specification<MtGoods> specification = goodsRepository.buildSpecification(params);

        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtGoods> result = goodsRepository.findAll(specification, sort);

        return result;
    }

    @Override
    public List<MtGoods> getStoreGoodsList(Integer storeId) {
        List<MtGoods> result = goodsRepository.getStoreGoodsList(storeId);
        return result;
    }

    @Override
    public List<GoodsSpecValueDto> getSpecListBySkuId(Integer skuId) {
        if (skuId < 0 || skuId == null) {
            return new ArrayList<>();
        }
        List<GoodsSpecValueDto> result = new ArrayList<>();

        MtGoodsSku goodsSku = goodsSkuRepository.findOne(skuId);
        if (goodsSku == null) {
            return result;
        }

        String specIds = goodsSku.getSpecIds();
        String specIdArr[] = specIds.split("-");
        for (String specId : specIdArr) {
            MtGoodsSpec mtGoodsSpec = goodsSpecRepository.findOne(Integer.parseInt(specId));
            GoodsSpecValueDto dto = new GoodsSpecValueDto();
            dto.setSpecValueId(mtGoodsSpec.getId());
            dto.setSpecName(mtGoodsSpec.getName());
            dto.setSpecValue(mtGoodsSpec.getValue());
            result.add(dto);
        }

        return result;
    }
}
