// 文件：code/src/java/com/petgame/PetServlet.java
// 作用：示例 Servlet，处理 JSON 请求
package com.petgame;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/pets")
public class PetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        // 如果没有单独 Filter，这里也可以设置简单的 CORS 头（推荐使用 Filter）
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        PrintWriter out = resp.getWriter();
        out.print("[{\"id\":1,\"name\":\"Tom\"},{\"id\":2,\"name\":\"Jerry\"}]");
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 读取 JSON 示例（简化）
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print("{\"status\":\"ok\"}");
        out.flush();
    }
}
