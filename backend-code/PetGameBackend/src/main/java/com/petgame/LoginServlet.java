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
import java.io.InputStreamReader;
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

        JsonObject json = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        String account = json.get("username").getAsString();
        String password = json.get("password").getAsString();

        try (Connection conn = DB.getConn()) {
            String sql = "SELECT id,name,password,avatar,level,coins,exp FROM users WHERE account=?";
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

            // 登录成功
            int userId = rs.getInt("id");
            HttpSession session = req.getSession();
            session.setAttribute("userId", userId);

            JsonObject data = new JsonObject();
            data.addProperty("id", userId);
            data.addProperty("account", account);
            data.addProperty("name", rs.getString("name"));
            data.addProperty("avatar", rs.getString("avatar"));
            data.addProperty("level", rs.getInt("level"));
            data.addProperty("coins", rs.getInt("coins"));
            data.addProperty("exp", rs.getInt("exp"));

            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", data);

            out.print(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
