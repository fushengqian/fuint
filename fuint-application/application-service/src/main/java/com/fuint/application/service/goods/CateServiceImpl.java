package com.fuint.application.service.goods;

import com.fuint.application.dao.entities.MtGoodsCate;
import com.fuint.application.dao.repositories.MtGoodsCateRepository;
import com.fuint.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 商品分类业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class CateServiceImpl implements CateService {

    private static final Logger log = LoggerFactory.getLogger(CateServiceImpl.class);

    @Autowired
    private MtGoodsCateRepository cateRepository;

    /**
     * 分页查询分类列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtGoodsCate> queryCateListByPagination(PaginationRequest paginationRequest) {
        PaginationResponse<MtGoodsCate> paginationResponse = cateRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加商品分类
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加商品分类")
    public MtGoodsCate addCate(MtGoodsCate reqDto) throws BusinessCheckException {
        MtGoodsCate mtCate = new MtGoodsCate();
        if (null != reqDto.getId()) {
            mtCate.setId(reqDto.getId());
        }
        mtCate.setName(reqDto.getName());
        mtCate.setStatus(StatusEnum.ENABLED.getKey());
        mtCate.setLogo(reqDto.getLogo());
        mtCate.setDescription(reqDto.getDescription());
        mtCate.setOperator(reqDto.getOperator());

        mtCate.setUpdateTime(new Date());
        mtCate.setCreateTime(new Date());

        return cateRepository.save(mtCate);
    }

    /**
     * 根据ID获取分类信息
     *
     * @param id 分类ID
     * @throws BusinessCheckException
     */
    @Override
    public MtGoodsCate queryCateById(Integer id) {
        return cateRepository.findOne(id);
    }

    /**
     * 根据ID删除分类信息
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除商品分类")
    public void deleteCate(Integer id, String operator) throws BusinessCheckException {
        MtGoodsCate cateInfo = this.queryCateById(id);
        if (null == cateInfo) {
            return;
        }

        cateInfo.setStatus(StatusEnum.DISABLE.getKey());
        cateInfo.setUpdateTime(new Date());

        cateRepository.save(cateInfo);
    }

    /**
     * 修改分类
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改商品分类")
    public MtGoodsCate updateCate(MtGoodsCate reqDto) throws BusinessCheckException {
        MtGoodsCate mtCate = this.queryCateById(reqDto.getId());
        if (null == mtCate) {
            log.error("该分类状态异常");
            throw new BusinessCheckException("该分类状态异常");
        }

        mtCate.setId(reqDto.getId());
        if (reqDto.getLogo() != null) {
            mtCate.setLogo(reqDto.getLogo());
        }
        if (reqDto.getName() != null) {
            mtCate.setName(reqDto.getName());
        }
        if (reqDto.getDescription() != null) {
            mtCate.setDescription(reqDto.getDescription());
        }
        mtCate.setUpdateTime(new Date());
        if (StringUtil.isNotEmpty(reqDto.getOperator())) {
            mtCate.setOperator(reqDto.getOperator());
        }
        if (reqDto.getStatus() != null) {
            mtCate.setStatus(reqDto.getStatus());
        }
        if (reqDto.getSort() != null) {
            mtCate.setSort(reqDto.getSort());
        }

        return cateRepository.save(mtCate);
    }

    @Override
    public List<MtGoodsCate> queryCateListByParams(Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();

        String status =  params.get("EQ_status") == null ? StatusEnum.ENABLED.getKey(): params.get("EQ_status").toString();
        param.put("EQ_status", status);

        Specification<MtGoodsCate> specification = cateRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<MtGoodsCate> result = cateRepository.findAll(specification, sort);

        return result;
    }
}
