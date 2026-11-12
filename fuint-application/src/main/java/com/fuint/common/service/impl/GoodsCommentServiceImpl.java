package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.goods.GoodsCommentDto;
import com.fuint.common.enums.AuditStatusEnum;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.GoodsCommentListParam;
import com.fuint.common.param.GoodsCommentSaveParam;
import com.fuint.common.service.GoodsCommentService;
import com.fuint.common.service.OrderService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtGoodsCommentMapper;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtOrderGoodsMapper;
import com.fuint.repository.mapper.MtOrderMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsComment;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtOrderGoods;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品评价业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class GoodsCommentServiceImpl extends ServiceImpl<MtGoodsCommentMapper, MtGoodsComment> implements GoodsCommentService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsCommentServiceImpl.class);

    private MtGoodsCommentMapper mtGoodsCommentMapper;

    private MtGoodsMapper mtGoodsMapper;

    private MtOrderGoodsMapper mtOrderGoodsMapper;

    private MtUserMapper mtUserMapper;

    private MtOrderMapper mtOrderMapper;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 分页查询商品评价列表
     *
     * @param param 查询参数
     * @return
     */
    @Override
    public PaginationResponse<GoodsCommentDto> queryCommentListByPagination(GoodsCommentListParam param) {
        LambdaQueryWrapper<MtGoodsComment> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtGoodsComment::getStatus, StatusEnum.DISABLE.getKey());

        Integer merchantId = param.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtGoodsComment::getMerchantId, merchantId);
        }
        Integer storeId = param.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtGoodsComment::getStoreId, storeId);
        }
        Integer goodsId = param.getGoodsId();
        if (goodsId != null && goodsId > 0) {
            lambdaQueryWrapper.eq(MtGoodsComment::getGoodsId, goodsId);
        }
        Integer score = param.getScore();
        if (score != null && score > 0) {
            lambdaQueryWrapper.eq(MtGoodsComment::getScore, score);
        }
        String auditStatus = param.getAuditStatus();
        if (StringUtil.isNotEmpty(auditStatus)) {
            lambdaQueryWrapper.eq(MtGoodsComment::getAuditStatus, auditStatus);
        }
        String status = param.getStatus();
        if (StringUtil.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtGoodsComment::getStatus, status);
        }
        String replyStatus = param.getReplyStatus();
        if (YesOrNoEnum.YES.getKey().equals(replyStatus)) {
            lambdaQueryWrapper.isNotNull(MtGoodsComment::getReplyContent);
        } else if (YesOrNoEnum.NO.getKey().equals(replyStatus)) {
            lambdaQueryWrapper.and(qw -> qw.isNull(MtGoodsComment::getReplyContent).or().eq(MtGoodsComment::getReplyContent, ""));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtil.isNotEmpty(param.getStartTime())) {
                String startTime = param.getStartTime();
                if (startTime.length() == 10) {
                    startTime = startTime + " 00:00:00";
                }
                lambdaQueryWrapper.ge(MtGoodsComment::getCreateTime, sdf.parse(startTime));
            }
            if (StringUtil.isNotEmpty(param.getEndTime())) {
                String endTime = param.getEndTime();
                if (endTime.length() == 10) {
                    endTime = endTime + " 23:59:59";
                }
                lambdaQueryWrapper.le(MtGoodsComment::getCreateTime, sdf.parse(endTime));
            }
        } catch (Exception e) {
            logger.error("评价列表时间参数解析失败：{}", e.getMessage());
        }
        // 按商品名称筛选
        String goodsName = param.getGoodsName();
        if (StringUtil.isNotEmpty(goodsName)) {
            LambdaQueryWrapper<MtGoods> goodsWrapper = Wrappers.lambdaQuery();
            goodsWrapper.like(MtGoods::getName, goodsName);
            goodsWrapper.select(MtGoods::getId);
            List<Integer> goodsIds = mtGoodsMapper.selectList(goodsWrapper).stream().map(MtGoods::getId).collect(Collectors.toList());
            if (goodsIds.size() == 0) {
                return buildEmptyResponse(param);
            }
            lambdaQueryWrapper.in(MtGoodsComment::getGoodsId, goodsIds);
        }
        // 按订单号筛选（模糊匹配订单表订单号，再按订单ID过滤）
        String orderSn = param.getOrderSn();
        if (StringUtil.isNotEmpty(orderSn)) {
            LambdaQueryWrapper<MtOrder> orderWrapper = Wrappers.lambdaQuery();
            orderWrapper.like(MtOrder::getOrderSn, orderSn);
            orderWrapper.select(MtOrder::getId);
            List<Integer> orderIds = mtOrderMapper.selectList(orderWrapper).stream().map(MtOrder::getId).collect(Collectors.toList());
            if (orderIds.size() == 0) {
                return buildEmptyResponse(param);
            }
            lambdaQueryWrapper.in(MtGoodsComment::getOrderId, orderIds);
        }
        // 按会员号筛选（模糊匹配会员表会员号，再按会员ID过滤）
        String userNo = param.getUserNo();
        if (StringUtil.isNotEmpty(userNo)) {
            LambdaQueryWrapper<MtUser> userWrapper = Wrappers.lambdaQuery();
            userWrapper.like(MtUser::getUserNo, userNo);
            userWrapper.select(MtUser::getId);
            List<Integer> userIds = mtUserMapper.selectList(userWrapper).stream().map(MtUser::getId).collect(Collectors.toList());
            if (userIds.size() == 0) {
                return buildEmptyResponse(param);
            }
            lambdaQueryWrapper.in(MtGoodsComment::getUserId, userIds);
        }
        lambdaQueryWrapper.orderByDesc(MtGoodsComment::getId);

        Page<MtGoodsComment> pageHelper = PageHelper.startPage(param.getPage(), param.getPageSize());
        List<MtGoodsComment> commentList = mtGoodsCommentMapper.selectList(lambdaQueryWrapper);
        List<GoodsCommentDto> dataList = fillCommentDto(commentList);

        PageRequest pageRequest = PageRequest.of(param.getPage(), param.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<GoodsCommentDto> paginationResponse = new PaginationResponse(pageImpl, GoodsCommentDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 会员提交商品评价
     *
     * @param param 评价参数
     * @param userId 会员ID
     * @return
     */
    @Override
    public MtGoodsComment saveComment(GoodsCommentSaveParam param, Integer userId) throws BusinessCheckException {
        if (param.getOrderId() == null || param.getOrderGoodsId() == null) {
            throw new BusinessCheckException("订单信息不能为空");
        }
        if (param.getScore() == null || param.getScore() < 1 || param.getScore() > 5) {
            throw new BusinessCheckException("评分只能是1-5星");
        }
        String content = param.getContent() == null ? "" : param.getContent().trim();
        if (content.length() > 500) {
            throw new BusinessCheckException("评价内容不能超过500字");
        }
        List<String> images = param.getImages() == null ? new ArrayList<>() : param.getImages();
        if (images.size() > 6) {
            throw new BusinessCheckException("评价图片最多上传6张");
        }

        MtOrder mtOrder = orderService.getOrderInfo(param.getOrderId());
        if (mtOrder == null) {
            throw new BusinessCheckException("该订单不存在");
        }
        if (!mtOrder.getUserId().equals(userId)) {
            throw new BusinessCheckException("无权评价该订单");
        }
        if (!OrderStatusEnum.COMPLETE.getKey().equals(mtOrder.getStatus())) {
            throw new BusinessCheckException("订单完成后才能评价");
        }

        MtOrderGoods mtOrderGoods = mtOrderGoodsMapper.selectById(param.getOrderGoodsId());
        if (mtOrderGoods == null || !mtOrderGoods.getOrderId().equals(param.getOrderId())) {
            throw new BusinessCheckException("订单商品信息不存在");
        }

        // 同一订单商品只能评价一次
        LambdaQueryWrapper<MtGoodsComment> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(MtGoodsComment::getOrderGoodsId, param.getOrderGoodsId());
        checkWrapper.ne(MtGoodsComment::getStatus, StatusEnum.DISABLE.getKey());
        if (mtGoodsCommentMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessCheckException("该商品已评价过，请勿重复评价");
        }

        MtGoodsComment mtGoodsComment = new MtGoodsComment();
        mtGoodsComment.setMerchantId(mtOrder.getMerchantId());
        mtGoodsComment.setStoreId(mtOrder.getStoreId());
        mtGoodsComment.setUserId(userId);
        mtGoodsComment.setOrderId(param.getOrderId());
        mtGoodsComment.setOrderGoodsId(param.getOrderGoodsId());
        mtGoodsComment.setGoodsId(mtOrderGoods.getGoodsId());
        mtGoodsComment.setScore(param.getScore());
        mtGoodsComment.setContent(content);
        if (images.size() > 0) {
            mtGoodsComment.setImages(String.join(",", images));
        }
        mtGoodsComment.setIsAnonymous(YesOrNoEnum.YES.getKey().equals(param.getIsAnonymous()) ? YesOrNoEnum.YES.getKey() : YesOrNoEnum.NO.getKey());
        // 默认免审直接通过，开启审核的商户可在后台调整审核状态
        mtGoodsComment.setAuditStatus(AuditStatusEnum.PASS.getKey());
        mtGoodsComment.setStatus(StatusEnum.ENABLED.getKey());
        mtGoodsComment.setCreateTime(new Date());
        mtGoodsComment.setUpdateTime(new Date());
        mtGoodsCommentMapper.insert(mtGoodsComment);
        logger.info("会员提交商品评价, commentId = {}, userId = {}, orderGoodsId = {}", mtGoodsComment.getId(), userId, param.getOrderGoodsId());

        return mtGoodsComment;
    }

    /**
     * 商品评价统计信息
     *
     * @param goodsId 商品ID
     * @return
     */
    @Override
    public Map<String, Object> getCommentStatistics(Integer goodsId) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<MtGoodsComment> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtGoodsComment::getGoodsId, goodsId);
        lambdaQueryWrapper.eq(MtGoodsComment::getStatus, StatusEnum.ENABLED.getKey());
        lambdaQueryWrapper.ne(MtGoodsComment::getAuditStatus, AuditStatusEnum.REJECT.getKey());

        List<MtGoodsComment> commentList = mtGoodsCommentMapper.selectList(lambdaQueryWrapper);
        long total = commentList.size();
        long goodCount = commentList.stream().filter(c -> c.getScore() >= 4).count();
        long midCount = commentList.stream().filter(c -> c.getScore() == 3).count();
        long badCount = commentList.stream().filter(c -> c.getScore() <= 2).count();
        long hasImageCount = commentList.stream().filter(c -> StringUtil.isNotEmpty(c.getImages())).count();

        Double avgScore = 0.0;
        if (total > 0) {
            avgScore = Math.round(commentList.stream().mapToInt(MtGoodsComment::getScore).average().orElse(0) * 10) / 10.0;
        }

        result.put("total", total);
        result.put("avgScore", avgScore);
        result.put("goodCount", goodCount);
        result.put("midCount", midCount);
        result.put("badCount", badCount);
        result.put("hasImageCount", hasImageCount);
        result.put("goodRate", total > 0 ? Math.round(goodCount * 1000.0 / total) / 10.0 : 0);
        result.put("starCount", getStarCount(commentList));

        return result;
    }

    /**
     * 获取订单内各商品的评价情况
     *
     * @param orderId 订单ID
     * @param userId 会员ID
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderGoodsCommentList(Integer orderId, Integer userId) throws BusinessCheckException {
        MtOrder mtOrder = orderService.getOrderInfo(orderId);
        if (mtOrder == null) {
            throw new BusinessCheckException("该订单不存在");
        }
        if (!mtOrder.getUserId().equals(userId)) {
            throw new BusinessCheckException("无权查看该订单");
        }

        LambdaQueryWrapper<MtOrderGoods> orderGoodsWrapper = Wrappers.lambdaQuery();
        orderGoodsWrapper.eq(MtOrderGoods::getOrderId, orderId);
        orderGoodsWrapper.ne(MtOrderGoods::getStatus, StatusEnum.DISABLE.getKey());
        List<MtOrderGoods> orderGoodsList = mtOrderGoodsMapper.selectList(orderGoodsWrapper);

        List<Integer> goodsIds = orderGoodsList.stream().map(MtOrderGoods::getGoodsId).collect(Collectors.toList());
        Map<Integer, MtGoods> goodsMap = new HashMap<>();
        if (goodsIds.size() > 0) {
            List<MtGoods> goodsList = mtGoodsMapper.selectBatchIds(goodsIds);
            for (MtGoods mtGoods : goodsList) {
                 goodsMap.put(mtGoods.getId(), mtGoods);
            }
        }

        LambdaQueryWrapper<MtGoodsComment> commentWrapper = Wrappers.lambdaQuery();
        commentWrapper.eq(MtGoodsComment::getOrderId, orderId);
        commentWrapper.eq(MtGoodsComment::getUserId, userId);
        commentWrapper.ne(MtGoodsComment::getStatus, StatusEnum.DISABLE.getKey());
        Map<Integer, MtGoodsComment> commentMap = new HashMap<>();
        for (MtGoodsComment comment : mtGoodsCommentMapper.selectList(commentWrapper)) {
             commentMap.put(comment.getOrderGoodsId(), comment);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (MtOrderGoods mtOrderGoods : orderGoodsList) {
             Map<String, Object> item = new HashMap<>();
             item.put("orderGoodsId", mtOrderGoods.getId());
             item.put("goodsId", mtOrderGoods.getGoodsId());
             item.put("num", mtOrderGoods.getNum());
             item.put("price", mtOrderGoods.getPrice());
             MtGoods mtGoods = goodsMap.get(mtOrderGoods.getGoodsId());
             if (mtGoods != null) {
                 item.put("goodsName", mtGoods.getName());
                 item.put("goodsLogo", mtGoods.getLogo());
             }
             MtGoodsComment comment = commentMap.get(mtOrderGoods.getId());
             item.put("commented", comment != null);
             if (comment != null) {
                 item.put("comment", fillCommentDto(Collections.singletonList(comment)).get(0));
             }
             item.put("canComment", OrderStatusEnum.COMPLETE.getKey().equals(mtOrder.getStatus()) && comment == null);
             dataList.add(item);
        }

        return dataList;
    }

    /**
     * 查询我的评价列表
     *
     * @param param 查询参数
     * @param userId 会员ID
     * @return
     */
    @Override
    public PaginationResponse<GoodsCommentDto> getMyCommentList(GoodsCommentListParam param, Integer userId) {
        LambdaQueryWrapper<MtGoodsComment> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtGoodsComment::getUserId, userId);
        lambdaQueryWrapper.ne(MtGoodsComment::getStatus, StatusEnum.DISABLE.getKey());
        lambdaQueryWrapper.orderByDesc(MtGoodsComment::getId);

        Page<MtGoodsComment> pageHelper = PageHelper.startPage(param.getPage(), param.getPageSize());
        List<MtGoodsComment> commentList = mtGoodsCommentMapper.selectList(lambdaQueryWrapper);
        List<GoodsCommentDto> dataList = fillCommentDto(commentList);

        PageRequest pageRequest = PageRequest.of(param.getPage(), param.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<GoodsCommentDto> paginationResponse = new PaginationResponse(pageImpl, GoodsCommentDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 商家回复评价
     *
     * @param commentId 评价ID
     * @param accountId 操作账号ID
     * @param content 回复内容
     * @return
     */
    @Override
    public MtGoodsComment replyComment(Integer commentId, Integer accountId, String content) throws BusinessCheckException {
        if (commentId == null || commentId <= 0) {
            throw new BusinessCheckException("评价ID不能为空");
        }
        String replyContent = content == null ? "" : content.trim();
        if (StringUtil.isEmpty(replyContent)) {
            throw new BusinessCheckException("回复内容不能为空");
        }
        if (replyContent.length() > 500) {
            throw new BusinessCheckException("回复内容不能超过500字");
        }
        MtGoodsComment mtGoodsComment = mtGoodsCommentMapper.selectById(commentId);
        if (mtGoodsComment == null || StatusEnum.DISABLE.getKey().equals(mtGoodsComment.getStatus())) {
            throw new BusinessCheckException("该评价不存在");
        }
        mtGoodsComment.setReplyContent(replyContent);
        mtGoodsComment.setReplyAccountId(accountId);
        mtGoodsComment.setReplyTime(new Date());
        mtGoodsCommentMapper.updateById(mtGoodsComment);
        logger.info("商家回复评价, commentId = {}, accountId = {}", commentId, accountId);

        return mtGoodsComment;
    }

    /**
     * 更新评价状态
     *
     * @param commentId 评价ID
     * @param status 状态
     * @return
     */
    @Override
    public Boolean updateStatus(Integer commentId, String status) throws BusinessCheckException {
        MtGoodsComment mtGoodsComment = mtGoodsCommentMapper.selectById(commentId);
        if (mtGoodsComment == null) {
            throw new BusinessCheckException("该评价不存在");
        }
        mtGoodsComment.setStatus(status);
        mtGoodsComment.setUpdateTime(new Date());
        mtGoodsCommentMapper.updateById(mtGoodsComment);
        return true;
    }

    /**
     * 会员删除自己的评价
     *
     * @param commentId 评价ID
     * @param userId 会员ID
     * @return
     */
    @Override
    public Boolean deleteMyComment(Integer commentId, Integer userId) throws BusinessCheckException {
        MtGoodsComment mtGoodsComment = mtGoodsCommentMapper.selectById(commentId);
        if (mtGoodsComment == null || StatusEnum.DISABLE.getKey().equals(mtGoodsComment.getStatus())) {
            throw new BusinessCheckException("该评价不存在");
        }
        if (!mtGoodsComment.getUserId().equals(userId)) {
            throw new BusinessCheckException("无权删除该评价");
        }
        mtGoodsComment.setStatus(StatusEnum.DISABLE.getKey());
        mtGoodsComment.setUpdateTime(new Date());
        mtGoodsCommentMapper.updateById(mtGoodsComment);
        return true;
    }

    /**
     * 组装评价DTO（补充会员、商品信息，匿名脱敏）
     *
     * @param commentList 评价列表
     * @return
     */
    private List<GoodsCommentDto> fillCommentDto(List<MtGoodsComment> commentList) {
        List<GoodsCommentDto> dataList = new ArrayList<>();
        if (commentList == null || commentList.size() == 0) {
            return dataList;
        }
        List<Integer> userIds = commentList.stream().map(MtGoodsComment::getUserId).distinct().collect(Collectors.toList());
        Map<Integer, MtUser> userMap = new HashMap<>();
        if (userIds.size() > 0) {
            for (MtUser mtUser : mtUserMapper.selectBatchIds(userIds)) {
                 userMap.put(mtUser.getId(), mtUser);
            }
        }
        List<Integer> goodsIds = commentList.stream().map(MtGoodsComment::getGoodsId).distinct().collect(Collectors.toList());
        Map<Integer, MtGoods> goodsMap = new HashMap<>();
        if (goodsIds.size() > 0) {
            for (MtGoods mtGoods : mtGoodsMapper.selectBatchIds(goodsIds)) {
                 goodsMap.put(mtGoods.getId(), mtGoods);
            }
        }
        List<Integer> orderIds = commentList.stream().map(MtGoodsComment::getOrderId).distinct().collect(Collectors.toList());
        Map<Integer, MtOrder> orderMap = new HashMap<>();
        if (orderIds.size() > 0) {
            for (MtOrder mtOrder : mtOrderMapper.selectBatchIds(orderIds)) {
                 orderMap.put(mtOrder.getId(), mtOrder);
            }
        }
        for (MtGoodsComment comment : commentList) {
             GoodsCommentDto dto = new GoodsCommentDto();
             dto.setId(comment.getId());
             dto.setMerchantId(comment.getMerchantId());
             dto.setStoreId(comment.getStoreId());
             dto.setUserId(comment.getUserId());
             dto.setOrderId(comment.getOrderId());
             dto.setOrderGoodsId(comment.getOrderGoodsId());
             dto.setGoodsId(comment.getGoodsId());
             dto.setScore(comment.getScore());
             dto.setContent(comment.getContent());
             if (StringUtil.isNotEmpty(comment.getImages())) {
                 List<String> images = new ArrayList<>(Arrays.asList(comment.getImages().split(",")));
                 dto.setImages(images);
             } else {
                 dto.setImages(new ArrayList<>());
             }
             dto.setIsAnonymous(comment.getIsAnonymous());
             dto.setAuditStatus(comment.getAuditStatus());
             dto.setReplyContent(comment.getReplyContent());
             dto.setReplyTime(comment.getReplyTime());
             dto.setStatus(comment.getStatus());
             dto.setCreateTime(comment.getCreateTime());
             dto.setUpdateTime(comment.getUpdateTime());

             MtUser mtUser = userMap.get(comment.getUserId());
             if (mtUser != null) {
                 dto.setUserInfo(mtUser);
                 dto.setUserName(getShowName(mtUser, comment.getIsAnonymous()));
                 dto.setUserNo(mtUser.getUserNo());
             }
             MtOrder mtOrder = orderMap.get(comment.getOrderId());
             if (mtOrder != null) {
                 dto.setOrderSn(mtOrder.getOrderSn());
             }
             MtGoods mtGoods = goodsMap.get(comment.getGoodsId());
             if (mtGoods != null) {
                 dto.setGoodsName(mtGoods.getName());
                 dto.setGoodsLogo(mtGoods.getLogo());
             }
             dataList.add(dto);
        }

        return dataList;
    }

    /**
     * 获取展示的会员称呼，匿名时脱敏
     *
     * @param mtUser 会员信息
     * @param isAnonymous 是否匿名
     * @return
     */
    private String getShowName(MtUser mtUser, String isAnonymous) {
        String name = mtUser.getName();
        if (StringUtil.isEmpty(name)) {
            name = mtUser.getMobile();
        }
        if (StringUtil.isEmpty(name)) {
            name = "会员" + mtUser.getId();
        }
        if (YesOrNoEnum.YES.getKey().equals(isAnonymous) && name.length() > 1) {
            name = name.substring(0, 1) + "**";
        }
        return name;
    }

    /**
     * 各星级评价数量
     *
     * @param commentList 评价列表
     * @return
     */
    private Map<Integer, Long> getStarCount(List<MtGoodsComment> commentList) {
        Map<Integer, Long> starCount = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            int score = i;
            starCount.put(score, commentList.stream().filter(c -> c.getScore() == score).count());
        }
        return starCount;
    }

    /**
     * 空分页响应
     *
     * @param param 查询参数
     * @return
     */
    private PaginationResponse<GoodsCommentDto> buildEmptyResponse(GoodsCommentListParam param) {
        PaginationResponse<GoodsCommentDto> paginationResponse = new PaginationResponse<>();
        paginationResponse.setContent(new ArrayList<>());
        paginationResponse.setTotalElements(0);
        paginationResponse.setTotalPages(0);
        return paginationResponse;
    }
}
