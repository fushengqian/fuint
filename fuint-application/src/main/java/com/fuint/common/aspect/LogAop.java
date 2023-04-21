package com.fuint.common.aspect;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * 控制器日志
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component  // 声明组件
@Aspect // 声明切面
@ComponentScan  //组件自动扫描
@EnableAspectJAutoProxy // spring自动切换JDK动态代理和CGLIB
public class LogAop {

    /**
     *自定义日志
     */
    private Logger logger = LoggerFactory.getLogger(LogAop.class);

    /**
     * 打印类method的名称以及参数
     * @param point 切面
     */
    public void printMethodParams(JoinPoint point){
        if (point == null) {
            return;
        }
        try {
            // 获取方法的参数值数组。方法名、类型以及地址等信息
            String className = point.getTarget().getClass().getName();
            String methodName = point.getSignature().getName();

            // 重新定义日志
            logger = LoggerFactory.getLogger(point.getTarget().getClass());
            logger.info("-------------------------"+className+"------------------------------------");
            logger.info("methodName = {}", methodName);

            // 获取方法的参数值数组
            Object[] methodArgs = point.getArgs();

            // 获取方法参数名称
            String[] paramNames = getFieldsName(className, methodName);

            // 输出方法的参数名和参数值
            printParams(paramNames, methodArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用获取方法参数名称
     * @param class_name    类名
     * @param method_name   方法名
     * @throws Exception
     */
    private String[] getFieldsName(String class_name, String method_name) throws Exception {
        Class<?> clazz = Class.forName(class_name);
        String clazz_name = clazz.getName();
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(clazz);
        pool.insertClassPath(classPath);
        try {
            CtClass ctClass = pool.get(clazz_name);
            CtMethod ctMethod = ctClass.getDeclaredMethod(method_name);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                return null;
            }
            String[] paramsArgsName = new String[ctMethod.getParameterTypes().length];
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramsArgsName.length; i++) {
                paramsArgsName[i] = attr.variableName(i + pos);
            }
            return paramsArgsName;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 判断是否为基本类型
     */
    private boolean isPrimite(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz == String.class){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 打印方法参数值  基本类型直接打印，非基本类型需要重写toString方法
     * @param paramsArgsName    方法参数名数组
     * @param paramsArgsValue   方法参数值数组
     */
    private void printParams(String[] paramsArgsName, Object[] paramsArgsValue) {
        if (ArrayUtils.isEmpty(paramsArgsName) || ArrayUtils.isEmpty(paramsArgsValue)) {
            return;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < paramsArgsName.length; i++) {
            // 参数名
            String name = paramsArgsName[i];
            // 参数值
            Object value = paramsArgsValue[i];
            buffer.append(name +" = ");
            if (isPrimite(value.getClass())) {
                buffer.append(value + " ,");
            } else {
                buffer.append(value.toString() + " ,");
            }
        }
        logger.info("params : " + buffer.toString());
        logger.info("-------------------------------------------------------------");
    }

    /**
     * 在方法执行前进行切面
     * 定义切面表达式
     * @param point 切面
     */
    @Before("execution(public * com.fuint.module..*.*(..))")
    public void before(JoinPoint point) {
        this.printMethodParams(point);
    }
}
