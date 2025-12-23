// 文件：code/src/java/com/petgame/ExploreServlet.java
// 修改探索模块，获取item/pet对应的名称
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet(urlPatterns={"/api/explore", "/api/explore/locations"})
public class ExploreServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        System.out.println("ExploreServlet GET path: " + path); // 调试信息
        if ("/api/explore/locations".equals(path)) {
            // 获取探索地点列表
            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            JsonObject res = new JsonObject();

            try (Connection conn = DB.getConn()) {
                String sql = "SELECT id, name, description, icon FROM locations ORDER BY id";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
                while (rs.next()) {
                    JsonObject loc = new JsonObject();
                    loc.addProperty("id", rs.getInt("id"));
                    loc.addProperty("name", rs.getString("name"));
                    loc.addProperty("description", rs.getString("description"));
                    loc.addProperty("icon", rs.getString("icon") != null ? rs.getString("icon") : "🌳");
                    loc.addProperty("fatigueCost", 5); // 默认疲劳消耗
                    arr.add(loc);
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
                out.print(gson.toJson(err));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 探索地点并获取奖励
        resp.setContentType("application/json;charset=UTF-8");
        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        int locId = jsonReq.get("locationId").getAsInt();
        HttpSession session = req.getSession(false);
        PrintWriter out = resp.getWriter();
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
            // 查询该地点所有奖励
            String sql = "SELECT type, item_id, amount, weight FROM explore_rewards WHERE location_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, locId);
            ResultSet rs = ps.executeQuery();

            // 累加权重并存储记录
            List<JsonObject> rewards = new ArrayList<>();
            int totalWeight = 0;
            while (rs.next()) {
                JsonObject rew = new JsonObject();
                rew.addProperty("type", rs.getString("type"));
                rew.addProperty("itemId", rs.getObject("item_id") != null ? rs.getInt("item_id") : 0);
                rew.addProperty("amount", rs.getInt("amount"));
                int w = rs.getInt("weight");
                totalWeight += w;
                rew.addProperty("cumWeight", totalWeight);
                rewards.add(rew);
            }

            if (rewards.isEmpty()) {
                throw new Exception("该地点没有奖励配置");
            }

            // 随机抽取
            Random rand = new Random();
            int r = rand.nextInt(totalWeight) + 1;
            JsonObject picked = null;
            for (JsonObject rew : rewards) {
                if (r <= rew.get("cumWeight").getAsInt()) {
                    picked = rew;
                    break;
                }
            }

            if (picked == null) {
                throw new Exception("未找到奖励配置");
            }

            // 更新用户数据
            String type = picked.get("type").getAsString();
            int itemId = picked.get("itemId").getAsInt();
            int amount = picked.get("amount").getAsInt();

            JsonObject data = new JsonObject();
            data.addProperty("type", type);
            data.addProperty("amount", amount);
            data.addProperty("itemId", itemId);

            // 根据类型获取对应的名称
            String itemName = "";
            if ("coin".equals(type)) {
                // 增加金币
                String up = "UPDATE users SET coins = coins + ? WHERE id=?";
                PreparedStatement ps2 = conn.prepareStatement(up);
                ps2.setInt(1, amount);
                ps2.setInt(2, userId);
                ps2.executeUpdate();
                itemName = amount + "金币";
                data.addProperty("message", "获得金币: " + amount);
            } else if ("item".equals(type)) {
                // 查询食物名称
                String nameSql = "SELECT name FROM shop_items WHERE id=?";
                PreparedStatement namePs = conn.prepareStatement(nameSql);
                namePs.setInt(1, itemId);
                ResultSet nameRs = namePs.executeQuery();
                if (nameRs.next()) {
                    itemName = nameRs.getString("name");
                } else {
                    itemName = "未知道具";
                }
                namePs.close();

                // 增加道具
                String sel = "SELECT id,amount FROM user_items WHERE user_id=? AND item_id=?";
                PreparedStatement ps3 = conn.prepareStatement(sel);
                ps3.setInt(1, userId);
                ps3.setInt(2, itemId);
                ResultSet rs3 = ps3.executeQuery();

                if (rs3.next()) {
                    String up2 = "UPDATE user_items SET amount = amount + ? WHERE id=?";
                    PreparedStatement ps4 = conn.prepareStatement(up2);
                    ps4.setInt(1, amount);
                    ps4.setInt(2, rs3.getInt("id"));
                    ps4.executeUpdate();
                } else {
                    String ins = "INSERT INTO user_items(user_id,item_id,amount) VALUES(?,?,?)";
                    PreparedStatement ps4 = conn.prepareStatement(ins);
                    ps4.setInt(1, userId);
                    ps4.setInt(2, itemId);
                    ps4.setInt(3, amount);
                    ps4.executeUpdate();
                }
                data.addProperty("itemName", itemName);
                data.addProperty("message", "获得" + itemName + " x" + amount);
            } else if ("pet".equals(type)) {
                // 查询宠物名称
                String nameSql = "SELECT name FROM pets_base WHERE id=?";
                PreparedStatement namePs = conn.prepareStatement(nameSql);
                namePs.setInt(1, itemId);
                ResultSet nameRs = namePs.executeQuery();
                if (nameRs.next()) {
                    itemName = nameRs.getString("name");
                } else {
                    itemName = "未知宠物";
                }
                namePs.close();

                // 增加宠物
                String ins2 = "INSERT INTO user_pets(user_id,pet_id, fatigue, fatigue_max) VALUES(?,?,0,10)";
                PreparedStatement ps5 = conn.prepareStatement(ins2);
                ps5.setInt(1, userId);
                ps5.setInt(2, itemId);
                ps5.executeUpdate();

                data.addProperty("itemName", itemName);
                data.addProperty("message", "获得新宠物：" + itemName);
            }

            // 返回结果
            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", data);
//            out.print(gson.toJson(res));

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject err = new JsonObject();
            err.addProperty("code", 500);
            err.addProperty("msg", "服务器异常: " + e.getMessage());
            err.add("data", null);
            out.print(gson.toJson(err));
        }
    }
}