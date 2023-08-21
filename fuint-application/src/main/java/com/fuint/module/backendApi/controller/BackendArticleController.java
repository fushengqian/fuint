package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ArticleDto;
import com.fuint.common.service.ArticleService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtArticle;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-文章相关接口")
@RestController
@RequestMapping(value = "/backendApi/article")
public class BackendArticleController extends BaseController {

    /**
     * 文章服务接口
     */
    @Autowired
    private ArticleService articleService;

    /**
     * 系统设置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 文章列表查询
     *
     * @param  request  HttpServletRequest对象
     * @return 文章列表
     */
    @ApiOperation(value = "文章列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String title = request.getParameter("title");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId;
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        } else {
            storeId = accountInfo.getStoreId();
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(title)) {
            params.put("title", title);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<ArticleDto> paginationResponse = articleService.queryArticleListByPagination(paginationRequest);

        String imagePath = settingService.getUploadBasePath();
        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", imagePath);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新文章状态
     *
     * @return
     */
    @ApiOperation(value = "更新文章状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtArticle mtArticle = articleService.queryArticleById(id);
        if (mtArticle == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();

        ArticleDto article = new ArticleDto();
        article.setOperator(operator);
        article.setId(id);
        article.setStatus(status);
        articleService.updateArticle(article);

        return getSuccessResult(true);
    }

    /**
     * 保存文章
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存文章")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String title = params.get("title") == null ? "" : params.get("title").toString();
        String description = params.get("description") == null ? "" : params.get("description").toString();
        String image = params.get("image") == null ? "" : params.get("image").toString();
        String url = params.get("url") == null ? "" : params.get("url").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();
        String sort = params.get("sort") == null ? "0" : params.get("sort").toString();
        String brief = params.get("brief") == null ? "" : params.get("brief").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        ArticleDto info = new ArticleDto();
        info.setTitle(title);
        info.setDescription(description);
        info.setImage(image);
        info.setUrl(url);
        info.setOperator(accountInfo.getAccountName());
        info.setStatus(status);
        info.setStoreId(Integer.parseInt(storeId));
        info.setSort(Integer.parseInt(sort));
        info.setBrief(brief);

        if (StringUtil.isNotEmpty(id)) {
            info.setId(Integer.parseInt(id));
            articleService.updateArticle(info);
        } else {
            articleService.addArticle(info);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取文章详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取文章详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtArticle articleInfo = articleService.queryArticleById(id);
        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("articleInfo", articleInfo);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }
}
