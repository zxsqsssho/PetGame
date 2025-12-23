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
    private static final String LOCAL_ASSETS_PATH = "/avatars/";
    private static final String DEFAULT_AVATAR = "txone.jpg";

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

                // 处理头像路径 - 直接使用数据库存储的本地路径
                String avatar = rs.getString("avatar");

                // 如果数据库中没有头像或头像为空，使用默认本地路径
                if (avatar == null || avatar.trim().isEmpty()) {
                    avatar = LOCAL_ASSETS_PATH + DEFAULT_AVATAR;
                    System.out.println("使用默认头像: " + avatar);
                }

                // 确保头像路径是正确的本地路径
                if (!avatar.startsWith("C:")) {
                    // 如果不是本地路径，尝试修正为本地路径
                    String fileName = avatar.contains("/") ?
                            avatar.substring(avatar.lastIndexOf("/") + 1) :
                            avatar;
                    avatar = LOCAL_ASSETS_PATH + fileName;
                }

                // 检查头像文件是否存在
                java.io.File avatarFile = new java.io.File(avatar);
                if (!avatarFile.exists()) {
                    System.err.println("头像文件不存在: " + avatar);
                    avatar = LOCAL_ASSETS_PATH + DEFAULT_AVATAR;
                    System.out.println("使用默认头像: " + avatar);
                }

                // 返回给前端的是文件名（不含路径），前端通过专门的Servlet获取图片
                String avatarFileName = avatar.substring(avatar.lastIndexOf("\\") + 1);
                data.addProperty("avatar", avatarFileName);

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