package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/user/info")
public class UserInfoServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);

        JsonObject res = new JsonObject();
        if (session == null || session.getAttribute("userId") == null) {
            res.addProperty("code", 4);
            res.addProperty("msg", "未登录");
            res.add("data", null);
            out.print(res);
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection conn = DB.getConn()) {
            String sql = "SELECT id,account,name,avatar,level,coins,exp FROM users WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JsonObject data = new JsonObject();
                data.addProperty("id", rs.getInt("id"));
                data.addProperty("account", rs.getString("account"));
                data.addProperty("name", rs.getString("name"));
                data.addProperty("avatar", rs.getString("avatar"));
                data.addProperty("level", rs.getInt("level"));
                data.addProperty("coins", rs.getInt("coins"));
                data.addProperty("exp", rs.getInt("exp"));

                res.addProperty("code", 0);
                res.addProperty("msg", "success");
                res.add("data", data);
            } else {
                res.addProperty("code", 5);
                res.addProperty("msg", "用户未找到");
                res.add("data", null);
            }

            out.print(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
