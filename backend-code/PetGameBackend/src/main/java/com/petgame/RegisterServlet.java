package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/api/user/register")
public class RegisterServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        String account = jsonReq.get("username").getAsString();
        String password = jsonReq.get("password").getAsString();

        try (Connection conn = DB.getConn()) {
            // 检查账号是否存在
            String checkSql = "SELECT id FROM users WHERE account=?";
            PreparedStatement ps1 = conn.prepareStatement(checkSql);
            ps1.setString(1, account);
            ResultSet rs = ps1.executeQuery();

            JsonObject res = new JsonObject();

            if (rs.next()) {
                res.addProperty("code", 3);
                res.addProperty("msg", "账号已存在");
                res.add("data", null);
                out.print(res);
                return;
            }

            // 插入用户
            String insertSql = "INSERT INTO users(account,name,password) VALUES(?,?,?)";
            PreparedStatement ps2 = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, account);
            ps2.setString(2, account); // 默认昵称
            ps2.setString(3, password);
            ps2.executeUpdate();

            ResultSet keys = ps2.getGeneratedKeys();
            int newId = keys.next() ? keys.getInt(1) : 0;

            JsonObject data = new JsonObject();
            data.addProperty("id", newId);

            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", data);

            out.print(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
