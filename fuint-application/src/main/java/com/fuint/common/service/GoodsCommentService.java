package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.goods.GoodsCommentDto;
import com.fuint.common.param.GoodsCommentListParam;
import com.fuint.common.param.GoodsCommentSaveParam;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtGoodsComment;
import java.util.List;
import java.util.Map;

/**
 * 商品评价业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface GoodsCommentService extends IService<MtGoodsComment> {

    /**
     * 分页查询商品评价列表
     *
     * @param  param 查询参数
     * @return
     * */
    PaginationResponse<GoodsCommentDto> queryCommentListByPagination(GoodsCommentListParam param);

    /**
     * 会员提交商品评价
     *
     * @param  param 评价参数
     * @param  userId 会员ID
     * @throws BusinessCheckException
     * @return
     * */
    MtGoodsComment saveComment(GoodsCommentSaveParam param, Integer userId) throws BusinessCheckException;

    /**
     * 商品评价统计信息（评分均值、评价数、好评率等）
     *
     * @param  goodsId 商品ID
     * @return
     * */
    Map<String, Object> getCommentStatistics(Integer goodsId);

    /**
     * 获取订单内各商品的评价情况（会员端待评价入口）
     *
     * @param  orderId 订单ID
     * @param  userId 会员ID
     * @throws BusinessCheckException
     * @return
     * */
    List<Map<String, Object>> getOrderGoodsCommentList(Integer orderId, Integer userId) throws BusinessCheckException;

    /**
     * 查询我的评价列表
     *
     * @param  param 查询参数
     * @param  userId 会员ID
     * @return
     * */
    PaginationResponse<GoodsCommentDto> getMyCommentList(GoodsCommentListParam param, Integer userId);

    /**
     * 商家回复评价
     *
     * @param  commentId 评价ID
     * @param  accountId 操作账号ID
     * @param  content 回复内容
     * @throws BusinessCheckException
     * @return
     * */
    MtGoodsComment replyComment(Integer commentId, Integer accountId, String content) throws BusinessCheckException;

    /**
     * 更新评价状态（展示/隐藏/删除）
     *
     * @param  commentId 评价ID
     * @param  status 状态
     * @throws BusinessCheckException
     * @return
     * */
    Boolean updateStatus(Integer commentId, String status) throws BusinessCheckException;

    /**
     * 会员删除自己的评价
     *
     * @param  commentId 评价ID
     * @param  userId 会员ID
     * @throws BusinessCheckException
     * @return
     * */
    Boolean deleteMyComment(Integer commentId, Integer userId) throws BusinessCheckException;
}
