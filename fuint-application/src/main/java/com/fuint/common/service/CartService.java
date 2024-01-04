package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtCart;
import java.util.List;
import java.util.Map;

/**
 * 购物车业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CartService extends IService<MtCart> {

    /**
     * 切换购物车给会员
     *
     * @param userId
     * @param cartIds
     * @return
     * */
    Boolean switchCartIds(Integer userId, String cartIds) throws BusinessCheckException;

    /**
     * 保存购物车
     *
     * @param reqDto
     * @param action + or - or =
     * @throws BusinessCheckException
     * @return
     */
    Integer saveCart(MtCart reqDto, String action) throws BusinessCheckException;

    /**
     * 删除购物车
     *
     * @param cartIds 购物车ID
     * @throws BusinessCheckException
     * @return
     */
    void removeCart(String cartIds) throws BusinessCheckException;

    /**
     * 删除购物车
     *
     * @param  hangNo 挂单序号
     * @throws BusinessCheckException
     * @return
     */
    void removeCartByHangNo(String hangNo) throws BusinessCheckException;

    /**
     * 清空会员购物车
     *
     * @param userId 会员ID
     * @throws BusinessCheckException
     * @return
     */
    void clearCart(Integer userId) throws BusinessCheckException;

    /**
     * 根据条件查找
     *
     * @param params 查询参数
     * @return
     * */
    List<MtCart> queryCartListByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 挂单
     *
     * @param  cartId 购物车ID
     * @param  hangNo 挂单序号
     * @param  isVisitor 是否游客
     * @return
     */
    MtCart setHangNo(Integer cartId, String hangNo, String isVisitor) throws BusinessCheckException;
}
