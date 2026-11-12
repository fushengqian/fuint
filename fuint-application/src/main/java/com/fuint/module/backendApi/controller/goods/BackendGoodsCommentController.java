package com.fuint.module.backendApi.controller.goods;

import com.fuint.common.dto.goods.GoodsCommentDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.GoodsCommentListParam;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.GoodsCommentService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品评价管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="管理端-商品评价相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/goods/comment")
public class BackendGoodsCommentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BackendGoodsCommentController.class);

    /**
     * 商品评价服务接口
     */
    private GoodsCommentService goodsCommentService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 分页查询评价列表
     */
    @Operation(summary="分页查询评价列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:comment:index')")
    public ResponseObject list(@RequestBody GoodsCommentListParam param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        if (merchantId > 0) {
            param.setMerchantId(merchantId);
        }
        if (storeId > 0) {
            param.setStoreId(storeId);
        }
        PaginationResponse<GoodsCommentDto> paginationResponse = goodsCommentService.queryCommentListByPagination(param);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 回复评价
     */
    @Operation(summary="回复评价")
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:comment:edit')")
    public ResponseObject reply(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Integer commentId = params.get("commentId") == null ? 0 : Integer.parseInt(params.get("commentId").toString());
        String content = params.get("content") == null ? "" : params.get("content").toString();

        goodsCommentService.replyComment(commentId, accountInfo.getId(), content);
        logger.info("回复评价, commentId = {}, account = {}", commentId, accountInfo.getAccountName());

        return getSuccessResult(true);
    }

    /**
     * 更新评价状态（展示/隐藏）
     */
    @Operation(summary="更新评价状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:comment:edit')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        goodsCommentService.updateStatus(params.getId(), params.getStatus());
        logger.info("更新评价状态, commentId = {}, status = {}, account = {}", params.getId(), params.getStatus(), accountInfo.getAccountName());

        return getSuccessResult(true);
    }

    /**
     * 删除评价
     */
    @Operation(summary="删除评价")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:comment:edit')")
    public ResponseObject delete(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        goodsCommentService.updateStatus(params.getId(), "D");
        logger.info("删除评价, commentId = {}, account = {}", params.getId(), accountInfo.getAccountName());

        return getSuccessResult(true);
    }
}
