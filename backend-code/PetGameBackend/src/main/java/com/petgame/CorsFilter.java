// 文件：code/src/java/com/petgame/CorsFilter.java
// 作用：统一处理 CORS，推荐注册为 Filter（可通过 @WebFilter 或 web.xml 注册）
package com.petgame;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;

        // ★ 必须是你的前端地址，不能用 "*"
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");

        // ★ 必须允许 cookie/session
        resp.setHeader("Access-Control-Allow-Credentials", "true");

        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        resp.setHeader("Access-Control-Max-Age", "3600");

        chain.doFilter(request, response);
    }
}
