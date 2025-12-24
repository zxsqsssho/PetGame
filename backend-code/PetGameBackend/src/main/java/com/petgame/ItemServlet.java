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
        // 获取玩家背包中探索道具列表（仅从food_base获取信息，出售价格=道具ID×40）
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
            // 核心修改：仅关联food_base，价格直接计算为item_id*40，聚合同一道具的总数量
            String sql = "SELECT ui.user_id, ui.item_id, " +
                    "COALESCE(fb.name, '未知道具') AS name, " +  // 仅从food_base取名称
                    "COALESCE(fb.icon, '/icons/default_item.png') AS icon, " +  // 仅从food_base取图标
                    "SUM(ui.amount) AS amount, " +  // 聚合同一道具的总数量
                    "(ui.item_id * 40) AS price, " +  // 出售价格=道具ID×40
                    "COALESCE(fb.description, '无描述') AS description, " +  // 仅从food_base取描述
                    "0 AS is_shop_item " +  // 固定为非商店道具（探索道具）
                    "FROM user_items ui " +
                    "LEFT JOIN food_base fb ON ui.item_id = fb.id " +  // 仅关联探索道具表
                    "WHERE ui.user_id=? " +
                    "GROUP BY ui.user_id, ui.item_id, fb.name, fb.icon, fb.description " +
                    "ORDER BY ui.item_id ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("itemId", rs.getInt("item_id"));
                it.addProperty("name", rs.getString("name"));
                it.addProperty("icon", rs.getString("icon"));
                it.addProperty("amount", rs.getInt("amount"));  // 合并后的总数量
                it.addProperty("price", rs.getInt("price"));  // 价格=ID×40
                it.addProperty("description", rs.getString("description"));
                it.addProperty("isShopItem", rs.getInt("is_shop_item") == 1);  // 固定为false
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
        // 出售玩家背包中道具（支持探索道具，价格=道具ID×40）
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
                    // 查询总数量+探索道具信息（仅关联food_base）
                    String checkSql = "SELECT " +
                            "SUM(ui.amount) AS total_amount, " +  // 总数量
                            "CASE WHEN fb.id IS NOT NULL THEN 1 ELSE 0 END AS is_explore_item " +
                            "FROM user_items ui " +
                            "LEFT JOIN food_base fb ON ui.item_id = fb.id " +
                            "WHERE ui.user_id = ? AND ui.item_id = ? " +
                            "GROUP BY ui.user_id, ui.item_id, fb.id";

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
                    boolean isExploreItem = checkRs.getInt("is_explore_item") == 1;

                    // 仅允许food_base中存在的探索道具出售
                    if (!isExploreItem) {
                        res.addProperty("code", 6);
                        res.addProperty("msg", "该道具无法出售");
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

                    // 探索道具出售价格：itemId × 40 × 出售数量
                    int sellPrice = itemId * 40 * saleAmount;

                    // 核心逻辑：判断是否全额出售（出售后数量为0）
                    int remainingToSell = saleAmount;
                    if (saleAmount == totalAmount) {
                        // 全额出售：直接删除该用户下该道具的所有记录
                        String deleteAllSql = "DELETE FROM user_items WHERE user_id = ? AND item_id = ?";
                        PreparedStatement deleteAllPs = conn.prepareStatement(deleteAllSql);
                        deleteAllPs.setInt(1, userId);
                        deleteAllPs.setInt(2, itemId);
                        deleteAllPs.executeUpdate();
                        remainingToSell = 0; // 标记为已扣减完成
                    } else {
                        // 非全额出售：逐条扣减/删除记录
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