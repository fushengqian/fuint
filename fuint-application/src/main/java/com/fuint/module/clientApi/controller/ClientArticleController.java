package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.ArticleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ArticleDetailParam;
import com.fuint.common.param.ArticleListParam;
import com.fuint.common.service.ArticleService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtArticleMapper;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RequestMapping(value = "/clientApi/article")
public class ClientArticleController extends BaseController {

    /**
     * 文章服务接口
     * */
    @Autowired
    private ArticleService articleService;

    @Resource
    private MtArticleMapper articleMapper;

    /**
     * 获取文章列表
     */
    @ApiOperation(value="获取文章列表", notes="获取文章列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody ArticleListParam articleListParam) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String title = articleListParam.getTitle();
        Integer page = articleListParam.getPage() == null ? Constants.PAGE_NUMBER : articleListParam.getPage();
        Integer pageSize = articleListParam.getPageSize() == null ? Constants.PAGE_SIZE : articleListParam.getPageSize();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(title)) {
            params.put("title", title);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<ArticleDto> paginationResponse = articleService.queryArticleListByPagination(paginationRequest);

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
