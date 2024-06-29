package com.fuint.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.ActionLogService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.repository.model.TActionLog;
import com.fuint.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.javassist.*;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台操作日志
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@Aspect
public class TActionLogAop {

    private Logger logger = LoggerFactory.getLogger(TActionLogAop.class);

    @Lazy
    @Autowired
    private ActionLogService tActionLogService;

    @Lazy
    @Autowired
    private AccountService tAccountService;

    private String userName = ""; // 用户名
    private Integer merchantId = 0; // 商户ID
    private Integer storeId = 0; // 店铺ID
    private Long startTimeMillis = 0l; // 开始时间
    private Long endTimeMillis = 0l; // 结束时间
    private String clientIp = "";
    private Integer clientPort = 0;
    private String module = "";
    private String url = "";
    private String userAgent = "";
    private String param = "";

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
        // 记录方法开始执行的时间
        startTimeMillis = System.currentTimeMillis();

        Map<String, String> params = getJoinPointPramas(joinPoint);
        String methodName = params.get("methodName");
        String classPath = params.get("classPath");
        Class<?> clazz = null;
        CtMethod ctMethod = null;
        LocalVariableAttribute attr = null;
        int length = 0;
        int pos = 0;

        try {
            //获取切入点参数
            clazz = Class.forName(classPath);
            String clazzName = clazz.getName();
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classClassPath = new ClassClassPath(clazz);
            pool.insertClassPath(classClassPath);
            CtClass ctClass = pool.get(clazzName);
            ctMethod = ctClass.getDeclaredMethod(methodName);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            length = ctMethod.getParameterTypes().length;
            pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            Object[] paramsArgsValues = joinPoint.getArgs();
            String[] parmasArgsNames = new String[length];
            Map<String, Object> parmasMap = new HashMap<String, Object>();
            for (int i = 0; i < length; i++) {
                parmasArgsNames[i] = attr.variableName(i + pos);
                String paramsArgsName = attr.variableName(i + pos);
                if (paramsArgsName.equalsIgnoreCase("request")
                        || paramsArgsName.equalsIgnoreCase("response")
                        || paramsArgsName.equalsIgnoreCase("session")
                        || paramsArgsName.equalsIgnoreCase("model")) {
                    continue;
                }
                Object paramsArgsValue = paramsArgsValues[i];
                parmasMap.put(paramsArgsName, paramsArgsValue);
            }
            param = JSON.toJSONString(parmasMap);
        } catch (ClassNotFoundException e) {
            logger.info("AOP切入点获取参数异常", e);
        } catch (NotFoundException e) {
            logger.info("AOP切入点获取参数异常", e);
        } catch (Exception e) {
            logger.info("AOP切入点获取参数异常", e.getMessage());
        }
    }

    /**
     * 方法后调用
     *
     * @param operationServiceLog
     */
    @After("serviceAspect() && @annotation(operationServiceLog)")
    public void doAfterInService(OperationServiceLog operationServiceLog) {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return;
            }
            endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
            clientIp = CommonUtil.getIPFromHttpRequest(request);
            userAgent = request.getHeader("user-agent");
            url = request.getRequestURI();
            clientPort = 0;
            module = operationServiceLog.description();
            if (module.length() > 255) {
                module = module.substring(0, 255);
            }
            String token = request.getHeader("Access-Token");
            if (StringUtils.isNotEmpty(token)) {
                AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
                if (accountInfo != null) {
                    userName = accountInfo.getAccountName();
                    merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
                    storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
                }
            } else {
                if (StringUtil.isNotEmpty(param) && param.length() > 10) {
                    JSONObject jsonObject = JSON.parseObject(param);
                    if (jsonObject != null) {
                        JSONObject tAccount = jsonObject.getJSONObject("tAccount");
                        if (tAccount != null) {
                            String accountName = tAccount.getString("username");
                            AccountInfo accountInfo = tAccountService.getAccountByName(accountName);
                            if (accountInfo != null) {
                                userName = accountInfo.getAccountName();
                                merchantId = accountInfo.getMerchantId();
                                storeId = accountInfo.getStoreId();
                            }
                        }
                    }
                }
            }
            printOptLog();
        } catch (Exception e) {
            logger.error("保存后台日志出错啦：{}", e.getMessage());
            e.printStackTrace();
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
        hal.setMerchantId(merchantId);
        hal.setStoreId(storeId);
        if (param.length() > 10000) {
            param = param.substring(0, 10000);
        }
        hal.setParam(param.equals("{}") ? "" : param);
        if (StringUtils.isNotEmpty(module) && userName != null && StringUtils.isNotEmpty(userName)) {
            tActionLogService.saveActionLog(hal);
        }
    }

    protected HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取切入点参数信息
     *
     * @param joinPoint
     * @return
     */
    public Map<String, String> getJoinPointPramas(JoinPoint joinPoint) {
        Map<String, String> mapParams = new HashMap<String, String>();
        // 获取切入点所在的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method smethod = signature.getMethod();
        String classPath = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        mapParams.put("module", module);
        mapParams.put("classPath", classPath);
        mapParams.put("methodName", methodName);
        return mapParams;
    }
}
