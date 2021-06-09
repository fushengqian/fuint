package com.fuint.base.shiro.config;

import com.fuint.base.shiro.ShiroDbRealm;
import com.fuint.base.shiro.credentials.RetryLimitHashedCredentialsMatcher;
import com.fuint.base.shiro.filter.AuthFilter;
import com.fuint.util.Constant;
import com.fuint.util.StringUtil;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * shiro认证服务配置类
 *
 * @author fsq
 * @version $Id: ShiroConfiguration.java, v 0.1 2015年11月12日 上午11:18:47 fsq Exp $
 */
@Configuration
public class ShiroConfiguration {

    private static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();


    /**
     * 缓存管理器
     *
     * @return
     */
    @Bean(name = "shiroEhcacheManager")
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    /**
     * 凭证匹配器
     *
     * @return
     */
    @Bean(name = "credentialsMatcher")
    public RetryLimitHashedCredentialsMatcher getRetryLimitHashedCredentialsMatcher() {
        RetryLimitHashedCredentialsMatcher rlhcm = new RetryLimitHashedCredentialsMatcher(getEhCacheManager());
        rlhcm.setHashAlgorithmName(Constant.SaltConstant.HASH_ALGORITHM);
        rlhcm.setHashIterations(Constant.SaltConstant.HASH_INTERATIONS);
        rlhcm.setStoredCredentialsHexEncoded(true);
        return rlhcm;
    }

    /**
     * shiro认证授权realm实现类
     *
     * @return
     */
    @Bean(name = "shiroDbRealm")
    public ShiroDbRealm getShiroRealm() {
        ShiroDbRealm sdr = new ShiroDbRealm();
        sdr.setCredentialsMatcher(getRetryLimitHashedCredentialsMatcher());
        sdr.setCachingEnabled(true);
        sdr.setAuthenticationCachingEnabled(true);
        sdr.setAuthenticationCacheName("authenticationCache");
        sdr.setAuthorizationCachingEnabled(true);
        sdr.setAuthorizationCacheName("authorizationCache");
        return sdr;
    }

    /**
     * shiro声明周期管理
     *
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * 会话ID生成器
     *
     * @return
     */
    @Bean(name = "sessionIdGenerator")
    public JavaUuidSessionIdGenerator getJavaUuidSessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    /**
     * spring自动代理（辅助shiro注解支持）
     *
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }


    /**
     * 会话COOKIE模板
     *
     * @return
     */
    @Bean(name = "sessionIdCookie")
    public SimpleCookie getSessionSimpleCookie() {
        SimpleCookie sc = new SimpleCookie("sid");
        sc.setHttpOnly(true);
        sc.setMaxAge(-1);
        return sc;
    }

    /**
     * rememberMeCOOKIE模板
     *
     * @return
     */
    @Bean(name = "rememberMeCookie")
    public SimpleCookie getRememberMeSimpleCookie() {
        SimpleCookie sc = new SimpleCookie("rememberMe");
        sc.setHttpOnly(true);
        sc.setMaxAge(2592000);
        return sc;
    }

    /**
     * 会话DAO
     *
     * @return
     */
    @Bean(name = "sessionDAO")
    public EnterpriseCacheSessionDAO getEnterpriseCacheSessionDAO() {
        EnterpriseCacheSessionDAO ecs = new EnterpriseCacheSessionDAO();
        ecs.setActiveSessionsCacheName("shiro-activeSessionCache");
        ecs.setSessionIdGenerator(getJavaUuidSessionIdGenerator());
        return ecs;
    }

    /**
     * 会话管理器
     *
     * @return
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager getDefaultWebSessionManager() {
        DefaultWebSessionManager dwsm = new DefaultWebSessionManager();
        dwsm.setGlobalSessionTimeout(10800000);
        dwsm.setDeleteInvalidSessions(true);
        dwsm.setSessionValidationSchedulerEnabled(true);
        dwsm.setSessionDAO(getEnterpriseCacheSessionDAO());
        dwsm.setSessionIdCookieEnabled(true);
        dwsm.setSessionIdCookie(getSessionSimpleCookie());
        return dwsm;
    }

    /**
     * 会话验证调度器
     *
     * @return
     */
    @Bean(name = "sessionValidationScheduler")
    public QuartzSessionValidationScheduler getQuartzSessionValidationScheduler() {
        QuartzSessionValidationScheduler qsvs = new QuartzSessionValidationScheduler();
        qsvs.setSessionValidationInterval(10800000);
        qsvs.setSessionManager(getDefaultWebSessionManager());
        return qsvs;
    }


