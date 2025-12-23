// src/main/java/com/petgame/LoginServlet.java
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
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
        JsonObject responseJson = new JsonObject();

        try {
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String rawBody = sb.toString();
            System.out.println("🔥 LoginServlet RAW BODY = " + rawBody);

            // 解析JSON
            JsonObject json = gson.fromJson(rawBody, JsonObject.class);

            // 验证必要字段
            if (json == null ||
                    !json.has("username") ||
                    !json.has("password")) {

                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "缺少必要字段：username, password");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            // 获取字段
            String account = json.get("username").getAsString().trim();
            String password = json.get("password").getAsString().trim();

            // 输入验证
            if (account.isEmpty() || password.isEmpty()) {
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "账号和密码不能为空");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            try (Connection conn = DB.getConn()) {
                // 查询用户信息
                String sql = "SELECT id, name, password, avatar, coins FROM users WHERE account = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, account);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    responseJson.addProperty("code", 2);
                    responseJson.addProperty("msg", "用户不存在");
                    responseJson.add("data", null);
                    out.print(responseJson);
                    return;
                }

                // 验证密码（明文比较）
                String storedPassword = rs.getString("password");
                if (!password.equals(storedPassword)) {
                    responseJson.addProperty("code", 1);
                    responseJson.addProperty("msg", "密码错误");
                    responseJson.add("data", null);
                    out.print(responseJson);
                    return;
                }

                // 设置Session
                HttpSession session = req.getSession(true);
                session.setAttribute("userId", rs.getInt("id"));
                session.setAttribute("userAccount", account);
                session.setAttribute("userName", rs.getString("name"));

                // 设置session超时时间（30分钟）
                session.setMaxInactiveInterval(30 * 60);

                // 构建返回数据
                JsonObject data = new JsonObject();
                data.addProperty("id", rs.getInt("id"));
                data.addProperty("account", account);
                data.addProperty("name", rs.getString("name"));

                // 处理头像URL
                String avatar = rs.getString("avatar");
                if (avatar == null || avatar.isEmpty()) {
                    avatar = "/avatars/default.png";
                }
                data.addProperty("avatar", avatar);

                data.addProperty("coins", rs.getInt("coins"));
                data.addProperty("sessionId", session.getId());

                responseJson.addProperty("code", 0);
                responseJson.addProperty("msg", "登录成功");
                responseJson.add("data", data);

                out.print(responseJson);

            } catch (Exception e) {
                e.printStackTrace();

                responseJson.addProperty("code", 500);
                responseJson.addProperty("msg", "服务器异常：" + e.getMessage());
                responseJson.add("data", null);
                out.print(responseJson);
            }

        } catch (Exception e) {
            e.printStackTrace();

            responseJson.addProperty("code", 500);
            responseJson.addProperty("msg", "服务器异常：" + e.getMessage());
            responseJson.add("data", null);
            out.print(responseJson);
        }
    }
}