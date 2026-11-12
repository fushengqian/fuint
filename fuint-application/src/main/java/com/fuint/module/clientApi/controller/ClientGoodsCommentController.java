package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.goods.GoodsCommentDto;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.GoodsCommentListParam;
import com.fuint.common.param.GoodsCommentSaveParam;
import com.fuint.common.service.GoodsCommentService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtGoodsComment;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评价类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-商品评价相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/goodsApi/comment")
public class ClientGoodsCommentController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientGoodsCommentController.class);

    /**
     * 商品评价服务接口
     * */
    private GoodsCommentService goodsCommentService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 提交商品评价
     */
    @Operation(summary="提交商品评价")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(@RequestBody GoodsCommentSaveParam params) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        MtGoodsComment mtGoodsComment = goodsCommentService.saveComment(params, userInfo.getId());
        return getSuccessResult(mtGoodsComment);
    }

    /**
     * 商品评价列表（含评分统计）
     */
    @Operation(summary="商品评价列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) {
        Integer goodsId = StringUtil.isEmpty(request.getParameter("goodsId")) ? 0 : Integer.parseInt(request.getParameter("goodsId"));
        if (goodsId <= 0) {
            return getFailureResult(201, "商品ID不能为空");
        }
        Integer page = StringUtil.isEmpty(request.getParameter("page")) ? 1 : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = StringUtil.isEmpty(request.getParameter("pageSize")) ? 20 : Integer.parseInt(request.getParameter("pageSize"));
        Integer score = StringUtil.isEmpty(request.getParameter("score")) ? 0 : Integer.parseInt(request.getParameter("score"));

        GoodsCommentListParam param = new GoodsCommentListParam();
        param.setGoodsId(goodsId);
        param.setScore(score);
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setStatus(StatusEnum.ENABLED.getKey());
        PaginationResponse<GoodsCommentDto> paginationResponse = goodsCommentService.queryCommentListByPagination(param);
        fillImagePath(paginationResponse);

        Map<String, Object> result = new HashMap<>();
        result.put("statistics", goodsCommentService.getCommentStatistics(goodsId));
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 我的评价列表
     */
    @Operation(summary="我的评价列表")
    @RequestMapping(value = "/myList", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject myList(@RequestBody GoodsCommentListParam params) {
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        PaginationResponse<GoodsCommentDto> paginationResponse = goodsCommentService.getMyCommentList(params, userInfo.getId());
        fillImagePath(paginationResponse);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 订单内商品评价情况（待评价入口）
     */
    @Operation(summary="订单内商品评价情况")
    @RequestMapping(value = "/orderGoods", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject orderGoods(HttpServletRequest request) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Integer orderId = StringUtil.isEmpty(request.getParameter("orderId")) ? 0 : Integer.parseInt(request.getParameter("orderId"));
        if (orderId <= 0) {
            return getFailureResult(201, "订单ID不能为空");
        }
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = goodsCommentService.getOrderGoodsCommentList(orderId, userInfo.getId());
        // 补全商品图片访问路径
        String basePath = settingService.getUploadBasePath();
        for (Map<String, Object> item : list) {
             Object goodsLogo = item.get("goodsLogo");
             if (goodsLogo != null && StringUtil.isNotEmpty(goodsLogo.toString())) {
                 item.put("goodsLogo", basePath + goodsLogo);
             }
        }
        result.put("list", list);

        return getSuccessResult(result);
    }

    /**
     * 删除自己的评价
     */
    @Operation(summary="删除自己的评价")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject delete(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Integer commentId = params.get("commentId") == null ? 0 : Integer.parseInt(params.get("commentId").toString());
        if (commentId <= 0) {
            return getFailureResult(201, "评价ID不能为空");
        }
        goodsCommentService.deleteMyComment(commentId, userInfo.getId());
        logger.info("会员删除评价, commentId = {}, userId = {}", commentId, userInfo.getId());

        return getSuccessResult(true);
    }

    /**
     * 补全图片访问路径
     */
    private void fillImagePath(PaginationResponse<GoodsCommentDto> paginationResponse) {
        String basePath = settingService.getUploadBasePath();
        for (GoodsCommentDto dto : paginationResponse.getContent()) {
             if (dto.getImages() != null && dto.getImages().size() > 0) {
                 for (int i = 0; i < dto.getImages().size(); i++) {
                      dto.getImages().set(i, basePath + dto.getImages().get(i));
                 }
             }
             if (dto.getGoodsLogo() != null && !dto.getGoodsLogo().isEmpty()) {
                 dto.setGoodsLogo(basePath + dto.getGoodsLogo());
             }
        }
    }
}
