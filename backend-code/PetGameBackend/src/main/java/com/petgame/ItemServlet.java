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
        // 获取玩家背包中道具列表（合并同一item_id的所有记录，数量相加）
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        JsonObject res = new JsonObject();

        // 登录校验
        if (session == null || session.getAttribute("userId")==null) {
            res.addProperty("code", 4);
            res.addProperty("msg", "未登录");
            res.add("data", null);
            out.print(gson.toJson(res));
            return;
        }
        int userId = (int) session.getAttribute("userId");

        try (Connection conn = DB.getConn()) {
            // 关键修改：GROUP BY 聚合同一用户的同一道具，SUM计算总数量
            String sql = "SELECT ui.user_id, ui.item_id, " +
                    "COALESCE(MAX(si.name), MAX(fb.name), '未知道具') AS name, " +
                    "COALESCE(MAX(si.icon), MAX(fb.icon), '/icons/default_item.png') AS icon, " +
                    "SUM(ui.amount) AS amount, " +  // 聚合总数量
                    "COALESCE(MAX(si.price), 0) AS price, " +
                    "COALESCE(MAX(si.description), MAX(fb.description), '无描述') AS description, " +
                    "CASE WHEN MAX(si.id) IS NOT NULL THEN 1 ELSE 0 END AS is_shop_item " +
                    "FROM user_items ui " +
                    "LEFT JOIN shop_items si ON ui.item_id = si.id " +
                    "LEFT JOIN food_base fb ON ui.item_id = fb.id " +
                    "WHERE ui.user_id=? " +
                    "GROUP BY ui.user_id, ui.item_id " +  // 按用户+道具ID分组
                    "ORDER BY is_shop_item DESC, ui.item_id ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("itemId", rs.getInt("item_id"));
                it.addProperty("name", rs.getString("name"));
                it.addProperty("icon", rs.getString("icon"));
                it.addProperty("amount", rs.getInt("amount"));  // 总数量
                it.addProperty("price", rs.getInt("price"));
                it.addProperty("description", rs.getString("description"));
                it.addProperty("isShopItem", rs.getInt("is_shop_item") == 1);
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
            out.print(gson.toJson(err));
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        // 出售玩家背包中道具（支持商店道具+探索道具，基于总数量校验/扣减）
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        JsonObject res = new JsonObject();

        // 登录校验
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
            int itemId = jsonReq.get("itemId").getAsInt();
            int saleAmount = jsonReq.get("saleAmount").getAsInt();

            if (saleAmount <= 0) {
                res.addProperty("code", 2);
                res.addProperty("msg", "出售数量必须大于0");
                res.add("data", null);
                out.print(gson.toJson(res));
                return;
            }

            try (Connection conn = DB.getConn()) {
                conn.setAutoCommit(false);

                try {
                    // 【修改1】查询总数量+道具类型（聚合所有同item_id的记录）
                    String checkSql = "SELECT " +
                            "SUM(ui.amount) AS total_amount, " +  // 总数量
                            "COALESCE(MAX(si.price), 0) AS shop_price, " +
                            "CASE WHEN MAX(si.id) IS NOT NULL THEN 1 ELSE 0 END AS is_shop_item, " +
                            "CASE WHEN MAX(fb.id) IS NOT NULL THEN 1 ELSE 0 END AS is_explore_item " +
                            "FROM user_items ui " +
                            "LEFT JOIN shop_items si ON ui.item_id = si.id " +
                            "LEFT JOIN food_base fb ON ui.item_id = fb.id " +
                            "WHERE ui.user_id = ? AND ui.item_id = ? " +
                            "GROUP BY ui.user_id, ui.item_id";

                    PreparedStatement checkPs = conn.prepareStatement(checkSql);
                    checkPs.setInt(1, userId);
                    checkPs.setInt(2, itemId);
                    ResultSet checkRs = checkPs.executeQuery();

                    if (!checkRs.next()) {
                        res.addProperty("code", 3);
                        res.addProperty("msg", "您没有该物品");
                        out.print(gson.toJson(res));
                        return;
                    }

                    int totalAmount = checkRs.getInt("total_amount");  // 总数量
                    int shopPrice = checkRs.getInt("shop_price");
                    boolean isShopItem = checkRs.getInt("is_shop_item") == 1;
                    boolean isExploreItem = checkRs.getInt("is_explore_item") == 1;

                    // 仅禁止非商店/非探索的无效道具
                    if (!isShopItem && !isExploreItem) {
                        res.addProperty("code", 6);
                        res.addProperty("msg", "该道具无法出售");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    // 仅对商店道具检查价格有效性
                    if (isShopItem && shopPrice <= 0) {
                        res.addProperty("code", 7);
                        res.addProperty("msg", "该道具不可出售");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    if (totalAmount < saleAmount) {
                        res.addProperty("code", 5);
                        res.addProperty("msg", "出售数量超过拥有数量");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    // 计算出售价格
                    int sellPrice;
                    if (isShopItem) {
                        // 商店道具：原价半价出售
                        sellPrice = (int) (shopPrice * 0.5 * saleAmount);
                    } else {
                        // 探索道具：itemId × 40 × 出售数量
                        sellPrice = itemId * 40 * saleAmount;
                    }

                    // 【修改2】逐条扣减/删除记录，直到扣够saleAmount
                    int remainingToSell = saleAmount;
                    // 查询该道具的所有记录（按ID升序，优先扣减旧记录）
                    String listItemsSql = "SELECT id, amount FROM user_items WHERE user_id = ? AND item_id = ? ORDER BY id ASC";
                    PreparedStatement listPs = conn.prepareStatement(listItemsSql);
                    listPs.setInt(1, userId);
                    listPs.setInt(2, itemId);
                    ResultSet itemRs = listPs.executeQuery();

                    while (itemRs.next() && remainingToSell > 0) {
                        int recordId = itemRs.getInt("id");
                        int recordAmount = itemRs.getInt("amount");

                        if (recordAmount <= remainingToSell) {
                            // 该记录数量不足，直接删除
                            String deleteSql = "DELETE FROM user_items WHERE id = ?";
                            PreparedStatement deletePs = conn.prepareStatement(deleteSql);
                            deletePs.setInt(1, recordId);
                            deletePs.executeUpdate();
                            remainingToSell -= recordAmount;
                        } else {
                            // 该记录数量足够，扣减部分数量
                            String updateSql = "UPDATE user_items SET amount = amount - ? WHERE id = ?";
                            PreparedStatement updatePs = conn.prepareStatement(updateSql);
                            updatePs.setInt(1, remainingToSell);
                            updatePs.setInt(2, recordId);
                            updatePs.executeUpdate();
                            remainingToSell = 0;
                        }
                    }

                    if (remainingToSell > 0) {
                        throw new SQLException("扣减道具数量失败，剩余未扣减数量：" + remainingToSell);
                    }

                    // 增加用户金币
                    String coinSql = "UPDATE users SET coins = coins + ? WHERE id = ?";
                    PreparedStatement coinPs = conn.prepareStatement(coinSql);
                    coinPs.setInt(1, sellPrice);
                    coinPs.setInt(2, userId);
                    coinPs.executeUpdate();

                    // 提交事务
                    conn.commit();

                    // 获取更新后的金币数量
                    String getCoinsSql = "SELECT coins FROM users WHERE id = ?";
                    PreparedStatement getCoinsPs = conn.prepareStatement(getCoinsSql);
                    getCoinsPs.setInt(1, userId);
                    ResultSet coinsRs = getCoinsPs.executeQuery();
                    int newCoins = 0;
                    if (coinsRs.next()) {
                        newCoins = coinsRs.getInt("coins");
                    }

                    // 返回成功响应
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