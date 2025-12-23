// 文件：code/src/java/com/petgame/ItemServlet.java
// 背包模块作用：对应 /api/items/list（GET），返回当前玩家拥有的所有道具。
//            后台根据登录用户 ID 查询 user_items 并关联 shop_items 获取名称等信息，返回列表。
//            示例返回字段包含道具 ID、名称、数量、描述等。
package com.petgame;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

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
import java.sql.SQLException;

@WebServlet(urlPatterns={"/api/items/list","/api/items/sale"})
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

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        // 出售玩家背包中道具
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

        try {
            JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
            int itemId = jsonReq.get("itemId").getAsInt();//出售的道具ID
            int saleAmount = jsonReq.get("saleAmount").getAsInt();//出售的道具数量

            if (saleAmount <= 0) {
                res.addProperty("code", 2);
                res.addProperty("msg", "出售数量必须大于0");
                res.add("data", null);
                out.print(gson.toJson(res));
                return;
            }

            try (Connection conn = DB.getConn()) {
                // 开启事务
                conn.setAutoCommit(false);

                try {
                    // 1. 查询用户是否有该物品，以及物品的价格
                    String checkSql = "SELECT ui.amount, si.price " +
                            "FROM user_items ui " +
                            "JOIN shop_items si ON ui.item_id = si.id " +
                            "WHERE ui.user_id = ? AND ui.item_id = ?";
                    PreparedStatement checkPs = conn.prepareStatement(checkSql);
                    checkPs.setInt(1, userId);
                    checkPs.setInt(2, itemId);
                    ResultSet checkRs = checkPs.executeQuery();

                    if (!checkRs.next()) {
                        res.addProperty("code", 3);
                        res.addProperty("msg", "您没有该物品");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    int currentAmount = checkRs.getInt("amount");
                    int itemPrice = checkRs.getInt("price");

                    if (currentAmount < saleAmount) {
                        res.addProperty("code", 5);
                        res.addProperty("msg", "出售数量超过拥有数量");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    // 2. 计算出售价格（假设出售价格为购买价格的一半）
                    int sellPrice = (int) (itemPrice * 0.5 * saleAmount);

                    // 3. 更新用户物品数量
                    String updateSql;
                    PreparedStatement updatePs;

                    if (currentAmount == saleAmount) {
                        // 如果全部出售，删除记录
                        updateSql = "DELETE FROM user_items WHERE user_id = ? AND item_id = ?";
                        updatePs = conn.prepareStatement(updateSql);
                        updatePs.setInt(1, userId);
                        updatePs.setInt(2, itemId);
                    } else {
                        // 否则减少数量
                        updateSql = "UPDATE user_items SET amount = amount - ? WHERE user_id = ? AND item_id = ?";
                        updatePs = conn.prepareStatement(updateSql);
                        updatePs.setInt(1, saleAmount);
                        updatePs.setInt(2, userId);
                        updatePs.setInt(3, itemId);
                    }
                    int updateResult = updatePs.executeUpdate();

                    if (updateResult == 0) {
                        throw new SQLException("更新物品数量失败");
                    }

                    // 4. 增加用户金币
                    String coinSql = "UPDATE users SET coins = coins + ? WHERE id = ?";
                    PreparedStatement coinPs = conn.prepareStatement(coinSql);
                    coinPs.setInt(1, sellPrice);
                    coinPs.setInt(2, userId);
                    coinPs.executeUpdate();

                    // 5. 提交事务
                    conn.commit();

                    // 6. 获取更新后的金币数量
                    String getCoinsSql = "SELECT coins FROM users WHERE id = ?";
                    PreparedStatement getCoinsPs = conn.prepareStatement(getCoinsSql);
                    getCoinsPs.setInt(1, userId);
                    ResultSet coinsRs = getCoinsPs.executeQuery();
                    int newCoins = 0;
                    if (coinsRs.next()) {
                        newCoins = coinsRs.getInt("coins");
                    }

                    // 7. 返回成功响应
                    JsonObject data = new JsonObject();
                    data.addProperty("sellPrice", sellPrice);
                    data.addProperty("newCoins", newCoins);
                    data.addProperty("soldAmount", saleAmount);
                    data.addProperty("itemId", itemId);

                    res.addProperty("code", 0);
                    res.addProperty("msg", "出售成功");
                    res.add("data", data);
                    out.print(gson.toJson(res));

                } catch (Exception e) {
                    // 回滚事务
                    conn.rollback();
                    throw e;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                res.addProperty("code", 500);
                res.addProperty("msg", "数据库错误: " + e.getMessage());
                res.add("data", null);
                out.print(gson.toJson(res));
            }

        } catch (NumberFormatException e) {
            res.addProperty("code", 1);
            res.addProperty("msg", "参数格式错误");
            res.add("data", null);
            out.print(gson.toJson(res));
        } catch (JsonSyntaxException e) {
            res.addProperty("code", 1);
            res.addProperty("msg", "JSON格式错误");
            res.add("data", null);
            out.print(gson.toJson(res));
        } catch (Exception e) {
            e.printStackTrace();
            res.addProperty("code", 500);
            res.addProperty("msg", "服务器异常: " + e.getMessage());
            res.add("data", null);
            out.print(gson.toJson(res));
        }
    }
}
