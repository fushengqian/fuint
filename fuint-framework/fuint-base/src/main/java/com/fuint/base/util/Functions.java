package com.fuint.base.util;

import com.fuint.base.shiro.ShiroUser;
import com.fuint.util.Constant;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;

/**
 * 页面数据处理
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public class Functions {

    /**
     * 根据会话获取用户名
     *
     * @param session
     * @return
     */
    public static String principal(Session session) {
        PrincipalCollection principalCollection = (PrincipalCollection) session
                .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if(principalCollection == null){
            return "";
        }
        ShiroUser su = (ShiroUser) principalCollection.getPrimaryPrincipal();
        return su.getAcctName();
    }

    /**
     * 判断当前会话是否为强制退出
     *
     * @param session
     * @return
     */
    public static boolean isForceLogout(Session session) {
        return session.getAttribute(Constant.sessionConstant.SESSION_FORCE_LOGOUT_KEY) != null;
    }
}
