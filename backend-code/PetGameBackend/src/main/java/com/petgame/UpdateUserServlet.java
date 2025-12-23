// src/main/java/com/petgame/UpdateUserServlet.java
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

@WebServlet("/api/user/update")
public class UpdateUserServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        JsonObject responseJson = new JsonObject();

        try {
            // 检查登录状态
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                responseJson.addProperty("code", 401);
                responseJson.addProperty("msg", "未登录或会话已过期");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            int userId = (int) session.getAttribute("userId");

            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);

            if (json == null) {
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "请求数据格式错误");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            // 构建更新SQL
            StringBuilder sqlBuilder = new StringBuilder("UPDATE users SET ");
            boolean hasUpdate = false;

            if (json.has("name") && !json.get("name").isJsonNull()) {
                String name = json.get("name").getAsString().trim();
                if (!name.isEmpty() && name.length() <= 20) {
                    sqlBuilder.append("name = ?, ");
                    hasUpdate = true;
                }
            }

            if (json.has("avatar") && !json.get("avatar").isJsonNull()) {
                String avatar = json.get("avatar").getAsString();
                if (avatar != null && avatar.length() <= 1000) {
                    sqlBuilder.append("avatar = ?, ");
                    hasUpdate = true;
                }
            }

            if (!hasUpdate) {
                responseJson.addProperty("code", 400);
                responseJson.addProperty("msg", "没有可更新的数据");
                responseJson.add("data", null);
                out.print(responseJson);
                return;
            }

            // 移除最后的逗号和空格
            String sql = sqlBuilder.substring(0, sqlBuilder.length() - 2) + " WHERE id = ?";

            try (Connection conn = DB.getConn()) {
                PreparedStatement ps = conn.prepareStatement(sql);

                int paramIndex = 1;
                if (json.has("name") && !json.get("name").isJsonNull()) {
                    ps.setString(paramIndex++, json.get("name").getAsString().trim());
                }
                if (json.has("avatar") && !json.get("avatar").isJsonNull()) {
                    ps.setString(paramIndex++, json.get("avatar").getAsString());
                }
                ps.setInt(paramIndex, userId);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    responseJson.addProperty("code", 0);
                    responseJson.addProperty("msg", "更新成功");
                    responseJson.add("data", null);
                } else {
                    responseJson.addProperty("code", 500);
                    responseJson.addProperty("msg", "更新失败");
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
}