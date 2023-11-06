package com.fuint.common.permission;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.SourceService;
import com.fuint.common.util.AuthUserUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.TSource;
import com.fuint.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限控制业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service("pms")
public class PermissionService {

    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 后台菜单接口
     * */
    @Resource
    SourceService sourceService;

    /**
     * 验证用户是否具备某权限
     *
     * @param  permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermission(String permission) throws BusinessCheckException {
        if (StringUtil.isEmpty(permission)) {
            return false;
        }

        AccountInfo accountInfo = AuthUserUtil.get();
        if (accountInfo == null) {
            return false;
        }

        Set<String> allPermission = new HashSet<>();
        List<TSource> sources = sourceService.getMenuListByUserId(accountInfo.getMerchantId(), accountInfo.getId());
        if (sources != null && sources.size() > 0) {
            for (TSource tSource : sources) {
                allPermission.add(tSource.getPath().replaceAll("/", ":"));
            }
        }

        return hasPermissions(allPermission, permission);
    }

    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission  权限字符串
     * @return boolean
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringUtil.trim(permission));
    }
}