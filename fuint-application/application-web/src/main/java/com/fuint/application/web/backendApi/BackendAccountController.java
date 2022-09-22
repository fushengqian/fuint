package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.dto.RoleDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.entities.TDuty;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.application.service.token.TokenService;
import com.fuint.base.service.duty.TDutyService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.apache.commons.collections.map.HashedMap;
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
 * 管理员管理
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/account")
public class BackendAccountController extends BaseController {

    /**
     * 账户接口
     */
    @Autowired
    private TAccountService tAccountService;

    /**
     * 角色接口
     */
    @Autowired
    private TDutyService tDutyService;

    /**
     * 店铺接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    /**
     * 账户信息列表
     *
     * @param request  HttpServletRequest对象
     * @return 账户信息列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String accountName = request.getParameter("accountName") == null ? "" : request.getParameter("accountName");
        String accountStatus = request.getParameter("accountStatus") == null ? "" : request.getParameter("accountStatus");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtil.isNotEmpty(accountName)) {
            searchParams.put("EQ_accountName", accountName);
        }
        if (StringUtil.isNotEmpty(accountStatus)) {
            searchParams.put("EQ_accountStatus", accountStatus);
        }
        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"createDate desc"});

        PaginationResponse<TAccount> paginationResponse = tAccountService.findAccountsByPagination(paginationRequest);
        List<AccountDto> content = new ArrayList<>();
        if (paginationResponse.getContent().size() > 0) {
            for (TAccount tAccount : paginationResponse.getContent()) {
                 AccountDto e = new AccountDto();
                 e.setId(tAccount.getId());
                 e.setAccountKey(tAccount.getAccountKey());
                 e.setAccountName(tAccount.getAccountName());
                 e.setAccountStatus(tAccount.getAccountStatus());
                 e.setCreateDate(tAccount.getCreateDate());
                 e.setRealName(tAccount.getRealName());
                 e.setModifyDate(tAccount.getModifyDate());
                 e.setStaffId(tAccount.getStaffId());
                 e.setStoreId(tAccount.getStoreId());
                 if (e.getStoreId() > 0) {
                    MtStore mtStore = storeService.queryStoreById(tAccount.getStoreId());
                    e.setStoreName(mtStore.getName());
                 }
                 content.add(e);
            }
        }

        PageRequest pageRequest = new PageRequest((paginationRequest.getCurrentPage() + 1), paginationRequest.getPageSize());
        Page pageImpl = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<AccountDto> result = new PaginationResponse(pageImpl, AccountDto.class);
        result.setTotalPages(paginationResponse.getTotalPages());
        result.setTotalElements(paginationResponse.getTotalElements());
        result.setContent(content);

        return getSuccessResult(result);
    }

    /**
     * 获取账户详情
     *
     * @param   userId 账号ID
     * @return  账户详情
     */
    @RequestMapping(value = "/info/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info( @PathVariable("userId") Long userId) throws BusinessCheckException {
        Map<String, Object> result = new HashMap<>();

        List<TDuty> roleList = tDutyService.getAvailableRoles();
        List<RoleDto> roles = new ArrayList<>();
        if (roleList.size() > 0) {
            for (TDuty duty : roleList) {
                RoleDto e = new RoleDto();
                e.setId(duty.getId());
                e.setName(duty.getName());
                e.setStatus(duty.getStatus());
                roles.add(e);
            }
        }
        result.put("roles", roles);

        Map<String, Object> params = new HashMap<>();
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> stores = storeService.queryStoresByParams(params);
        result.put("stores", stores);

        AccountDto accountInfo = null;
        if (userId > 0) {
            TAccount tAccount = tAccountService.findAccountById(userId);

            accountInfo = new AccountDto();
            accountInfo.setId(tAccount.getId());
            accountInfo.setAccountKey(tAccount.getAccountKey());
            accountInfo.setAccountName(tAccount.getAccountName());
            accountInfo.setAccountStatus(tAccount.getAccountStatus());
            accountInfo.setCreateDate(tAccount.getCreateDate());
            accountInfo.setRealName(tAccount.getRealName());
            accountInfo.setModifyDate(tAccount.getModifyDate());
            accountInfo.setStaffId(tAccount.getStaffId());
            if (tAccount.getStoreId() > 0) {
                accountInfo.setStoreId(tAccount.getStoreId());
            }

            if (tAccount.getStoreId() > 0) {
                MtStore mtStore = storeService.queryStoreById(tAccount.getStoreId());
                accountInfo.setStoreName(mtStore.getName());
            }

            if (tAccount != null) {
                List<Long> roleIds = tAccountService.getDutyIdsByAccountId(tAccount.getId());
                result.put("roleIds", roleIds);
            }
        } else {
            result.put("roleIds", "");
        }

        result.put("account", accountInfo);

        return getSuccessResult(result);
    }

    /**
     * 新增账户
     *
     * @return 账户列表页面
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/doCreate", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doCreate(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        List<Integer> roleIds = (List) param.get("roleIds");
        String accountName = param.get("accountName").toString();
        String accountStatus = param.get("accountStatus").toString();
        String realName = param.get("realName").toString();
        String password = param.get("password").toString();
        String storeId = param.get("storeId").toString();

        TAccount accountInfo = tAccountService.findByAccountName(accountName);
        if (accountInfo != null) {
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

        TAccount account = new TAccount();
        account.setRealName(realName);
        account.setAccountName(accountName);
        account.setAccountStatus(Integer.parseInt(accountStatus));
        account.setPassword(password);
        account.setIsActive(1);
        account.setLocked(0);
        account.setStoreId(Integer.parseInt(storeId));

        tAccountService.addAccount(account, duties, "1");

        return getSuccessResult(true);
    }

    /**
     * 修改账户信息
     *
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        List<Integer> roleIds = (List) param.get("roleIds");
        String realName = param.get("realName").toString();
        String accountName = param.get("accountName").toString();
        String accountStatus = param.get("accountStatus").toString();
        String storeId = param.get("storeId").toString();
        Long id =  Long.parseLong(param.get("id").toString());

        TAccount tAccount = new TAccount();
        tAccount.setId(id);
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

        TAccount accountInfo = tAccountService.findByAccountName(accountName);
        if (accountInfo != null && !accountInfo.getId().equals(id)) {
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

        tAccountService.editAccount(tAccount, duties, "1");

        return getSuccessResult(true);
    }

    /**
     * 删除账户信息
     *
     * @param request    HttpServletRequest对象
     * @param userId     账户ID
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject deleteAccount(HttpServletRequest request, @PathVariable("userId") Long userId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        TAccount tAccount = tAccountService.findAccountById(userId);

        if (tAccount == null) {
            return getFailureResult(201, "账户不存在");
        }

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (StringUtil.equals(accountInfo.getAccountName(), tAccount.getAccountName())){
            return getFailureResult(201, "您不能删除自己");
        }

        tAccountService.removeAccount(tAccount.getAccountKey(), "1");

        return getSuccessResult(true);
    }

    /**
     * 更新账户状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) {
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        Integer status = param.get("status") == null ? 0 : Integer.parseInt(param.get("status").toString());

        TAccount tAccount = tAccountService.findAccountById(userId.longValue());
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
    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject resetPwd(@RequestBody Map<String, Object> param) {
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String password = param.get("password") == null ? "" : param.get("password").toString();

        TAccount tAccount = tAccountService.findAccountById(userId.longValue());
        tAccount.setPassword(password);

        if (tAccount != null) {
            tAccountService.entryptPassword(tAccount);
            tAccountService.updateAccount(tAccount);
        }

        return getSuccessResult(true);
    }
}