    /**
     * rememberMe管理器
     *
     * @return
     */
    @Bean(name = "rememberMeManager")
    public CookieRememberMeManager getCookieRememberMeManager() {
        CookieRememberMeManager crm = new CookieRememberMeManager();
        try {
            crm.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        } catch (Base64DecodingException e) {
            e.printStackTrace();
        }
        crm.setCookie(getRememberMeSimpleCookie());
        return crm;
    }

    /**
     * shiro安全管理器
     *
     * @return
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager() {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(getShiroRealm());
        dwsm.setCacheManager(getEhCacheManager());
        dwsm.setSessionManager(getDefaultWebSessionManager());
        dwsm.setRememberMeManager(getCookieRememberMeManager());
        return dwsm;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }


    /**
     * 实现shiro注解的支持
     *
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getDefaultWebSecurityManager());
        return aasa;
    }

    /**
     * shiro的web过滤器
     * <p/>
     * anon   org.apache.shiro.web.filter.authc.AnonymousFilter
     * <p/>
     * authc  org.apache.shiro.web.filter.authc.FormAuthenticationFilter
     * <p/>
     * authcBasic org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
     * <p/>
     * perms  org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
     * <p/>
     * port   org.apache.shiro.web.filter.authz.PortFilter
     * <p/>
     * rest   org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
     * <p/>
     * roles  org.apache.shiro.web.filter.authz.RolesAuthorizationFilter
     * <p/>
     * ssl    org.apache.shiro.web.filter.authz.SslFilter
     * <p/>
     * user   org.apache.shiro.web.filter.authc.UserFilter
     * <p/>
     * logout org.apache.shiro.web.filter.authc.LogoutFilter
     * <p/>
     * anon:例子/admins/**=anon 没有参数，表示可以匿名使用。
     * <p/>
     * authc:例如/admins/user/**=authc表示需要认证(登录)才能使用，没有参数
     * <p/>
     * roles：例子/admins/user/**=roles[admin],参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，当有多个参数时，例如admins/user/**=roles["admin,guest"],每个参数通过才算通过，相当于hasAllRoles()方法。
     * <p/>
     * perms：例子/admins/user/**=perms[user:add:*],参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，例如/admins/user/**=perms["user:add:*,user:modify:*"]，当有多个参数时必须每个参数都通过才通过，想当于isPermitedAll()方法。
     * <p/>
     * rest：例子/admins/user/**=rest[user],根据请求的方法，相当于/admins/user/**=perms[user:method] ,其中method为post，get，delete等。
     * <p/>
     * port：例子/admins/user/**=port[8081],当请求的url的端口不是8081是跳转到schemal://serverName:8081?queryString,其中schmal是协议http或https等，serverName是你访问的host,8081是url配置里port的端口，queryString是你访问的url里的？后面的参数。
     * <p/>
     * authcBasic：例如/admins/user/**=authcBasic没有参数表示httpBasic认证
     * <p/>
     * ssl:例子/admins/user/**=ssl没有参数，表示安全的url请求，协议为https
     * <p/>
     * user:例如/admins/user/**=user没有参数表示必须存在用户，当登入操作时不做检查
     *
     * @return
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(getDefaultWebSecurityManager());
        shiroFilterFactoryBean.setLoginUrl("/login");//登录URL
        shiroFilterFactoryBean.setSuccessUrl("/index");//登录成功跳转URL
        shiroFilterFactoryBean.setUnauthorizedUrl("/login");//登录失败跳转URL

        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
        filters.put("authFilter", new AuthFilter());
        shiroFilterFactoryBean.setFilters(filters);
        filterChainDefinitionMap.put("/login", "authFilter");
        filterChainDefinitionMap.put("/index", "authFilter");
        filterChainDefinitionMap.put("/logout", "logout");

        Properties property = new Properties();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/application.properties");
            property.load(inputStream);
            String authFilter = property.getProperty("auth.filter");
            if (StringUtil.isNotBlank(authFilter)) {
                String[] paths = authFilter.split(";");
                if (paths != null && paths.length > 0) {
                    for (String path : paths) {
                        filterChainDefinitionMap.put(path, "authFilter");
                    }
                }
            } else {
                filterChainDefinitionMap.put("/*", "authFilter");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("load auth path array error.");
        }


        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/captcha/**", "anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


}