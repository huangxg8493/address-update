package com.address.config;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class CachingRequestResponseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // 只过滤 JSON 请求
            String contentType = httpRequest.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                ContentCachingRequestWrapper requestWrapper =
                    new ContentCachingRequestWrapper(httpRequest);
                ContentCachingResponseWrapper responseWrapper =
                    new ContentCachingResponseWrapper(httpResponse);

                chain.doFilter(requestWrapper, responseWrapper);

                // 重要：复制响应体到原始响应
                responseWrapper.copyBodyToResponse();
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
