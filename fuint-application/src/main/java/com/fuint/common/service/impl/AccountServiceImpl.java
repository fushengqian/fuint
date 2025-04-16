package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.AccountDto;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.CaptchaService;
import com.fuint.common.service.StaffService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.exception.BusinessRuntimeException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.backendApi.request.LoginRequest;
import com.fuint.module.backendApi.response.LoginResponse;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.Digests;
import com.fuint.utils.Encodes;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * 后台账号接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class AccountServiceImpl extends ServiceImpl<TAccountMapper, TAccount> implements AccountService {

    private TAccountMapper tAccountMapper;

    private TDutyMapper tDutyMapper;

    private TAccountDutyMapper tAccountDutyMapper;

    private MtMerchantMapper mtMerchantMapper;

    private MtStoreMapper mtStoreMapper;

    /**
     * 员工接口
     */
    private StaffService staffService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 验证码服务接口
     * */
    private CaptchaService captchaService;

    /**
     * 分页查询账号列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<AccountDto> getAccountListByPagination(PaginationRequest paginationRequest) {
        Page<MtBanner> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<TAccount> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(TAccount::getAccountStatus, -1); // 1:启用；0:禁用；-1:删除

        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotEmpty(name)) {
            lambdaQueryWrapper.like(TAccount::getAccountName, name);
        }
        String realName = paginationRequest.getSearchParams().get("realName") == null ? "" : paginationRequest.getSearchParams().get("realName").toString();
        if (StringUtils.isNotEmpty(realName)) {
            lambdaQueryWrapper.like(TAccount::getRealName, realName);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(TAccount::getAccountStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotEmpty(merchantId)) {
            lambdaQueryWrapper.eq(TAccount::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotEmpty(storeId)) {
            lambdaQueryWrapper.eq(TAccount::getStoreId, storeId);
        }
        String staffId = paginationRequest.getSearchParams().get("staffId") == null ? "" : paginationRequest.getSearchParams().get("staffId").toString();
        if (StringUtils.isNotEmpty(staffId)) {
            lambdaQueryWrapper.eq(TAccount::getStaffId, staffId);
        }

        lambdaQueryWrapper.orderByDesc(TAccount::getAcctId);
        List<TAccount> accountList = tAccountMapper.selectList(lambdaQueryWrapper);
        List<AccountDto> dataList = new ArrayList<>();

        for (TAccount tAccount : accountList) {
             AccountDto accountDto = new AccountDto();
             BeanUtils.copyProperties(tAccount, accountDto);
             accountDto.setId(tAccount.getAcctId());
             MtMerchant mtMerchant = mtMerchantMapper.selectById(tAccount.getMerchantId());
             if (mtMerchant != null) {
                 accountDto.setMerchantName(mtMerchant.getName());
             }
             MtStore mtStore = mtStoreMapper.selectById(tAccount.getStoreId());
             if (mtStore != null) {
                 accountDto.setStoreName(mtStore.getName());
             }
             accountDto.setSalt(null);
             accountDto.setPassword(null);
             dataList.add(accountDto);
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<AccountDto> paginationResponse = new PaginationResponse(pageImpl, AccountDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 根据账号名称获取账号信息
     *
     * @param userName 账号名称
     * @return
     * */
    @Override
    public AccountInfo getAccountByName(String userName) {
        Map<String, Object> param = new HashMap();
        param.put("account_name", userName);
        param.put("account_status", 1);
        List<TAccount> accountList = tAccountMapper.selectByMap(param);
        if (accountList != null && accountList.size() > 0) {
            AccountInfo accountInfo = new AccountInfo();
            TAccount account = accountList.get(0);
            accountInfo.setId(account.getAcctId());
            accountInfo.setAccountName(account.getAccountName());
            accountInfo.setRealName(account.getRealName());
            accountInfo.setRoleIds(account.getRoleIds());
            accountInfo.setStaffId(account.getStaffId());
            accountInfo.setStoreId(account.getStoreId());
            Integer merchantId = account.getMerchantId() == null ? 0 : account.getMerchantId();
            accountInfo.setMerchantId(merchantId);
            if (account.getMerchantId() != null && account.getMerchantId() > 0) {
                MtMerchant mtMerchant = mtMerchantMapper.selectById(account.getMerchantId());
                if (mtMerchant != null) {
                    accountInfo.setMerchantName(mtMerchant.getName());
                }
            }
            if (account.getStoreId() != null && account.getStoreId() > 0) {
                MtStore mtStore = mtStoreMapper.selectById(account.getStoreId());
                if (mtStore != null) {
                    accountInfo.setStoreName(mtStore.getName());
                }
            }
            return accountInfo;
        } else {
            return null;
        }
    }

    /**
     * 根据ID获取账号信息
     *
     * @param userId 账号ID
     * @return
     * */
    @Override
    public TAccount getAccountInfoById(Integer userId) {
        TAccount tAccount = tAccountMapper.selectById(userId);
        return tAccount;
    }

    /**
     * 新增后台账户
     *
     * @param tAccount
     * @return
     * */
    @Override
    @OperationServiceLog(description = "新增后台账户")
    public TAccount createAccountInfo(TAccount tAccount, List<TDuty> duties) throws BusinessCheckException {
        TAccount account = new TAccount();
        account.setAccountKey(tAccount.getAccountKey());
        account.setAccountName(tAccount.getAccountName().toLowerCase());
        account.setAccountStatus(1);
        account.setRealName(tAccount.getRealName());
        account.setRoleIds(tAccount.getRoleIds());
        account.setStaffId(tAccount.getStaffId());
        Integer storeId = tAccount.getStoreId() == null ? 0 : tAccount.getStoreId();
        if (tAccount.getMerchantId() == null || tAccount.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null) {
                tAccount.setMerchantId(mtStore.getMerchantId());
            }
        }
        account.setMerchantId(tAccount.getMerchantId());
        account.setStoreId(tAccount.getStoreId());
        account.setCreateDate(new Date());
        account.setModifyDate(new Date());
        account.setStoreId(tAccount.getStoreId());
        account.setStaffId(tAccount.getStaffId());
        account.setPassword(tAccount.getPassword());
        this.entryptPassword(account);
        int id = tAccountMapper.insert(account);

        if (id > 0 && duties != null && duties.size() > 0) {
            for (TDuty tDuty : duties) {
                 TAccountDuty tAccountDuty = new TAccountDuty();
                 tAccountDuty.setDutyId(tDuty.getDutyId());
                 tAccountDuty.setAcctId(account.getAcctId());
                 tAccountDutyMapper.insert(tAccountDuty);
            }
        }

        if (id > 0 ) {
            return this.getAccountInfoById(id);
        } else {
            throw new BusinessRuntimeException("创建账号错误");
        }
    }

    /**
     * 获取账号角色ID
     *
     * @param accountId
     * @return
     * */
    @Override
    public List<Long> getRoleIdsByAccountId(Integer accountId) {
        List<Long> roleIds = tDutyMapper.getRoleIdsByAccountId(accountId);
        return roleIds;
    }

    /**
     * 修改账户
     *
     * @param  tAccount 账户实体
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改后台账户")
    public void editAccount(TAccount tAccount, List<TDuty> duties) throws BusinessCheckException {
        TAccount oldAccount = tAccountMapper.selectById(tAccount.getAcctId());
        if (oldAccount == null) {
            throw new BusinessCheckException("账户不存在.");
        }
        tAccount.setModifyDate(new Date());
        if (duties != null && duties.size() > 0) {
            if (tAccount.getAcctId() != null && tAccount.getAcctId() > 0) {
                tAccountDutyMapper.deleteDutiesByAccountId(tAccount.getAcctId());
                for (TDuty tDuty : duties) {
                     TAccountDuty tAccountDuty = new TAccountDuty();
                     tAccountDuty.setDutyId(tDuty.getDutyId());
                     tAccountDuty.setAcctId(tAccount.getAcctId());
                     tAccountDutyMapper.insert(tAccountDuty);
                }
            }
        }
        if (tAccount.getStaffId() != null && tAccount.getStaffId() > 0) {
            MtStaff mtStaff = staffService.queryStaffById(tAccount.getStaffId());
            if (mtStaff == null) {
                tAccount.setStaffId(0);
            }
        }
        tAccountMapper.updateById(tAccount);
    }

    /**
     * 根据账户名称获取账户所分配的角色ID集合
     *
     * @param  accountId 账户
     * @return 角色ID集合
     */
    @Override
    public List<Integer> getDutyIdsByAccountId(Integer accountId) {
        return tAccountDutyMapper.getDutyIdsByAccountId(accountId);
    }

    /**
     * 更新账户
     *
     * @param tAccount
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改后台账户")
    public void updateAccount(TAccount tAccount) {
        tAccountMapper.updateById(tAccount);
    }

    /**
     * 删除账号
     *
     * @param accountId 账号ID
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除后台账户")
    public void deleteAccount(Long accountId) {
        TAccount tAccount = tAccountMapper.selectById(accountId);
        tAccount.setAccountStatus(-1);
        tAccount.setModifyDate(new Date());
        tAccountMapper.updateById(tAccount);
    }

    /**
     * 设定安全的密码
     *
     * @param tAccount 账号信息
     * @return
     */
    @Override
    public void entryptPassword(TAccount tAccount) {
        byte[] salt = Digests.generateSalt(8);
        tAccount.setSalt(Encodes.encodeHex(salt));
        byte[] hashPassword = Digests.sha1(tAccount.getPassword().getBytes(), salt, 1024);
        tAccount.setPassword(Encodes.encodeHex(hashPassword));
    }

    /**
     * 获取加密密码
     *
     * @param password
     * @param salt
     * @return
     * */
    @Override
    public String getEntryptPassword(String password, String salt) {
        byte[] salt1 = Encodes.decodeHex(salt);
        byte[] hashPassword = Digests.sha1(password.getBytes(), salt1, 1024);
        return Encodes.encodeHex(hashPassword);
    }

    /**
     * 登录后台系统
     *
     * @param loginRequest 登录参数
     * @param userAgent 登录浏览器
     * @return
     * */
    @Override
    @OperationServiceLog(description = "登录后台系统")
    public LoginResponse doLogin(LoginRequest loginRequest, String userAgent) throws BusinessCheckException {
        String accountName = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String captchaCode = loginRequest.getCaptchaCode();
        String uuid = loginRequest.getUuid();

        Boolean captchaVerify = captchaService.checkCodeByUuid(captchaCode, uuid);
        if (!captchaVerify) {
            throw new BusinessCheckException("图形验证码有误");
        }

        if (StringUtil.isEmpty(accountName)|| StringUtil.isEmpty(password) || StringUtil.isEmpty(captchaCode)) {
            throw new BusinessCheckException("登录参数有误");
        } else {
            AccountInfo accountInfo = getAccountByName(loginRequest.getUsername());
            if (accountInfo == null) {
                throw new BusinessCheckException("登录账号或密码有误");
            }

            TAccount tAccount = getAccountInfoById(accountInfo.getId());
            String myPassword = tAccount.getPassword();
            String inputPassword = getEntryptPassword(password, tAccount.getSalt());
            if (!myPassword.equals(inputPassword) || !tAccount.getAccountStatus().toString().equals("1")) {
                throw new BusinessCheckException("登录账号或密码有误");
            }

            // 商户已禁用
            if (tAccount.getMerchantId() != null && tAccount.getMerchantId() > 0) {
                MtMerchant mtMerchant = mtMerchantMapper.selectById(tAccount.getMerchantId());
                if (mtMerchant != null && !mtMerchant.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    throw new BusinessCheckException("您的商户已被禁用，请联系平台方");
                }
            }

            // 店铺已禁用
            if (tAccount.getStoreId() != null && tAccount.getStoreId() > 0) {
                MtStore mtStore = mtStoreMapper.selectById(tAccount.getStoreId());
                if (mtStore != null && !mtStore.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    throw new BusinessCheckException("您的店铺已被禁用，请联系平台方");
                }
            }

            String token = TokenUtil.generateToken(userAgent, accountInfo);
            LoginResponse response = new LoginResponse();
            response.setLogin(true);
            response.setToken(token);
            response.setTokenCreatedTime(new Date());

            return response;
        }
    }
}
