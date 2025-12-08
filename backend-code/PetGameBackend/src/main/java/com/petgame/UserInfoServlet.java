package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/user/info")
public class UserInfoServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        int userId = (Integer) session.getAttribute("userId");

        try (Connection conn = DB.getConn()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                res.addProperty("code", 5);
                res.addProperty("msg", "用户不存在");
                res.add("data", null);

            } else {
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
            }

            out.print(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
