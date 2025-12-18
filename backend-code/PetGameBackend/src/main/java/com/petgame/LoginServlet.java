//src/main/java/com/petgame/LoginServlet.java
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/user/login")
public class LoginServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter out = resp.getWriter();

        // ---------------------------
        // 1. 读取原始 JSON Body，并打印到控制台
        // ---------------------------
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String rawBody = sb.toString();
        System.out.println("🔥 LoginServlet RAW BODY = " + rawBody);

        // ---------------------------
        // 2. 解析 JSON（增加安全判断）
        // ---------------------------
        JsonObject json;
        try {
            json = gson.fromJson(rawBody, JsonObject.class);
        } catch (Exception e) {
            json = null;
        }

        if (json == null) {
            JsonObject err = new JsonObject();
            err.addProperty("code", 500);
            err.addProperty("msg", "JSON 解析失败（body为空或格式错误）");
            err.add("data", null);
            out.print(err);
            return;
        }

        // ---------------------------
        // 3. 获取字段
        // ---------------------------
        if (!json.has("username") || !json.has("password")) {
            JsonObject err = new JsonObject();
            err.addProperty("code", 500);
            err.addProperty("msg", "缺少必要字段 username/password");
            err.add("data", null);
            out.print(err);
            return;
        }

        String account = json.get("username").getAsString();
        String password = json.get("password").getAsString();

        // ---------------------------
        // 4. 登录逻辑
        // ---------------------------
        try (Connection conn = DB.getConn()) {

            String sql = "SELECT id,name,password,avatar,coins FROM users WHERE account=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();

            JsonObject res = new JsonObject();

            if (!rs.next()) {
                res.addProperty("code", 2);
                res.addProperty("msg", "用户不存在");
                res.add("data", null);
                out.print(res);
                return;
            }

            if (!password.equals(rs.getString("password"))) {
                res.addProperty("code", 1);
                res.addProperty("msg", "密码错误");
                res.add("data", null);
                out.print(res);
                return;
            }

            // ---------------------------
            // 5. 设置 Session
            // ---------------------------
            HttpSession session = req.getSession();
            session.setAttribute("userId", rs.getInt("id"));

            // ---------------------------
            // 6. 返回成功
            // ---------------------------
            JsonObject data = new JsonObject();
            data.addProperty("id", rs.getInt("id"));
            data.addProperty("account", account);
            data.addProperty("name", rs.getString("name"));
            data.addProperty("avatar", rs.getString("avatar"));
            data.addProperty("coins", rs.getInt("coins"));

            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", data);

            out.print(res);

        } catch (Exception e) {
        e.printStackTrace();

        JsonObject err = new JsonObject();
        err.addProperty("code", 500);
        err.addProperty("msg", "服务器异常：" + e.getMessage());
        err.add("data", null);

        out.print(err);
    }

}
}
