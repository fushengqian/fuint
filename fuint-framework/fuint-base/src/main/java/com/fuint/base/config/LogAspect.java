package com.fuint.base.config;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.entities.TActionLog;
import com.fuint.base.service.log.TActionLogService;
import com.fuint.base.shiro.ShiroUser;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 操作日志AOP实现
 * s
 *
 * @author FSQ
 * Contact wx fsq_better
 * @version $Id: LogAspect.java
 */
@Component
@Aspect
public class LogAspect {

    private Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Lazy
    @Autowired
    private TActionLogService tActionLogService;

    private String userName = ""; // 用户名
    private Long startTimeMillis = 0l; // 开始时间
    private Long endTimeMillis = 0l; // 结束时间
    private String clientIp = "";
    private Integer clientPort = 0;
    private String module = "";
    private String url = "";
    private String userAgent = "";

    //Service层切点
    @Pointcut("@annotation(com.fuint.base.annoation.OperationServiceLog)")
    public void serviceAspect() {
        // empty
    }

    /**
     * service 方法前调用
     *
     * @param joinPoint
     */
    @Before("serviceAspect()")
    public void doBeforeService(JoinPoint joinPoint) {
        startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
    }

    /**
     * 方法后调用
     *
     * @param joinPoint
     */
    @After("serviceAspect() && @annotation(operationServiceLog)")
    public void doAfterInService(JoinPoint joinPoint, OperationServiceLog operationServiceLog) {
        try {
            endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
            Subject sub = SecurityUtils.getSubject();
            ShiroUser user = (ShiroUser) sub.getPrincipal();
            Session session = sub.getSession(true);
            clientIp = session.getHost();

            if (user != null) {
                userName = user.getAcctName();

                userAgent = user.getUserAgent();
                if (userAgent.length() > 255) {
                    userAgent = userAgent.substring(0, 255);
                }

                clientPort = user.getClientPort();

                module = operationServiceLog.description();
                if (module.length() > 255) {
                    module = module.substring(0, 255);
                }

                url = user.getRequestURL();
                if (url.length() > 255) {
                    url = url.substring(0, 255);
                }
            }

            this.printOptLog();
        } catch (Exception e) {
            LOGGER.error("操作日志记录失败啦：" + e.getMessage());
        }
    }

    /**
     * 组装日志
     */
    private void printOptLog() {
        TActionLog hal = new TActionLog();
        hal.setAcctName(userName);
        hal.setModule(module);
        hal.setActionTime(new Date());
        hal.setClientIp(clientIp);
        hal.setClientPort(clientPort);
        hal.setUrl(url);
        hal.setTimeConsuming(endTimeMillis - startTimeMillis);
        hal.setUserAgent(userAgent);
        if (StringUtils.isNotEmpty(module)) {
            this.tActionLogService.saveActionLog(hal);
        }
    }
}
