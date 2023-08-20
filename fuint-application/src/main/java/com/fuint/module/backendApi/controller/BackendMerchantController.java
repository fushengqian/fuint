package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtMerchant;
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
 * 商户管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商户管理相关接口")
@RestController
@RequestMapping(value = "/backendApi/merchant")
public class BackendMerchantController extends BaseController {

    /**
     * 商户服务接口
     */
    @Autowired
    private MerchantService merchantService;

    /**
     * 系统设置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 分页查询商户列表
     *
     * @param  request  HttpServletRequest对象
     * @return 商户列表
     */
    @ApiOperation(value = "分页查询商户列表")
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        String merchantId = request.getParameter("id");
        String merchantName = request.getParameter("name");
        String status = request.getParameter("status");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(merchantId)) {
            params.put("id", merchantId);
        }
        if (StringUtil.isNotEmpty(merchantName)) {
            params.put("name", merchantName);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtMerchant> paginationResponse = merchantService.queryMerchantListByPagination(paginationRequest);

        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }

    /**
     * 查询商户列表
     * */
    @ApiOperation(value = "查询商户列表")
    @RequestMapping(value = "/searchMerchant",  method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject searchMerchant(HttpServletRequest request) throws BusinessCheckException {
        String merchantId = request.getParameter("id") == null ? "" : request.getParameter("id");
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(merchantId)) {
            params.put("merchantId", merchantId);
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }

        params.put("status", StatusEnum.ENABLED.getKey());
        List<MtMerchant> merchantList = merchantService.queryMerchantByParams(params);
        Map<String, Object> result = new HashMap<>();
        result.put("merchantList", merchantList);

        return getSuccessResult(result);
    }

    /**
     * 更新商户状态
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "更新商户状态")
    @RequestMapping(value = "/updateStatus")
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer merchantId = params.get("merchantId") == null ? 0 : Integer.parseInt(params.get("merchantId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        merchantService.updateStatus(merchantId, operator, status);

        return getSuccessResult(true);
    }

    /**
     * 保存商户信息
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @ApiOperation(value = "保存商户信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer merchantId = StringUtil.isEmpty(params.get("id").toString()) ? Integer.parseInt("0") : Integer.parseInt(params.get("id").toString());
        String name = CommonUtil.replaceXSS(params.get("name").toString());
        String merchantNo = CommonUtil.replaceXSS(params.get("no").toString());
        String contact = params.get("contact") == null ? "" : CommonUtil.replaceXSS(params.get("contact").toString());
        String phone = params.get("phone") == null ? "" : CommonUtil.replaceXSS(params.get("phone").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String address = params.get("address") == null ? "" : CommonUtil.replaceXSS(params.get("address").toString());
        String status = params.get("status") == null ? "" : CommonUtil.replaceXSS(params.get("status").toString());
        String logo = params.get("logo") == null ? "" : CommonUtil.replaceXSS(params.get("logo").toString());

        MtMerchant merchantInfo = new MtMerchant();
        merchantInfo.setName(name);
        merchantInfo.setNo(merchantNo);
        merchantInfo.setContact(contact);
        merchantInfo.setPhone(phone);
        merchantInfo.setDescription(description);
        merchantInfo.setAddress(address);
        merchantInfo.setLogo(logo);
        merchantInfo.setStatus(status);

        if (StringUtil.isEmpty(name)) {
            return getFailureResult(201, "商户名称不能为空");
        } else {
            MtMerchant tempDto = merchantService.queryMerchantByName(name);
            if (null != tempDto && !tempDto.getId().equals(merchantId)) {
                return getFailureResult(201, "该商户名称已经存在");
            }
        }

        // 修改商户信息
        if (merchantId > 0) {
            merchantInfo.setId(merchantId);
        }

        String operator = accountInfo.getAccountName();
        merchantInfo.setOperator(operator);

        merchantService.saveMerchant(merchantInfo);
        return getSuccessResult(true);
    }

    /**
     * 获取商户详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取商户详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getMerchantInfo(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtMerchant merchantInfo = merchantService.queryMerchantById(id);;

        Map<String, Object> result = new HashMap<>();
        result.put("merchantInfo", merchantInfo);

        return getSuccessResult(result);
    }
}
