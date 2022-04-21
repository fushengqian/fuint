package com.fuint.application.service.cart;

import com.fuint.application.dao.entities.MtCart;
import com.fuint.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 购物车业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface CartService {

    /**
     * 保存购物车
     *
     * @param reqDto
     * @param action + or -
     * @throws BusinessCheckException
     */
    Integer saveCart(MtCart reqDto, String action) throws BusinessCheckException;

    /**
     * 删除购物车
     *
     * @param cartIds 购物车ID
     * @throws BusinessCheckException
     */
    void removeCart(String cartIds) throws BusinessCheckException;

    /**
     * 清空会员购物车
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     */
    void clearCart(Integer userId) throws BusinessCheckException;

    /**
     * 根据条件查找
     * */
    List<MtCart> queryCartListByParams(Map<String, Object> params) throws BusinessCheckException;
}
