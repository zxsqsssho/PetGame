// 文件：code/src/java/com/petgame/PetServlet.java
// 作用：宠物模块，提供用户的宠物列表和喂食操作
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(urlPatterns={"/api/pets/list", "/api/pets/feed"})
public class PetServlet extends HttpServlet {
    private Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取用户宠物列表
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
            // 联合 pets_base 查询宠物信息
            String sql = "SELECT up.id AS upid, up.pet_id, up.nickname, pb.name, pb.rarity, pb.icon " +
                    "FROM user_pets up JOIN pets_base pb ON up.pet_id=pb.id WHERE up.user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject pet = new JsonObject();
                pet.addProperty("id", rs.getInt("upid"));           // 用户宠物记录ID
                pet.addProperty("petId", rs.getInt("pet_id"));     // 宠物图鉴ID
                pet.addProperty("name", rs.getString("name"));
                pet.addProperty("rarity", rs.getInt("rarity"));
                pet.addProperty("icon", rs.getString("icon"));
                String nick = rs.getString("nickname");
                pet.addProperty("nickname", nick != null ? nick : "");
                arr.add(pet);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 喂食宠物：示意减少宠物疲劳（具体逻辑可扩展）
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        int petRecId = jsonReq.get("petId").getAsInt(); // 用户宠物唯一ID
        // 此处示例：没有实际食物消耗逻辑，仅返回成功
        JsonObject data = new JsonObject();
        data.addProperty("petId", petRecId);
        data.addProperty("message", "喂食成功，宠物疲劳减少");
        JsonObject res = new JsonObject();
        res.addProperty("code", 0);
        res.addProperty("msg", "success");
        res.add("data", data);
        out.print(gson.toJson(res));
    }
}

