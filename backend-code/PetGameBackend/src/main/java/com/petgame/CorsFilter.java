// 文件：code/src/java/com/petgame/CorsFilter.java
// 作用：统一处理 CORS，推荐注册为 Filter（可通过 @WebFilter 或 web.xml 注册）
package com.petgame;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        resp.setHeader("Access-Control-Max-Age", "3600");

        // 对预检请求直接返回 200
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(200);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
