// 文件：code/src/java/com/petgame/ExploreServlet.java
// 作用：探索模块，实现 /api/explore（POST）接口，用于探索某地点并获得奖励
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

@WebServlet("/api/explore")
public class ExploreServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        int locId = jsonReq.get("locationId").getAsInt();
        HttpSession session = req.getSession(false);
        PrintWriter out = resp.getWriter();
        JsonObject res = new JsonObject();

        if (session == null || session.getAttribute("userId") == null) {
            res.addProperty("code", 4);
            res.addProperty("msg", "未登录");
            res.add("data", null);
            out.print(gson.toJson(res));
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection conn = DB.getConn()) {

            // === 新增：检查用户是否携带了活跃宠物 ===
            String getActivePetSql = "SELECT id, fatigue, fatigue_max FROM user_pets WHERE user_id = ? AND is_active = 1";
            PreparedStatement activePs = conn.prepareStatement(getActivePetSql);
            activePs.setInt(1, userId);
            ResultSet activeRs = activePs.executeQuery();

            if (!activeRs.next()) {
                res.addProperty("code", 8);
                res.addProperty("msg", "请先携带一只宠物才能探索");
                res.add("data", null);
                out.print(gson.toJson(res));
                return;
            }

            int petRecId = activeRs.getInt("id");
            int currentFatigue = activeRs.getInt("fatigue");
            int maxFatigue = activeRs.getInt("fatigue_max");

            // 根据地点确定疲劳消耗（与前端 Explore.vue 中的 fatigue 值一致）
            int fatigueCost;
            switch (locId) {
                case 1: fatigueCost = 10; break; // 公园
                case 2: fatigueCost = 15; break; // 神秘湖泊
                case 3: fatigueCost = 20; break; // 遗迹
                default: fatigueCost = 10;
            }

            // 判断是否超过疲劳上限
            if (currentFatigue + fatigueCost > maxFatigue) {
                res.addProperty("code", 9);
                res.addProperty("msg", "宠物疲劳已达上限，无法探索");
                res.add("data", null);
                out.print(gson.toJson(res));
                return;
            }

            // 更新宠物疲劳值
            int newFatigue = currentFatigue + fatigueCost;
            String updateFatigueSql = "UPDATE user_pets SET fatigue = ? WHERE id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateFatigueSql);
            updatePs.setInt(1, newFatigue);
            updatePs.setInt(2, petRecId);
            updatePs.executeUpdate();
            // === 疲劳处理结束 ===

            // === 原有探索奖励逻辑（保持不变）===
            String sql = "SELECT type, item_id, amount, weight FROM explore_rewards WHERE location_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, locId);
            ResultSet rs = ps.executeQuery();

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

            if (totalWeight == 0) {
                res.addProperty("code", 0);
                res.addProperty("msg", "该地点暂无奖励");
                res.add("data", new JsonObject());
                out.print(gson.toJson(res));
                return;
            }

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
                res.addProperty("code", 0);
                res.addProperty("msg", "探索成功，但未获得奖励");
                res.add("data", new JsonObject());
                out.print(gson.toJson(res));
                return;
            }

            String type = picked.get("type").getAsString();
            int itemId = picked.get("itemId").getAsInt();
            int amount = picked.get("amount").getAsInt();
            JsonObject data = new JsonObject();
            data.addProperty("type", type);
            data.addProperty("amount", amount);

            if ("coin".equals(type)) {
                String up = "UPDATE users SET coins = coins + ? WHERE id=?";
                PreparedStatement ps2 = conn.prepareStatement(up);
                ps2.setInt(1, amount);
                ps2.setInt(2, userId);
                ps2.executeUpdate();
                data.addProperty("message", "获得金币: " + amount);
            } else if ("item_range".equals(type)) {
                int minId = (locId == 1) ? 1 : (locId == 2 ? 6 : 11);
                int maxId = (locId == 1) ? 5 : (locId == 2 ? 10 : 15);
                int randomId = new Random().nextInt(maxId - minId + 1) + minId;

                PreparedStatement psName = conn.prepareStatement("SELECT name FROM food_base WHERE id=?");
                psName.setInt(1, randomId);
                ResultSet rsName = psName.executeQuery();
                String name = rsName.next() ? rsName.getString("name") : "未知食物";

                String sel = "SELECT id FROM user_items WHERE user_id=? AND item_id=?";
                PreparedStatement ps3 = conn.prepareStatement(sel);
                ps3.setInt(1, userId);
                ps3.setInt(2, randomId);
                ResultSet rs3 = ps3.executeQuery();
                if (rs3.next()) {
                    String up2 = "UPDATE user_items SET amount = amount + ? WHERE user_id=? AND item_id=?";
                    PreparedStatement ps4 = conn.prepareStatement(up2);
                    ps4.setInt(1, amount);
                    ps4.setInt(2, userId);
                    ps4.setInt(3, randomId);
                    ps4.executeUpdate();
                } else {
                    String ins = "INSERT INTO user_items(user_id,item_id,amount) VALUES(?,?,?)";
                    PreparedStatement ps4 = conn.prepareStatement(ins);
                    ps4.setInt(1, userId);
                    ps4.setInt(2, randomId);
                    ps4.setInt(3, amount);
                    ps4.executeUpdate();
                }
                data.addProperty("message", "获得食物: " + name);
            } else if ("pet_range".equals(type)) {
                int minId = (locId == 1) ? 1 : (locId == 2 ? 6 : 11);
                int maxId = (locId == 1) ? 5 : (locId == 2 ? 10 : 15);
                int randomId = new Random().nextInt(maxId - minId + 1) + minId;

                PreparedStatement psName = conn.prepareStatement("SELECT name FROM pets_base WHERE id=?");
                psName.setInt(1, randomId);
                ResultSet rsName = psName.executeQuery();
                String name = rsName.next() ? rsName.getString("name") : "未知宠物";

                // 插入新宠物（不激活），使用默认疲劳上限（从 pets_base 获取更佳，此处简化）
                String ins2 = "INSERT INTO user_pets(user_id, pet_id, fatigue, fatigue_max, is_active) " +
                        "VALUES(?, ?, 0, 100, 0)";
                PreparedStatement ps5 = conn.prepareStatement(ins2);
                ps5.setInt(1, userId);
                ps5.setInt(2, randomId);
                ps5.executeUpdate();

                data.addProperty("message", "获得宠物: " + name);
            }

            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", data);
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