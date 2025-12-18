// 文件：code/src/java/com/petgame/ItemServlet.java
// 背包模块作用：对应 /api/items/list（GET），返回当前玩家拥有的所有道具。
//            后台根据登录用户 ID 查询 user_items 并关联 shop_items 获取名称等信息，返回列表。
//            示例返回字段包含道具 ID、名称、数量、描述等。
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

@WebServlet("/api/items/list")
public class ItemServlet extends HttpServlet {
    private Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取玩家背包中道具列表
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
            String sql = "SELECT ui.item_id, si.name, si.icon, ui.amount " +
                    "FROM user_items ui JOIN shop_items si ON ui.item_id=si.id " +
                    "WHERE ui.user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("itemId", rs.getInt("item_id"));
                it.addProperty("name", rs.getString("name"));
                it.addProperty("icon", rs.getString("icon"));
                it.addProperty("amount", rs.getInt("amount"));
                arr.add(it);
            }
            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", arr);
            out.print(gson.toJson(res));
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
