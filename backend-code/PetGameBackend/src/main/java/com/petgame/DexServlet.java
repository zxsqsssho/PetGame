// 文件：code/src/java/com/petgame/DexServlet.java
// 图鉴模块作用：接口 /api/dex/list（GET）返回所有宠物的收集状态。后台查询 pets_base 并与 user_pets 左连接（LEFT JOIN）
//            w3schools.com：若用户已有该宠物（user_pets 存在记录），则标记为已收集（collected=true），否则为 false。
//            返回内容示例：[{id,name,icon,collected},...]。
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

@WebServlet("/api/dex/list")
public class DexServlet extends HttpServlet {
    private Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取图鉴收集状态
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        JsonObject res = new JsonObject();
        if (session == null || session.getAttribute("userId")==null) {
            res.addProperty("code", 4);
            res.addProperty("msg", "未登录");
            res.add("data", null);
            out.print(gson.toJson(res));
            return;
        }
        int userId = (int) session.getAttribute("userId");
        try (Connection conn = DB.getConn()) {
            String sql = "SELECT pb.id, pb.name, pb.icon, " +
                    "CASE WHEN up.id IS NULL THEN 0 ELSE 1 END AS collected " +
                    "FROM pets_base pb LEFT JOIN user_pets up " +
                    "ON pb.id = up.pet_id AND up.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject e = new JsonObject();
                e.addProperty("id", rs.getInt("id"));
                e.addProperty("name", rs.getString("name"));
                e.addProperty("icon", rs.getString("icon"));
                e.addProperty("collected", rs.getInt("collected") == 1);
                arr.add(e);
            }
            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", arr);
            out.print(gson.toJson(res));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
