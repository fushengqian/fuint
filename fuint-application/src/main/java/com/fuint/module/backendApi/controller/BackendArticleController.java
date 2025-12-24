package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ArticleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ArticlePage;
import com.fuint.common.service.ArticleService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtArticle;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@AllArgsConstructor
@RequestMapping(value = "/backendApi/article")
public class BackendArticleController extends BaseController {

    /**
     * 文章服务接口
     */
    private ArticleService articleService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 文章列表查询
     */
    @ApiOperation(value = "文章列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:index')")
    public ResponseObject list(@ModelAttribute ArticlePage articlePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            articlePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            articlePage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<ArticleDto> paginationResponse = articleService.queryArticleListByPagination(articlePage);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新文章状态
     */
    @ApiOperation(value = "更新文章状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:edit')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        MtArticle mtArticle = articleService.queryArticleById(id);
        if (mtArticle == null) {
            return getFailureResult(201);
        }

        ArticleDto article = new ArticleDto();
        article.setOperator(accountInfo.getAccountName());
        article.setId(id);
        article.setStatus(status);
        articleService.updateArticle(article);

        return getSuccessResult(true);
    }

    /**
     * 保存文章
     */
    @ApiOperation(value = "保存文章")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:add')")
    public ResponseObject saveHandler(@RequestBody ArticleDto articleDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        articleDto.setOperator(accountInfo.getAccountName());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            articleDto.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            articleDto.setStoreId(accountInfo.getStoreId());
        }

        if (articleDto.getId() != null && articleDto.getId() > 0) {
            articleService.updateArticle(articleDto);
        } else {
            articleService.addArticle(articleDto);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取文章详情
     */
    @ApiOperation(value = "获取文章详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtArticle articleInfo = articleService.queryArticleById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!articleInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("articleInfo", articleInfo);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }
}
