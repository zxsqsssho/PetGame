// 文件：code/src/java/com/petgame/ShopServlet.java
// 商店模块作用：提供 /api/shop/items（GET，获取商品列表）和 /api/shop/buy（POST，购买商品）。
//            获取列表：直接查询 shop_items 表返回所有商品信息（含名称、价格等）。购买：前端 POST 商品 itemId 和 quantity。
//            后台检查用户金币是否足够（users.coins ≥ price*quantity），若足够则扣除金币、增加相应道具。
//            示例：购买后在 user_items 表更新该道具数量。返回购买结果及剩余金币。
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

@WebServlet(urlPatterns={"/api/shop/items", "/api/shop/buy"})
public class ShopServlet extends HttpServlet {
    private Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 返回商店商品列表
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try (Connection conn = DB.getConn()) {
            String sql = "SELECT id,name,price,description,icon FROM shop_items where price>0";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject item = new JsonObject();
                item.addProperty("id", rs.getInt("id"));
                item.addProperty("name", rs.getString("name"));
                item.addProperty("price", rs.getInt("price"));
                item.addProperty("description", rs.getString("description"));
                item.addProperty("icon", rs.getString("icon"));
                arr.add(item);
            }
            JsonObject res = new JsonObject();
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
        // 购买商品
        resp.setContentType("application/json;charset=UTF-8");
        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        int itemId = jsonReq.get("itemId").getAsInt();
        int quantity = jsonReq.has("quantity") ? jsonReq.get("quantity").getAsInt() : 1;
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
            // 查询商品价格
            String sqlItem = "SELECT price FROM shop_items WHERE id=?";
            PreparedStatement psItem = conn.prepareStatement(sqlItem);
            psItem.setInt(1, itemId);
            ResultSet rs = psItem.executeQuery();
            if (!rs.next()) {
                res.addProperty("code", 6);
                res.addProperty("msg", "商品不存在");
                res.add("data", null);
                out.print(gson.toJson(res));
                return;
            }
            int price = rs.getInt("price") * quantity;
            // 检查金币
            String sqlUser = "SELECT coins FROM users WHERE id=?";
            PreparedStatement psUser = conn.prepareStatement(sqlUser);
            psUser.setInt(1, userId);
            ResultSet rs2 = psUser.executeQuery();
            rs2.next();
            int coins = rs2.getInt("coins");
            if (coins < price) {
                res.addProperty("code", 7);
                res.addProperty("msg", "金币不足");
                res.add("data", null);
                out.print(gson.toJson(res));
                return;
            }
            // 扣除金币
            String updCoins = "UPDATE users SET coins = coins - ? WHERE id=?";
            PreparedStatement psUpd = conn.prepareStatement(updCoins);
            psUpd.setInt(1, price);
            psUpd.setInt(2, userId);
            psUpd.executeUpdate();
            // 增加道具
            for (int i = 0; i < quantity; i++) {
                String sel = "SELECT id,amount FROM user_items WHERE user_id=? AND item_id=?";
                PreparedStatement psSel = conn.prepareStatement(sel);
                psSel.setInt(1, userId); psSel.setInt(2, itemId);
                ResultSet rs3 = psSel.executeQuery();
                if (rs3.next()) {
                    String upd = "UPDATE user_items SET amount = amount + 1 WHERE id=?";
                    PreparedStatement ps2 = conn.prepareStatement(upd);
                    ps2.setInt(1, rs3.getInt("id"));
                    ps2.executeUpdate();
                } else {
                    String ins = "INSERT INTO user_items(user_id,item_id,amount) VALUES(?,?,1)";
                    PreparedStatement ps2 = conn.prepareStatement(ins);
                    ps2.setInt(1, userId); ps2.setInt(2, itemId);
                    ps2.executeUpdate();
                }
            }
            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            JsonObject data = new JsonObject();
            data.addProperty("remainingCoins", coins - price);
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
