package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.BalanceDto;
import com.fuint.common.param.BalancePage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtBalance;
import java.util.List;

/**
 * 余额业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BalanceService extends IService<MtBalance> {

    /**
     * 分页查询余额列表
     *
     * @param balancePage
     * @return
     */
    PaginationResponse<BalanceDto> queryBalanceListByPagination(BalancePage balancePage) throws BusinessCheckException;

    /**
     * 添加余额记录
     *
     * @param reqDto
     * @param updateBalance
     * @throws BusinessCheckException
     */
    Boolean addBalance(MtBalance reqDto, Boolean updateBalance) throws BusinessCheckException;

    /**
     * 发放余额
     *
     * @param accountInfo 账号信息
     * @param object 发放对象，all全部
     * @param userIds 会员ID
     * @param amount 发放金额
     * @param remark 备注
     * @return
     */
    void distribute(AccountInfo accountInfo, String object, String userIds, String amount, String remark) throws BusinessCheckException;

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    List<MtBalance> getBalanceListByOrderSn(String orderSn) throws BusinessCheckException;
}
