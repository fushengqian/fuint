package com.fuint.application.service.cart;

import com.fuint.application.dao.entities.MtCart;
import com.fuint.application.dao.entities.MtGoodsSku;
import com.fuint.application.dao.repositories.MtCartRepository;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import org.springframework.data.jpa.domain.Specification;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 购物车业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private MtCartRepository cartRepository;

    @Autowired
    private MtGoodsSkuRepository goodsSkuRepository;

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
            cartRepository.deleteCartItem(reqDto.getUserId(), reqDto.getGoodsId(), reqDto.getSkuId());
        }

        // 校验skuId是否正确
        if (reqDto.getSkuId() != null) {
            if (reqDto.getSkuId() > 0) {
                Map<String, Object> param = new HashMap<>();
                param.put("EQ_goodsId", reqDto.getGoodsId().toString());
                param.put("EQ_id", reqDto.getSkuId().toString());
                Specification<MtGoodsSku> specification = goodsSkuRepository.buildSpecification(param);
                Sort sort = new Sort(Sort.Direction.ASC, "id");
                List<MtGoodsSku> skuList = goodsSkuRepository.findAll(specification, sort);
                // 该skuId不正常
                if (skuList.size() < 1) {
                    reqDto.setSkuId(0);
                }
            }
        }

        mtCart.setStatus(StatusEnum.ENABLED.getKey());
        mtCart.setUpdateTime(new Date());
        mtCart.setSkuId(reqDto.getSkuId());
        mtCart.setNum(reqDto.getNum());

        Map<String, Object> params = new HashMap<>();
        params.put("EQ_userId", mtCart.getUserId().toString());
        params.put("EQ_goodsId", mtCart.getGoodsId().toString());
        params.put("EQ_skuId", mtCart.getSkuId().toString());

        List<MtCart> cartList = this.queryCartListByParams(params);
        if (action.equals("-") && cartList.size() == 0) {
            return cartId;
        }

        if (cartList.size() > 0) {
            mtCart = cartList.get(0);
            if (action.equals("+")) {
                mtCart.setNum(mtCart.getNum() + reqDto.getNum());
            } else {
                Integer num = mtCart.getNum() - 1;
                if (num <= 0) {
                    this.removeCart(mtCart.getId()+"");
                    return mtCart.getId();
                } else {
                    mtCart.setNum(mtCart.getNum() - 1);
                }
            }
        } else {
            mtCart.setCreateTime(new Date());
        }

        MtCart cartInfo = cartRepository.save(mtCart);

        return cartInfo.getId();
    }

    /**
     * 删除购物车
     *
     * @param cartIds
     * @throws BusinessCheckException
     */
    @Override
    public void removeCart(String cartIds) throws BusinessCheckException {
        String[] ids = cartIds.split(",");
        if (ids.length < 1) {
           return;
        }

        for (int i = 0; i < ids.length; i++) {
            MtCart mtCart = cartRepository.findOne(Integer.parseInt(ids[i].trim()));
            if (mtCart != null) {
                cartRepository.delete(mtCart.getId());
            }
        }
    }

    /**
     * 清空会员购物车
     *
     * @param userId
     * @throws BusinessCheckException
     */
    @Override
    public void clearCart(Integer userId) throws BusinessCheckException {
       cartRepository.clearCart(userId);
    }

    @Override
    public List<MtCart> queryCartListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        params.put("EQ_status", status);

        Specification<MtCart> specification = cartRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtCart> result = cartRepository.findAll(specification, sort);

        return result;
    }
}
