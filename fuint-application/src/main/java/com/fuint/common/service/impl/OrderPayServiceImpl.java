package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.OrderPayService;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.mapper.MtOrderPayMapper;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtOrderPay;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单支付单接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class OrderPayServiceImpl extends ServiceImpl<MtOrderPayMapper, MtOrderPay> implements OrderPayService {

    private static final Logger logger = LoggerFactory.getLogger(OrderPayServiceImpl.class);

    private MtOrderPayMapper mtOrderPayMapper;

    /**
     * 创建支付单，生成支付单号
     *
     * @param orderInfo 订单信息
     * @param amount 本次应付金额
     * @return
     * */
    @Override
    public MtOrderPay createPayLog(MtOrder orderInfo, BigDecimal amount) throws BusinessCheckException {
        if (orderInfo == null || orderInfo.getId() == null) {
            throw new BusinessCheckException("订单信息不能为空");
        }
        String paySn = generatePaySn(String.valueOf(orderInfo.getUserId() == null ? 0 : orderInfo.getUserId()));

        MtOrderPay mtOrderPay = new MtOrderPay();
        mtOrderPay.setOrderId(orderInfo.getId());
        mtOrderPay.setOrderSn(orderInfo.getOrderSn());
        mtOrderPay.setPaySn(paySn);
        mtOrderPay.setMerchantId(orderInfo.getMerchantId());
        mtOrderPay.setStoreId(orderInfo.getStoreId());
        mtOrderPay.setUserId(orderInfo.getUserId());
        mtOrderPay.setPayType(orderInfo.getPayType());
        mtOrderPay.setAmount(amount);
        mtOrderPay.setPayStatus(PayStatusEnum.WAIT.getKey());
        mtOrderPay.setCreateTime(new Date());
        mtOrderPay.setUpdateTime(new Date());
        mtOrderPayMapper.insert(mtOrderPay);

        logger.info("创建订单支付单 orderSn={}, paySn={}, amount={}", orderInfo.getOrderSn(), paySn, amount);
        return mtOrderPay;
    }

    /**
     * 根据支付单号获取支付单
     *
     * @param paySn 支付单号
     * @return
     * */
    @Override
    public MtOrderPay getPayLogByPaySn(String paySn) {
        if (StringUtil.isEmpty(paySn)) {
            return null;
        }
        return mtOrderPayMapper.findByPaySn(paySn);
    }

    /**
     * 支付单置为已支付
     *
     * @param paySn 支付单号
     * @return
     * */
    @Override
    public Boolean updatePayLogPaid(String paySn) {
        MtOrderPay mtOrderPay = getPayLogByPaySn(paySn);
        if (mtOrderPay == null) {
            return false;
        }
        mtOrderPay.setPayStatus(PayStatusEnum.SUCCESS.getKey());
        mtOrderPay.setUpdateTime(new Date());
        return mtOrderPayMapper.updateById(mtOrderPay) > 0;
    }

    /**
     * 生成支付单号，保证在支付单表内唯一
     *
     * @param userId 会员ID
     * @return
     * */
    private String generatePaySn(String userId) {
        String paySn;
        int retry = 0;
        do {
            paySn = CommonUtil.createOrderSN(userId);
            retry++;
        } while (getPayLogByPaySn(paySn) != null && retry < 5);
        return paySn;
    }
}
