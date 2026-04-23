package com.address.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequestResponseLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.address.controller..*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();

        String url = request.getMethod() + " " + request.getRequestURI();
        String args = formatArgs(joinPoint.getArgs());

        logger.info("==> 请求 URL: {}", url);
        logger.info("==> 请求参数: {}", args);

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            if (error != null) {
                logger.error("<== 响应异常: {} (耗时: {}ms)", error.getMessage(), cost);
            } else {
                logger.info("<== 响应结果: {} (耗时: {}ms)", formatArgs(new Object[]{result}), cost);
            }
        }
    }

    private String formatArgs(Object[] args) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(args);
        } catch (Exception e) {
            return java.util.Arrays.toString(args);
        }
    }
}
