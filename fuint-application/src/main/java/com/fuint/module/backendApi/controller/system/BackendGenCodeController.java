package com.fuint.module.backendApi.controller.system;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.GenCodePage;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.GenCodeService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.TGenCode;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-代码生成相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/genCode")
public class BackendGenCodeController extends BaseController {

    /**
     * 生成代码服务接口
     */
    private GenCodeService genCodeService;

    /**
     * 代码生成列表
     */
    @ApiOperation(value = "代码生成列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:index')")
    public ResponseObject list(@ModelAttribute GenCodePage genCodePage) throws BusinessCheckException {
        PaginationResponse<TGenCode> paginationResponse = genCodeService.queryGenCodeListByPagination(genCodePage);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 更新代码生成状态
     */
    @ApiOperation(value = "更新代码状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:add')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        TGenCode tGenCode = genCodeService.queryGenCodeById(params.getId());
        if (tGenCode == null) {
            return getFailureResult(201);
        }
        tGenCode.setId(params.getId());
        tGenCode.setStatus(params.getStatus());
        genCodeService.updateGenCode(tGenCode);

        return getSuccessResult(true);
    }

    /**
     * 保存代码生成
     */
    @ApiOperation(value = "保存代码生成")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:add')")
    public ResponseObject saveHandler(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String status = params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        String tableName = params.get("tableName") == null ? "" : params.get("tableName").toString();
        String moduleName = params.get("moduleName") == null ? "" : params.get("moduleName").toString();
        String tablePrefix = params.get("tablePrefix") == null ? "" : params.get("tablePrefix").toString();
        String author = params.get("author") == null ? "" : params.get("author").toString();
        String backendPath = params.get("backendPath") == null ? "" : params.get("backendPath").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            return getFailureResult(1004, "平台超管帐号才有操作权限");
        }

        TGenCode tGenCode = new TGenCode();
        tGenCode.setPkName("id");
        tGenCode.setStatus(status);
        tGenCode.setTableName(tableName);
        tGenCode.setModuleName(moduleName);
        tGenCode.setTablePrefix(tablePrefix);
        tGenCode.setAuthor(author);
        tGenCode.setBackendPath(backendPath);
        tGenCode.setServiceName(CommonUtil.firstLetterToUpperCase(tableName));
        tGenCode.setPackageName(tableName);
        if (StringUtil.isNotEmpty(id)) {
            tGenCode.setId(Integer.parseInt(id));
            genCodeService.updateGenCode(tGenCode);
        } else {
            genCodeService.addGenCode(tGenCode);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取代码生成详情
     */
    @ApiOperation(value = "获取代码生成详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        TGenCode tGenCode = genCodeService.queryGenCodeById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("tGenCode", tGenCode);

        return getSuccessResult(result);
    }

    /**
     * 生成代码
     */
    @ApiOperation(value = "生成代码")
    @RequestMapping(value = "/gen/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:gen')")
    public ResponseObject gen(@PathVariable("id") Integer id) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            return getFailureResult(1004, "平台超管帐号才有操作权限");
        }
        TGenCode tGenCode = genCodeService.queryGenCodeById(id);
        if (tGenCode == null) {
            return getFailureResult(201, "生成代码不存在");
        }

        genCodeService.generatorCode(tGenCode.getTableName());
        return getSuccessResult(true);
    }
}
