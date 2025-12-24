package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.ArticleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ArticleDetailParam;
import com.fuint.common.param.ArticlePage;
import com.fuint.common.service.ArticleService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtArticleMapper;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-文章相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/article")
public class ClientArticleController extends BaseController {

    private MtArticleMapper articleMapper;

    /**
     * 文章服务接口
     * */
    private ArticleService articleService;

    /**
     * 获取文章列表
     */
    @ApiOperation(value="获取文章列表", notes="获取文章列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody ArticlePage articlePage) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        articlePage.setStatus(StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(merchantNo)) {
            articlePage.setMerchantNo(merchantNo);
        }
        PaginationResponse<ArticleDto> paginationResponse = articleService.queryArticleListByPagination(articlePage);

        Map<String, Object> outParams = new HashMap();
        outParams.put("content", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(outParams);
    }

    /**
     * 获取文章详情
     */
    @ApiOperation(value="获取文章详情", notes="根据ID获取文章详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody ArticleDetailParam articleDetailParam) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String articleIdStr = articleDetailParam.getArticleId() == null ? "" : articleDetailParam.getArticleId();
        Integer articleId = 0;
        if (StringUtil.isNotEmpty(articleIdStr)) {
            articleId = Integer.parseInt(articleIdStr);
        }

        // 更新阅读点击数
        ArticleDto mtArticle = articleService.getArticleDetail(articleId);
        if (mtArticle != null && mtArticle.getId() != null) {
            articleMapper.increaseClick(mtArticle.getId());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("articleInfo", mtArticle);

        return getSuccessResult(result);
    }
}
