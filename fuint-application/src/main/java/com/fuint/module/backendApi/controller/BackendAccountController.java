package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountDto;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.RoleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.DutyService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.TAccount;
import com.fuint.repository.model.TDuty;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理员管理
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-管理员相关接口")
@RestController
@RequestMapping(value = "/backendApi/account")
public class BackendAccountController extends BaseController {

    /**
     * 账户接口
     */
    @Autowired
    private AccountService tAccountService;

    /**
     * 角色接口
     */
    @Autowired
    private DutyService tDutyService;

    /**
     * 店铺接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 账户信息列表
     *
     * @param  request HttpServletRequest对象
     * @return 账户信息列表
     */
    @ApiOperation(value = "账户信息列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String accountName = request.getParameter("accountName") == null ? "" : request.getParameter("accountName");
        String realName = request.getParameter("realName") == null ? "" : request.getParameter("realName");
        String accountStatus = request.getParameter("accountStatus") == null ? "" : request.getParameter("accountStatus");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(accountName)) {
            searchParams.put("name", accountName);
        }
        if (StringUtil.isNotEmpty(realName)) {
            searchParams.put("realName", realName);
        }
        if (StringUtil.isNotEmpty(accountStatus)) {
            searchParams.put("status", accountStatus);
        }
        if (StringUtil.isNotEmpty(accountStatus)) {
            searchParams.put("status", accountStatus);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<AccountDto> paginationResponse = tAccountService.getAccountListByPagination(paginationRequest);
        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取账户详情
     *
     * @param  request
     * @param  userId 账号ID
     * @return 账户详情
     */
    @ApiOperation(value = "获取账户详情")
    @RequestMapping(value = "/info/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("userId") Long userId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Map<String, Object> result = new HashMap<>();

        List<TDuty> roleList = tDutyService.getAvailableRoles();
        List<RoleDto> roles = new ArrayList<>();
        if (roleList.size() > 0) {
            for (TDuty duty : roleList) {
                RoleDto e = new RoleDto();
                e.setId(duty.getDutyId().longValue());
                e.setName(duty.getDutyName());
                e.setStatus(duty.getStatus());
                roles.add(e);
            }
        }
        result.put("roles", roles);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> stores = storeService.queryStoresByParams(params);
        result.put("stores", stores);

        AccountDto accountDto = null;
        if (userId > 0) {
            TAccount tAccount = tAccountService.getAccountInfoById(userId.intValue());
            accountDto = new AccountDto();
            accountDto.setId(tAccount.getAcctId());
            accountDto.setAccountKey(tAccount.getAccountKey());
            accountDto.setAccountName(tAccount.getAccountName());
            accountDto.setAccountStatus(tAccount.getAccountStatus());
            accountDto.setCreateDate(tAccount.getCreateDate());
            accountDto.setRealName(tAccount.getRealName());
            accountDto.setModifyDate(tAccount.getModifyDate());
            accountDto.setStaffId(tAccount.getStaffId());
            if (tAccount.getStoreId() > 0) {
                accountDto.setStoreId(tAccount.getStoreId());
            }
            if (tAccount.getStoreId() > 0) {
                MtStore mtStore = storeService.queryStoreById(tAccount.getStoreId());
                if (mtStore != null) {
                    accountDto.setStoreName(mtStore.getName());
                }
            }
            if (tAccount != null) {
                List<Long> roleIds = tAccountService.getRoleIdsByAccountId(tAccount.getAcctId());
                result.put("roleIds", roleIds);
            }
        } else {
            result.put("roleIds", "");
        }

        result.put("account", accountDto);
        return getSuccessResult(result);
    }

    /**
     * 新增账户
     *
     * @return 新增账户
     * @throws BusinessCheckException
     */
    @ApiOperation(value = "新增账户")
    @RequestMapping(value = "/doCreate", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doCreate(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        AccountInfo loginAccount = TokenUtil.getAccountInfoByToken(token);
        if (loginAccount == null) {
            return getFailureResult(1001, "请先登录");
        }

        List<Integer> roleIds = (List) param.get("roleIds");
        String accountName = param.get("accountName").toString();
        String accountStatus = param.get("accountStatus").toString();
        String realName = param.get("realName").toString();
        String password = param.get("password").toString();
        String storeId = param.get("storeId") == null ? "0" : param.get("storeId").toString();
        String merchantId = param.get("merchantId") == null ? "0" : param.get("merchantId").toString();
        String staffId = param.get("staffId") == null ? "0" : param.get("staffId").toString();

        AccountInfo accountInfo = tAccountService.getAccountByName(accountName);
        if (accountInfo != null) {
            return getFailureResult(201, "该用户名已存在");
        }

        List<TDuty> duties = new ArrayList<>();
        if (roleIds.size() > 0) {
            Integer[] roles = roleIds.toArray(new Integer[roleIds.size()]);
            String[] ids = new String[roles.length];
            for (int i = 0; i < roles.length; i++) {
                ids[i] = roles[i].toString();
            }
            duties = tDutyService.findDatasByIds(ids);
            if (duties.size() < roleIds.size()) {
                return getFailureResult(201, "您分配的角色不存在");
            }
        }

        TAccount tAccount = new TAccount();
        tAccount.setRealName(realName);
        tAccount.setAccountName(accountName);
        tAccount.setAccountStatus(Integer.parseInt(accountStatus));
        tAccount.setPassword(password);
        tAccount.setIsActive(1);
        tAccount.setLocked(0);
        tAccount.setStoreId(Integer.parseInt(storeId));
        tAccount.setMerchantId(Integer.parseInt(merchantId));
        tAccount.setStaffId(Integer.parseInt(staffId));

        tAccountService.createAccountInfo(tAccount, duties);
        return getSuccessResult(true);
    }

    /**
     * 修改账户信息
     *
     * @return
     * @throws BusinessCheckException
     */
    @ApiOperation(value = "修改账户信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        List<Integer> roleIds = (List) param.get("roleIds");
        String realName = param.get("realName").toString();
        String accountName = param.get("accountName").toString();
        String accountStatus = param.get("accountStatus").toString();
        String storeId = param.get("storeId") == null ? "" : param.get("storeId").toString();
        String staffId = param.get("staffId") == null ? "" : param.get("staffId").toString();
        String merchantId = param.get("merchantId") == null ? "" : param.get("merchantId").toString();
        Long id = Long.parseLong(param.get("id").toString());

        AccountInfo loginAccount = TokenUtil.getAccountInfoByToken(token);
        if (loginAccount == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount tAccount = tAccountService.getAccountInfoById(id.intValue());
        tAccount.setAcctId(id.intValue());
        tAccount.setRealName(realName);

        if (StringUtil.isNotEmpty(accountName)) {
            tAccount.setAccountName(accountName);
        }
        if (StringUtil.isNotEmpty(accountStatus)) {
            tAccount.setAccountStatus(Integer.parseInt(accountStatus));
        }
        if (StringUtil.isNotEmpty(storeId)) {
            tAccount.setStoreId(Integer.parseInt(storeId));
        }
        if (StringUtil.isNotEmpty(staffId)) {
            tAccount.setStaffId(Integer.parseInt(staffId));
        }
        if (StringUtil.isNotEmpty(merchantId)) {
            tAccount.setMerchantId(Integer.parseInt(merchantId));
        }

        AccountInfo accountInfo = tAccountService.getAccountByName(accountName);
        if (accountInfo != null && accountInfo.getId() != id.intValue()) {
            return getFailureResult(201, "该用户名已存在");
        }

        List<TDuty> duties = null;
        if (roleIds.size() > 0) {
            Integer[] roles = roleIds.toArray(new Integer[roleIds.size()]);
            String[] ids = new String[roles.length];
            for (int i = 0; i < roles.length; i++) {
                ids[i] = roles[i].toString();
            }
            duties = tDutyService.findDatasByIds(ids);
            if (duties.size() < roleIds.size()) {
                return getFailureResult(201, "您分配的角色不存在");
            }
        }

        tAccountService.editAccount(tAccount, duties);
        return getSuccessResult(true);
    }

    /**
     * 删除账户信息
     *
     * @param request HttpServletRequest对象
     * @param userIds  账户ID（逗号隔开）
     * @return
     * @throws BusinessCheckException
     */
    @ApiOperation(value = "删除账户信息")
    @RequestMapping(value = "/delete/{userIds}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject deleteAccount(HttpServletRequest request, @PathVariable("userIds") String userIds) {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        String ids[] = userIds.split(",");
        if (ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                 if (StringUtil.isNotEmpty(ids[i])) {
                     Integer userId = Integer.parseInt(ids[i]);
                     TAccount tAccount = tAccountService.getAccountInfoById(userId.intValue());
                     if (tAccount == null) {
                         return getFailureResult(201, "账户不存在");
                     }
                     if (StringUtil.equals(accountInfo.getAccountName(), tAccount.getAccountName())) {
                         return getFailureResult(201, "您不能删除自己");
                     }
                 }
            }
            for (int i = 0; i < ids.length; i++) {
                 if (StringUtil.isNotEmpty(ids[i])) {
                     Long userId = Long.parseLong(ids[i]);
                     tAccountService.deleteAccount(userId);
                 }
            }
        }
        return getSuccessResult(true);
    }

    /**
     * 更新账户状态
     *
     * @return
     */
    @ApiOperation(value = "更新账户状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        Integer status = param.get("status") == null ? 0 : Integer.parseInt(param.get("status").toString());

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount tAccount = tAccountService.getAccountInfoById(userId.intValue());
        if (tAccount == null) {
            return getFailureResult(201, "账户不存在");
        }

        tAccount.setAccountStatus(status);
        tAccountService.updateAccount(tAccount);

        return getSuccessResult(true);
    }

    /**
     * 修改账户密码
     *
     * @return
     */
    @ApiOperation(value = "修改账户密码")
    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject resetPwd(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String password = param.get("password") == null ? "" : param.get("password").toString();

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount tAccount = tAccountService.getAccountInfoById(userId.intValue());
        tAccount.setPassword(password);

        if (tAccount != null) {
            tAccountService.entryptPassword(tAccount);
            tAccountService.updateAccount(tAccount);
        }

        return getSuccessResult(true);
    }
}