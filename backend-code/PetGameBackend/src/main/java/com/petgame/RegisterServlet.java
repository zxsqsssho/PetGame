package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/user/register")
public class RegisterServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(req.getInputStream(), "UTF-8"));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        PrintWriter out = resp.getWriter();
        JsonObject res = new JsonObject();

        if (json == null || !json.has("username") || !json.has("password")) {
            res.addProperty("code", 400);
            res.addProperty("msg", "请求体无效");
            res.add("data", null);
            out.print(res);
            return;
        }

        String account = json.get("username").getAsString();
        String password = json.get("password").getAsString();

        try (Connection conn = DB.getConn()) {
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM users WHERE account=?");
            check.setString(1, account);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                res.addProperty("code", 3);
                res.addProperty("msg", "账号已存在");
                res.add("data", null);
                out.print(res);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(account,name,password) VALUES(?,?,?)");

            ps.setString(1, account);
            ps.setString(2, account);
            ps.setString(3, password);
            ps.executeUpdate();

            res.addProperty("code", 0);
            res.addProperty("msg", "注册成功");
            res.add("data", null);
            out.print(res);

        } catch (Exception e) {
            e.printStackTrace();

            JsonObject err = new JsonObject();
            err.addProperty("code", 500);
            err.addProperty("msg", "服务器异常: " + e.getMessage());
            err.add("data", null);

            out.print(err);
        }

    }
}
