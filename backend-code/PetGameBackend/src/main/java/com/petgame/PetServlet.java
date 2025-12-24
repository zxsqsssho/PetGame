// 文件：code/src/java/com/petgame/PetServlet.java
// 作用：宠物模块，提供用户的宠物列表、喂食操作、携带宠物功能
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

@WebServlet(urlPatterns={"/api/pets/list", "/api/pets/feed", "/api/pets/carry"})
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
            // 联合 pets_base 查询宠物信息，移除不存在的level字段
            String sql = "SELECT up.id AS upid, up.pet_id, up.nickname, up.fatigue, up.fatigue_max, up.is_active, " +
                    "pb.name, pb.rarity, pb.icon, pb.food_item_id " +
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
                pet.addProperty("fatigue", rs.getInt("fatigue"));
                pet.addProperty("fatigueMax", rs.getInt("fatigue_max"));
                pet.addProperty("carried", rs.getInt("is_active") == 1);

                String nick = rs.getString("nickname");
                pet.addProperty("nickname", nick != null ? nick : rs.getString("name"));

                // 添加宠物偏好食物信息
                int foodItemId = rs.getInt("food_item_id");
                pet.addProperty("preferredFood", getFoodTypeById(foodItemId));

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
        // 喂食宠物或携带宠物
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        HttpSession session = req.getSession(false);
        JsonObject res = new JsonObject();

        String path = req.getServletPath();
        System.out.println("PetServlet POST path: " + path); // 调试信息

        if (session == null || session.getAttribute("userId")==null) {
            res.addProperty("code", 4);
            res.addProperty("msg", "未登录");
            res.add("data", null);
            out.print(gson.toJson(res));
            return;
        }

        int userId = (int) session.getAttribute("userId");

        if ("/api/pets/feed".equals(path)) {
            // 喂食宠物
            int petRecId = jsonReq.get("petId").getAsInt();
            String foodType = jsonReq.get("foodType").getAsString();

            try (Connection conn = DB.getConn()) {
                // 获取宠物当前疲劳值
                String sql = "SELECT fatigue, fatigue_max, pet_id FROM user_pets WHERE id=? AND user_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, petRecId);
                ps.setInt(2, userId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    res.addProperty("code", 5);
                    res.addProperty("msg", "宠物不存在");
                    res.add("data", null);
                    out.print(gson.toJson(res));
                    return;
                }

                int currentFatigue = rs.getInt("fatigue");
                int maxFatigue = rs.getInt("fatigue_max");
                int petId = rs.getInt("pet_id");

                if (currentFatigue <= 0) {
                    res.addProperty("code", 6);
                    res.addProperty("msg", "宠物不饿");
                    res.add("data", null);
                    out.print(gson.toJson(res));
                    return;
                }

                // 获取食物恢复值
                int foodRecovery = 10; // 默认恢复值
                if ("golden".equals(foodType)) {
                    // 高级食物恢复更多疲劳
                    foodRecovery = 20;

                    // 检查用户是否有高级食物
                    String checkFoodSql = "SELECT amount FROM user_items WHERE user_id=? AND item_id=16";
                    PreparedStatement checkFoodPs = conn.prepareStatement(checkFoodSql);
                    checkFoodPs.setInt(1, userId);
                    ResultSet foodRs = checkFoodPs.executeQuery();

                    if (!foodRs.next() || foodRs.getInt("amount") < 1) {
                        res.addProperty("code", 7);
                        res.addProperty("msg", "没有高级食物");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    // 扣除高级食物
                    String consumeFoodSql = "UPDATE user_items SET amount = amount - 1 WHERE user_id=? AND item_id=16";
                    PreparedStatement consumeFoodPs = conn.prepareStatement(consumeFoodSql);
                    consumeFoodPs.setInt(1, userId);
                    consumeFoodPs.executeUpdate();
                } else {
                    // 普通食物恢复少量疲劳
                    String petFoodIdSql = "SELECT food_item_id FROM pets_base WHERE id=?";
                    PreparedStatement foodIdPs = conn.prepareStatement(petFoodIdSql);
                    foodIdPs.setInt(1, petId);
                    ResultSet foodIdRs = foodIdPs.executeQuery();

                    if (foodIdRs.next()) {
                        int foodItemId = foodIdRs.getInt("food_item_id");

                        // 检查用户是否有对应食物
                        String checkFoodSql = "SELECT amount FROM user_items WHERE user_id=? AND item_id=?";
                        PreparedStatement checkFoodPs = conn.prepareStatement(checkFoodSql);
                        checkFoodPs.setInt(1, userId);
                        checkFoodPs.setInt(2, foodItemId);
                        ResultSet foodRs = checkFoodPs.executeQuery();

                        if (!foodRs.next() || foodRs.getInt("amount") < 1) {
                            res.addProperty("code", 7);
                            res.addProperty("msg", "没有对应食物");
                            res.add("data", null);
                            out.print(gson.toJson(res));
                            return;
                        }

                        // 扣除食物
                        String consumeFoodSql = "UPDATE user_items SET amount = amount - 1 WHERE user_id=? AND item_id=?";
                        PreparedStatement consumeFoodPs = conn.prepareStatement(consumeFoodSql);
                        consumeFoodPs.setInt(1, userId);
                        consumeFoodPs.setInt(2, foodItemId);
                        consumeFoodPs.executeUpdate();
                    }
                }

                // 更新宠物疲劳值
                int newFatigue = Math.max(0, currentFatigue - foodRecovery);
                String updateFatigueSql = "UPDATE user_pets SET fatigue = ? WHERE id=?";
                PreparedStatement updatePs = conn.prepareStatement(updateFatigueSql);
                updatePs.setInt(1, newFatigue);
                updatePs.setInt(2, petRecId);
                updatePs.executeUpdate();

                JsonObject data = new JsonObject();
                data.addProperty("newFatigue", newFatigue);
                data.addProperty("message", "喂食成功，疲劳值减少");

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

        } else if ("/api/pets/carry".equals(path)) {
            // 携带/取消携带宠物
            int petRecId = jsonReq.get("petId").getAsInt();
            boolean carry = jsonReq.get("carry").getAsBoolean();

            try (Connection conn = DB.getConn()) {
                if (carry) {
                    // 取消当前携带的宠物
                    String clearActiveSql = "UPDATE user_pets SET is_active = 0 WHERE user_id = ? AND is_active = 1";
                    PreparedStatement clearPs = conn.prepareStatement(clearActiveSql);
                    clearPs.setInt(1, userId);
                    clearPs.executeUpdate();

                    // 设置新宠物为携带状态
                    String setActiveSql = "UPDATE user_pets SET is_active = 1 WHERE id = ? AND user_id = ?";
                    PreparedStatement setPs = conn.prepareStatement(setActiveSql);
                    setPs.setInt(1, petRecId);
                    setPs.setInt(2, userId);
                    int rows = setPs.executeUpdate();

                    if (rows > 0) {
                        res.addProperty("code", 0);
                        res.addProperty("msg", "携带宠物成功");
                        res.add("data", null);
                    } else {
                        res.addProperty("code", 5);
                        res.addProperty("msg", "宠物不存在");
                        res.add("data", null);
                    }
                } else {
                    // 取消携带
                    String clearActiveSql = "UPDATE user_pets SET is_active = 0 WHERE id = ? AND user_id = ?";
                    PreparedStatement clearPs = conn.prepareStatement(clearActiveSql);
                    clearPs.setInt(1, petRecId);
                    clearPs.setInt(2, userId);
                    int rows = clearPs.executeUpdate();

                    if (rows > 0) {
                        res.addProperty("code", 0);
                        res.addProperty("msg", "取消携带成功");
                        res.add("data", null);
                    } else {
                        res.addProperty("code", 5);
                        res.addProperty("msg", "宠物不存在或未携带");
                        res.add("data", null);
                    }
                }

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

    // 根据食物ID获取食物类型
    private String getFoodTypeById(int foodId) {
        switch (foodId) {
            case 1: return "鱼干";
            case 2: return "骨头";
            case 3: return "种子";
            case 4: return "坚果";
            case 5: return "胡萝卜";
            case 6: return "小鱼虾";
            case 7: return "电能饵";
            case 8: return "水晶藻";
            case 9: return "深湖肉块";
            case 10: return "荧光浮游生物";
            case 11: return "魔能矿石";
            case 12: return "灵魂碎片";
            case 13: return "巨鸟果实";
            case 14: return "金属能量块";
            default: return "古代核心";
        }
    }
}