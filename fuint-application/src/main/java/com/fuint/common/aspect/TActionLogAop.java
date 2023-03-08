package com.fuint.common.aspect;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.ActionLogService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.repository.model.TActionLog;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 后台操作日志
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@Aspect
public class TActionLogAop {

    private Logger LOGGER = LoggerFactory.getLogger(TActionLogAop.class);

    @Lazy
    @Autowired
    private ActionLogService tActionLogService;

    private String userName = ""; // 用户名
    private Long startTimeMillis = 0l; // 开始时间
    private Long endTimeMillis = 0l; // 结束时间
    private String clientIp = "";
    private Integer clientPort = 0;
    private String module = "";
    private String url = "";
    private String userAgent = "";

    // Service层切点
    @Pointcut("@annotation(com.fuint.framework.annoation.OperationServiceLog)")
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
     * @param operationServiceLog
     */
    @After("serviceAspect() && @annotation(operationServiceLog)")
    public void doAfterInService(OperationServiceLog operationServiceLog) {
        try {
            endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
            clientIp = CommonUtil.getIPFromHttpRequest(getRequest());
            userAgent = getRequest().getHeader("user-agent");
            url = getRequest().getRequestURI();
            clientPort = 0;
            module = operationServiceLog.description();
            if (module.length() > 255) {
                module = module.substring(0, 255);
            }
            HttpServletRequest request = getRequest();
            String token = request.getHeader("Access-Token");
            if (StringUtils.isNotEmpty(token)) {
                AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
                userName = accountInfo.getAccountName();
            }
            this.printOptLog();
        } catch (Exception e) {
            // empty
        }
    }

    /**
     * 组装日志
     */
    private void printOptLog() {
        if (userAgent.length() > 255) {
            userAgent = userAgent.substring(0, 255);
        }
        if (url.length() > 255) {
            url = url.substring(0, 255);
        }
        TActionLog hal = new TActionLog();
        hal.setAcctName(userName);
        hal.setModule(module);
        hal.setActionTime(new Date());
        hal.setClientIp(clientIp);
        hal.setClientPort(clientPort);
        hal.setUrl(url);
        hal.setTimeConsuming(new BigDecimal(endTimeMillis - startTimeMillis));
        hal.setUserAgent(userAgent);
        if (StringUtils.isNotEmpty(module) && userName != null && StringUtils.isNotEmpty(userName)) {
            this.tActionLogService.saveActionLog(hal);
        }
    }

    protected HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }
}
