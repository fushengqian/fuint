package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.dto.TokenDto;
import com.fuint.application.enums.AdminRoleEnum;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.vo.RouterVo;
import com.fuint.base.dao.entities.TDuty;
import com.fuint.base.dao.entities.TSource;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.duty.TDutyService;
import com.fuint.base.service.entities.TreeNode;
import com.fuint.base.util.TreeUtil;
import com.fuint.captcha.service.CaptchaService;
import com.fuint.application.service.source.SourceService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台登录
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/login")
public class BackendLoginController extends BaseController {

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    /**
     * 图形验证码
     * */
    @Autowired
    private CaptchaService captchaService;

    /**
     * 账户服务接口
     */
    @Autowired
    private TAccountService tAccountService;

    /**
     * 菜单服务接口
     * */
    @Autowired
    private SourceService sourceService;

    /**
     * 角色接口
     */
    @Autowired
    private TDutyService tDutyService;

    /**
     * 处理身份认证异常后的 post 请求
     *
     * @param param 登陆用户账户
     * @return 登陆后返回token
     */
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doLogin(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String username = param.get("username").toString();
        String password = param.get("password").toString();
        String captchaCode = param.get("captchaCode") == null ? "" : param.get("captchaCode").toString();
        String uuid = param.get("uuid") == null ? "" : param.get("uuid").toString();
        String userAgent = request.getHeader("user-agent") == null ? "" : request.getHeader("user-agent");

        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password) || StringUtil.isEmpty(captchaCode)) {
            return getFailureResult(201,"请求参数有误");
        }

        Boolean captchaVerify = captchaService.checkCodeByUuid(captchaCode, uuid);
        if (!captchaVerify) {
            return getFailureResult(1002,"图形验证码有误");
        }

        TAccount tAccount = tAccountService.findByAccountName(username);
        if (tAccount != null) {
            AccountDto accountInfo = new AccountDto();
            BeanUtils.copyProperties(tAccount, accountInfo);
            String myPassword = accountInfo.getPassword();
            String inputPassword = tAccountService.getEntryptPassword(password, accountInfo.getSalt());
            if (myPassword.equals(inputPassword)) {
                String token = tokenService.generateToken(userAgent, accountInfo.getId().toString());
                TokenDto result = new TokenDto();
                result.setIsLogin("true");
                result.setToken(token);
                tokenService.saveAccountToken(token, accountInfo);
                return getSuccessResult(result);
            } else {
                return getFailureResult(201, "账号或密码有误");
            }
        } else {
            return getFailureResult(201, "登录失败");
        }
    }

    /**
     * 获取账号详情
     *
     * @return
     */
    @RequiresPermissions("getInfo")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getInfo(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(201,"请求参数有误");
        }

        Map<String, Object> result = new HashMap<>();

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401,"请先登录");
        }

        List<Long> roleIds = tAccountService.getDutyIdsByAccountId(accountInfo.getId());

        List<String> roles = new ArrayList<>();
        if (roleIds.size() > 0) {
            for (int i = 0; i < roleIds.size(); i++) {
                TDuty role = tDutyService.getRoleById(roleIds.get(i));
                for (AdminRoleEnum item : AdminRoleEnum.values()) {
                    if (role.getDutyType().equals(item.getKey())) {
                        roles.add(item.getValue());
                    }
                }
            }
        }

        List<TSource> sources = sourceService.getMenuListByUserId(accountInfo.getId().intValue());
        List<String> permissions = new ArrayList<>();
        if (sources.size() > 0) {
            for (TSource source : sources) {
                 String permission = source.getPath().replaceAll("/", ":");
                 permissions.add(permission);
            }
        }

        result.put("accountInfo", accountInfo);
        result.put("roles", roles);
        result.put("permissions", permissions);

        return getSuccessResult(result);
    }

    /**
     * 处理退出get请求，加载登陆页面
     *
     * @return 退出登陆
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject logout(HttpServletRequest request) {
        String userToken = request.getHeader("Access-Token");
        Boolean flag = tokenService.removeToken(userToken);

        if (Boolean.FALSE == flag) {
            return getFailureResult(1001, "退出错误!");
        } else {
            return getSuccessResult(true);
        }
    }

    /**
     * 获取登录路由菜单
     *
     * @return
     */
    @RequiresPermissions("getRouters")
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getRouters(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(201,"请求参数有误");
        }

        AccountDto accountDto = tokenService.getAccountInfoByToken(token);
        List<TSource> sources = sourceService.getMenuListByUserId(accountDto.getId().intValue());

        List<TreeNode> trees = new ArrayList<>();
        TreeNode treeNode;
        for (TSource tSource : sources) {
             treeNode = new TreeNode();
             treeNode.setName(tSource.getName());
             treeNode.setEname(tSource.getEname());
             treeNode.setNewIcon(tSource.getNewIcon());
             treeNode.setPath(tSource.getPath());
             treeNode.setId(tSource.getId());
             treeNode.setLevel(tSource.getLevel());
             treeNode.setIsMenu(tSource.getIsMenu());
             if (tSource.getParent() != null) {
                 treeNode.setpId(tSource.getParent().getId());
             }
             treeNode.setUrl(tSource.getSourceCode());
             treeNode.setIcon(tSource.getIcon());
             trees.add(treeNode);
        }

        List<TreeNode> treeNodes = TreeUtil.sourceTreeNodes(trees);
        List<RouterVo> routers = sourceService.buildMenus(treeNodes);

        return getSuccessResult(routers);
    }
}
