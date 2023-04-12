package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.CartService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.mapper.MtCartMapper;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.model.MtCart;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 购物车业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class CartServiceImpl extends ServiceImpl<MtCartMapper, MtCart> implements CartService {

    @Resource
    private MtCartMapper mtCartMapper;

    @Resource
    private MtGoodsMapper mtGoodsMapper;

    @Resource
    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 保存购物车
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    public Integer saveCart(MtCart reqDto, String action) throws BusinessCheckException {
        MtCart mtCart = new MtCart();
        Integer cartId = 1;

        // 检查库存是否充足
        if (action.equals("+") || action.equals("=") && reqDto.getNum() > 0) {
            Integer cartNum = 0;
            MtGoods mtGoods = mtGoodsMapper.selectById(reqDto.getGoodsId());
            if (reqDto.getId() != null && reqDto.getId() > 0) {
                MtCart cartInfo = mtCartMapper.selectById(reqDto.getId());
                if (cartInfo != null) {
                    cartNum = cartInfo.getNum();
                }
            }
            if (reqDto.getSkuId() != null && reqDto.getSkuId() > 0) {
                MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(reqDto.getSkuId());
                if (mtGoodsSku != null && mtGoodsSku.getStock() < reqDto.getNum() && cartNum < reqDto.getNum()) {
                    throw new BusinessCheckException(mtGoods.getName() + "库存不足");
                }
            } else {
                if (mtGoods != null && mtGoods.getStock() < reqDto.getNum() && cartNum < reqDto.getNum()) {
                    throw new BusinessCheckException(mtGoods.getName() + "库存不足了");
                }
            }
        }

        if (reqDto.getGoodsId() > 0) {
            mtCart.setGoodsId(reqDto.getGoodsId());
        }
        if (reqDto.getUserId() > 0) {
            mtCart.setUserId(reqDto.getUserId());
        }

        // 数量为0，删除购物车
        if (reqDto.getNum() == 0 && reqDto.getId() > 0) {
            this.removeCart(reqDto.getId()+"");
        } else if (reqDto.getNum() == 0 && action.equals("-")) {
            mtCartMapper.deleteCartItem(reqDto.getUserId(), reqDto.getGoodsId(), reqDto.getSkuId());
        }

        // 校验skuId是否正确
        if (reqDto.getSkuId() != null) {
            if (reqDto.getSkuId() > 0) {
                Map<String, Object> param = new HashMap<>();
                param.put("goods_id", reqDto.getGoodsId().toString());
                param.put("id", reqDto.getSkuId().toString());
                List<MtGoodsSku> skuList = mtGoodsSkuMapper.selectByMap(param);
                // 该skuId不正常
                if (skuList.size() < 1) {
                    reqDto.setSkuId(0);
                }
            }
        }

        mtCart.setStoreId(reqDto.getStoreId() == null ? 0 : reqDto.getStoreId());
        mtCart.setStatus(StatusEnum.ENABLED.getKey());
        mtCart.setUpdateTime(new Date());
        mtCart.setSkuId(reqDto.getSkuId());
        mtCart.setNum(reqDto.getNum());
        mtCart.setHangNo(reqDto.getHangNo());
        mtCart.setIsVisitor(reqDto.getIsVisitor());

        Map<String, Object> params = new HashMap<>();
        params.put("userId", mtCart.getUserId());
        params.put("storeId", mtCart.getStoreId());
        params.put("goodsId", mtCart.getGoodsId());
        params.put("skuId", mtCart.getSkuId());
        params.put("hangNo", reqDto.getHangNo() == null ? "" : reqDto.getHangNo());

        List<MtCart> cartList = this.queryCartListByParams(params);
        if (action.equals("-") && cartList.size() == 0) {
            return cartId;
        }
        // 已存在，仅操作数量增加或减少
        if (cartList.size() > 0) {
            mtCart = cartList.get(0);
            if (action.equals("+")) {
                mtCart.setNum(mtCart.getNum() + reqDto.getNum());
            } else if (action.equals("=")) {
                mtCart.setNum(reqDto.getNum());
            } else {
                Integer num = mtCart.getNum() - 1;
                if (num <= 0) {
                    this.removeCart(mtCart.getId()+"");
                    return mtCart.getId();
                } else {
                    mtCart.setNum(mtCart.getNum() - 1);
                }
            }
            this.updateById(mtCart);
        } else {
            mtCart.setCreateTime(new Date());
            this.save(mtCart);
        }

        return mtCart.getId();
    }

    /**
     * 删除购物车
     *
     * @param cartIds
     * @throws BusinessCheckException
     */
    @Override
    public void removeCart(String cartIds) {
        String[] ids = cartIds.split(",");
        if (ids.length < 1) {
           return;
        }
        for (int i = 0; i < ids.length; i++) {
            MtCart mtCart = mtCartMapper.selectById(Integer.parseInt(ids[i].trim()));
            if (mtCart != null) {
                mtCartMapper.deleteById(mtCart.getId());
            }
        }
    }

    /**
     * 删除挂单购物车
     *
     * @param  hangNo 挂单序号
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除挂单")
    public void removeCartByHangNo(String hangNo) {
        if (hangNo != null && StringUtil.isNotEmpty(hangNo)) {
            mtCartMapper.deleteCartByHangNo(hangNo);
        }
    }

    /**
     * 清空会员购物车
     *
     * @param userId
     * @throws BusinessCheckException
     */
    @Override
    public void clearCart(Integer userId) {
       mtCartMapper.clearCart(userId);
    }

    @Override
    public List<MtCart> queryCartListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        String userId =  params.get("userId") == null ? "" : params.get("userId").toString();
        String ids =  params.get("ids") == null ? "" : params.get("ids").toString();
        String hangNo =  params.get("hangNo") == null ? "" : params.get("hangNo").toString();
        String goodsId =  params.get("goodsId") == null ? "" : params.get("goodsId").toString();
        String skuId =  params.get("skuId") == null ? "" : params.get("skuId").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();

        LambdaQueryWrapper<MtCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MtCart::getStatus, status);

        if (StringUtil.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(MtCart::getUserId, userId);
        }
        if (StringUtil.isNotEmpty(ids)) {
            List<String> idList = Arrays.asList(ids.split(","));
            lambdaQueryWrapper.in(MtCart::getId, idList);
            if (StringUtil.isNotEmpty(hangNo)) {
                lambdaQueryWrapper.eq(MtCart::getHangNo, hangNo);
            }
        } else {
            lambdaQueryWrapper.eq(MtCart::getHangNo, hangNo);
        }
        if (StringUtil.isNotEmpty(goodsId)) {
            lambdaQueryWrapper.eq(MtCart::getGoodsId, goodsId);
        }
        if (StringUtil.isNotEmpty(storeId)) {
            lambdaQueryWrapper.eq(MtCart::getStoreId, storeId);
        }
        if (StringUtil.isNotEmpty(skuId)) {
            lambdaQueryWrapper.eq(MtCart::getSkuId, skuId);
        }

        List<MtCart> result = mtCartMapper.selectList(lambdaQueryWrapper);
        return result;
    }

    /**
     * 更新购物车
     *
     * @param  cartId  ID
     * @param  hangNo 挂单序号
     * @param  isVisitor 是否游客
     * @return
     */
    @Override
    @OperationServiceLog(description = "执行挂单")
    public MtCart setHangNo(Integer cartId, String hangNo, String isVisitor) {
        MtCart mtCart = mtCartMapper.selectById(cartId);
        if (mtCart != null) {
            mtCart.setHangNo(hangNo);
            mtCart.setUpdateTime(new Date());
            mtCart.setIsVisitor(isVisitor);
            this.updateById(mtCart);
        }
        return mtCart;
    }
}
