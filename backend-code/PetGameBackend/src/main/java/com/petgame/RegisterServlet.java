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

@WebServlet("/api/user/register")  // 确保这个路径正确
public class RegisterServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 添加详细的日志输出
        System.out.println("=== RegisterServlet 开始处理 ===");

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // 处理预检请求
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        PrintWriter out = resp.getWriter();
        JsonObject responseJson = new JsonObject();

        try {
            System.out.println("请求URL: " + req.getRequestURI());
            System.out.println("请求方法: " + req.getMethod());
            System.out.println("Content-Type: " + req.getContentType());

            String account = null;
            String name = null;
            String password = null;
            String avatar = "/avatars/txone.jpg"; // 默认选择第一张头像

            // 读取JSON数据
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String rawBody = sb.toString();
            System.out.println("请求体数据: " + rawBody);

            if (!rawBody.isEmpty()) {
                try {
                    JsonObject json = gson.fromJson(rawBody, JsonObject.class);
                    if (json != null) {
                        account = json.has("username") ? json.get("username").getAsString() : null;
                        name = json.has("name") ? json.get("name").getAsString() : null;
                        password = json.has("password") ? json.get("password").getAsString() : null;

                        // 获取头像参数，如果没有则使用默认值
                        if (json.has("avatar") && !json.get("avatar").isJsonNull()) {
                            avatar = json.get("avatar").getAsString();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("JSON解析错误: " + e.getMessage());
                }
            }

            System.out.println("解析参数 - 账号: " + account);
            System.out.println("解析参数 - 昵称: " + name);
            System.out.println("解析参数 - 头像: " + avatar);

            // 验证必要字段
            if (account == null || account.trim().isEmpty() ||
                    name == null || name.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                System.err.println("缺少必要字段");
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "缺少必要字段：账号、昵称或密码");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            // 清理参数
            account = account.trim();
            name = name.trim();
            password = password.trim();

            // 验证头像路径
            String[] validAvatars = {
                    "/avatars/txone.jpg",
                    "/avatars/txtwo.jpg",
                    "/avatars/txthree.jpg"
            };

            boolean validAvatar = false;
            for (String valid : validAvatars) {
                if (valid.equals(avatar)) {
                    validAvatar = true;
                    break;
                }
            }

            if (!validAvatar) {
                // 如果头像无效，使用第一张作为默认
                avatar = "/avatars/txone.jpg";
            }

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

            // 数据库操作
            try (Connection conn = DB.getConn()) {
                System.out.println("数据库连接成功");

                // 检查账号是否已存在
                String checkSql = "SELECT id FROM users WHERE account = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, account);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.err.println("账号已存在: " + account);
                    responseJson.addProperty("code", 3);
                    responseJson.addProperty("msg", "账号已存在");
                    responseJson.add("data", null);
                    out.print(responseJson);
                    return;
                }

                // 插入新用户
                String insertSql = "INSERT INTO users(account, name, password, avatar, coins) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, account);
                insertStmt.setString(2, name);
                insertStmt.setString(3, password);
                insertStmt.setString(4, avatar);
                insertStmt.setInt(5, 1000); // 新用户赠送1000金币

                System.out.println("执行SQL: " + insertSql);
                System.out.println("参数: [" + account + ", " + name + ", " + avatar + ", 1000]");

                int rows = insertStmt.executeUpdate();
                System.out.println("插入行数: " + rows);

                if (rows > 0) {
                    // 获取新用户ID
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        System.out.println("生成用户ID: " + userId);
                    }

                    // 为新用户创建初始宠物
                    try {
                        createInitialPets(conn, userId);
                        System.out.println("初始宠物创建成功");
                    } catch (Exception e) {
                        System.err.println("创建初始宠物失败: " + e.getMessage());
                        // 继续执行，不影响用户注册
                    }

                    // 返回成功响应
                    JsonObject data = new JsonObject();
                    data.addProperty("id", userId);
                    data.addProperty("account", account);
                    data.addProperty("name", name);
                    data.addProperty("avatar", avatar);
                    data.addProperty("coins", 1000);

                    responseJson.addProperty("code", 0);
                    responseJson.addProperty("msg", "注册成功");
                    responseJson.add("data", data);

                    System.out.println("注册成功: " + account + ", ID: " + userId);

                } else {
                    System.err.println("插入用户失败");
                    responseJson.addProperty("code", 500);
                    responseJson.addProperty("msg", "注册失败，请重试");
                    responseJson.add("data", null);
                }

            } catch (Exception e) {
                System.err.println("数据库异常: " + e.getMessage());
                e.printStackTrace();

                responseJson.addProperty("code", 500);
                responseJson.addProperty("msg", "服务器异常: " + e.getMessage());
                responseJson.add("data", null);
            }

            out.print(responseJson);
            System.out.println("返回响应: " + responseJson.toString());

        } catch (Exception e) {
            System.err.println("全局异常: " + e.getMessage());
            e.printStackTrace();

            responseJson.addProperty("code", 500);
            responseJson.addProperty("msg", "服务器异常: " + e.getMessage());
            responseJson.add("data", null);
            out.print(responseJson);
        }

        System.out.println("=== RegisterServlet 处理结束 ===");
    }

    /**
     * 为新用户创建初始宠物
     */
    private void createInitialPets(Connection conn, int userId) {
        try {
            // 为新用户添加一只初始宠物（小猫）
            String petSql = "INSERT INTO user_pets(user_id, pet_id, nickname, fatigue, fatigue_max, is_active) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement petPs = conn.prepareStatement(petSql);
            petPs.setInt(1, userId);
            petPs.setInt(2, 1); // 宠物ID 1 = 小猫
            petPs.setString(3, "我的小猫");
            petPs.setInt(4, 0);
            petPs.setInt(5, 10);
            petPs.setInt(6, 1); // 设为当前携带

            petPs.executeUpdate();

            // 为新用户添加初始道具
            String itemSql = "INSERT INTO user_items(user_id, item_id, amount) VALUES(?, ?, ?)";
            PreparedStatement itemPs = conn.prepareStatement(itemSql);

            // 添加鱼干
            itemPs.setInt(1, userId);
            itemPs.setInt(2, 1); // 鱼干
            itemPs.setInt(3, 5);
            itemPs.addBatch();

            // 添加普通抽奖券
            itemPs.setInt(1, userId);
            itemPs.setInt(2, 6); // 普通抽奖券
            itemPs.setInt(3, 1);
            itemPs.addBatch();

            itemPs.executeBatch();

        } catch (Exception e) {
            throw new RuntimeException("创建初始宠物失败: " + e.getMessage(), e);
        }
    }
}