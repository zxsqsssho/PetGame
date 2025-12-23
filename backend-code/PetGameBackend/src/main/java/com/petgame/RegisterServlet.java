// src/main/java/com/petgame/RegisterServlet.java
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

@WebServlet("/api/user/register")
public class RegisterServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*"); // 允许跨域

        PrintWriter out = resp.getWriter();
        JsonObject responseJson = new JsonObject();

        try {
            // 调试：打印请求信息
            System.out.println("🔥 RegisterServlet 接收到请求");
            System.out.println("Content-Type: " + req.getContentType());
            System.out.println("Content-Length: " + req.getContentLength());

            // 打印所有参数
            System.out.println("请求参数:");
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                System.out.println(paramName + ": " + req.getParameter(paramName));
            }

            String account = null;
            String name = null;
            String password = null;
            String avatarValue = null;

            // 尝试从参数获取（FormData格式）
            account = req.getParameter("username");
            name = req.getParameter("name");
            password = req.getParameter("password");
            avatarValue = req.getParameter("avatarValue");

            // 如果参数为空，尝试从JSON获取
            if (account == null && name == null && password == null) {
                // 读取JSON请求体
                BufferedReader reader = req.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String rawBody = sb.toString();
                System.out.println("JSON Body: " + rawBody);

                if (!rawBody.isEmpty()) {
                    JsonObject json = gson.fromJson(rawBody, JsonObject.class);
                    if (json != null) {
                        account = json.has("username") ? json.get("username").getAsString() : null;
                        name = json.has("name") ? json.get("name").getAsString() : null;
                        password = json.has("password") ? json.get("password").getAsString() : null;
                        avatarValue = json.has("avatar") ? json.get("avatar").getAsString() : null;
                    }
                }
            }

            System.out.println("解析到的参数:");
            System.out.println("账号: " + account);
            System.out.println("昵称: " + name);
            System.out.println("密码: " + (password != null ? "已提供" : "未提供"));
            System.out.println("头像值: " + avatarValue);

            // 验证必要字段
            if (account == null || account.trim().isEmpty() ||
                    name == null || name.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "缺少必要字段：账号、昵称或密码");
                responseJson.add("data", null);
                out.print(responseJson);
                System.out.println("缺少必要字段，返回400");
                return;
            }

            // 清理参数
            account = account.trim();
            name = name.trim();
            password = password.trim();

            // 验证输入
            if (account.length() < 3 || account.length() > 20) {
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "账号长度必须在3-20个字符之间");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            if (password.length() < 6) {
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "密码长度至少6位");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            if (name.length() < 2 || name.length() > 20) {
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "昵称长度需在2-20个字符之间");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            // 处理头像
            String finalAvatar = null;
            if (avatarValue != null && !avatarValue.trim().isEmpty()) {
                // 使用默认头像
                finalAvatar = "/avatars/" + avatarValue.trim() + ".png";
                System.out.println("使用默认头像: " + finalAvatar);
            } else {
                // 使用系统默认头像
                finalAvatar = "/avatars/default.png";
                System.out.println("使用系统默认头像");
            }

            try (Connection conn = DB.getConn()) {
                // 检查账号是否已存在
                String checkSql = "SELECT id FROM users WHERE account = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, account);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    responseJson.addProperty("code", 3);
                    responseJson.addProperty("msg", "账号已存在");
                    responseJson.add("data", null);
                    out.print(responseJson);
                    System.out.println("账号已存在: " + account);
                    return;
                }

                // 插入新用户
                String insertSql = "INSERT INTO users(account, name, password, avatar, coins) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, account);
                insertStmt.setString(2, name);
                insertStmt.setString(3, password);
                insertStmt.setString(4, finalAvatar);
                insertStmt.setInt(5, 1000); // 新用户赠送1000金币

                int rows = insertStmt.executeUpdate();

                if (rows > 0) {
                    // 获取新用户ID
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }

                    // 为新用户创建初始宠物（可选）
                    createInitialPets(conn, userId);

                    // 返回成功响应
                    JsonObject data = new JsonObject();
                    data.addProperty("id", userId);
                    data.addProperty("account", account);
                    data.addProperty("name", name);
                    data.addProperty("avatar", finalAvatar);
                    data.addProperty("coins", 1000);

                    responseJson.addProperty("code", 0);
                    responseJson.addProperty("msg", "注册成功");
                    responseJson.add("data", data);

                    System.out.println("注册成功: " + account + ", ID: " + userId);

                } else {
                    responseJson.addProperty("code", 500);
                    responseJson.addProperty("msg", "注册失败，请重试");
                    responseJson.add("data", null);
                }

                out.print(responseJson);

            } catch (Exception e) {
                e.printStackTrace();

                responseJson.addProperty("code", 500);
                responseJson.addProperty("msg", "服务器异常: " + e.getMessage());
                responseJson.add("data", null);
                out.print(responseJson);
            }

        } catch (Exception e) {
            e.printStackTrace();

            responseJson.addProperty("code", 500);
            responseJson.addProperty("msg", "服务器异常: " + e.getMessage());
            responseJson.add("data", null);
            out.print(responseJson);
        }
    }

    /**
     * 为新用户创建初始宠物
     */
    private void createInitialPets(Connection conn, int userId) {
        try {
            // 为新用户添加一只初始宠物（例如小猫）
            String sql = "INSERT INTO user_pets(user_id, pet_id, nickname, fatigue, fatigue_max, is_active) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, 1); // 宠物ID 1 = 小猫
            ps.setString(3, "我的小猫");
            ps.setInt(4, 0);
            ps.setInt(5, 10);
            ps.setInt(6, 1); // 设为当前携带

            ps.executeUpdate();

            // 为新用户添加一些初始道具
            String itemSql = "INSERT INTO user_items(user_id, item_id, amount) VALUES(?, ?, ?)";
            PreparedStatement itemPs = conn.prepareStatement(itemSql);
            itemPs.setInt(1, userId);
            itemPs.setInt(2, 1); // 鱼干
            itemPs.setInt(3, 5);
            itemPs.addBatch();

            itemPs.setInt(1, userId);
            itemPs.setInt(2, 6); // 普通抽奖券
            itemPs.setInt(3, 1);
            itemPs.addBatch();

            itemPs.executeBatch();

            System.out.println("为用户 " + userId + " 创建初始宠物和道具成功");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("创建初始宠物失败: " + e.getMessage());
        }
    }
}