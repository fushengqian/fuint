package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 库存业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class StockServiceImpl extends ServiceImpl<MtStockMapper, MtStock> implements StockService {

    @Resource
    private MtStockMapper mtStockMapper;

    @Resource
    private MtStockItemMapper mtStockItemMapper;

    /**
     * 分页查询库存管理记录列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtStock> queryStockListByPagination(PaginationRequest paginationRequest) {
        Page<MtStock> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtStock> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtStock::getStatus, StatusEnum.DISABLE.getKey());

        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtStock::getStatus, status);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtStock::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByDesc(MtStock::getId);
        List<MtStock> dataList = mtStockMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtStock> paginationResponse = new PaginationResponse(pageImpl, MtStock.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 新增库存管理记录
     *
     * @param  params
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject addStock(MtStock params) {
        MtStock mtStock = new MtStock();

        mtStock.setStoreId(params.getStoreId());
        mtStock.setStatus(StatusEnum.ENABLED.getKey());
        mtStock.setType(params.getType());
        Date createTime = new Date();
        mtStock.setCreateTime(createTime);
        mtStock.setUpdateTime(createTime);
        mtStock.setDescription(params.getDescription());
        this.save(mtStock);

        ResponseObject result = new ResponseObject(200, "", mtStock);
        return result;
    }

    /**
     * 删除库存管理记录
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除商品分类")
    public void delete(Integer id, String operator) throws BusinessCheckException {
        MtStock mtStock = mtStockMapper.selectById(id);
        if (mtStock == null) {
            return;
        }
        mtStock.setStatus(StatusEnum.DISABLE.getKey());
        mtStock.setUpdateTime(new Date());
        mtStock.setOperator(operator);
        this.updateById(mtStock);
    }

    /**
     * 根据ID获取库存管理记录
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    @Override
    public MtStock queryStockById(Long id) {
        return mtStockMapper.selectById(id.intValue());
    }

    @Override
    public List<MtStockItem> queryItemByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        List<MtStockItem> result = mtStockItemMapper.selectByMap(params);
        return result;
    }
}
