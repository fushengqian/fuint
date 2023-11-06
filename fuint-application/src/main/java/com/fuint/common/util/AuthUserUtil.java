package com.fuint.common.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.fuint.common.dto.AccountInfo;

/**
 * 用户认证工具
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class AuthUserUtil {

    private static final ThreadLocal<AccountInfo> USER_INFO_IN_TOKEN_HOLDER = new TransmittableThreadLocal<>();

    public static AccountInfo get() {
        return USER_INFO_IN_TOKEN_HOLDER.get();
    }

    public static void set(AccountInfo userInfoInTokenBo) {
        USER_INFO_IN_TOKEN_HOLDER.set(userInfoInTokenBo);
    }

    public static void clean() {
      if (USER_INFO_IN_TOKEN_HOLDER.get() != null) {
            USER_INFO_IN_TOKEN_HOLDER.remove();
      }
    }
}
