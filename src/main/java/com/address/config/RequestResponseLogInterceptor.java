package com.address.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestResponseLogInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogInterceptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // 打印请求信息
        logger.info("========== 请求开始 ==========");
        logger.info("URL: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("请求参数: {}", formatQueryParams(request));

        // 尝试打印请求体（如果有）
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] body = wrapper.getContentAsByteArray();
            if (body.length > 0) {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                logger.info("请求体: {}", formatJson(bodyStr));
            }
        }

        logger.info("================================");

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) {
        // 暂时不需要处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        // 获取响应体
        String responseBody = "";
        if (response instanceof ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
            byte[] body = wrapper.getContentAsByteArray();
            if (body.length > 0) {
                responseBody = new String(body, StandardCharsets.UTF_8);
            }
        }

        // 打印响应信息
        logger.info("========== 响应结束 ==========");
        logger.info("URL: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("状态码: {}", response.getStatus());
        logger.info("耗时: {}ms", duration);

        if (ex != null) {
            logger.error("异常: {}", ex.getMessage());
        }

        if (!responseBody.isEmpty()) {
            logger.info("响应体: {}", formatJson(responseBody));
        }

        logger.info("================================");
    }

    private String formatQueryParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        try {
            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            return params.toString();
        }
    }

    private String formatJson(String jsonStr) {
        try {
            Object obj = objectMapper.readTree(jsonStr);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return jsonStr;
        }
    }
}
