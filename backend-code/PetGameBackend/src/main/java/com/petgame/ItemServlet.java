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
        // 获取玩家背包中道具列表（包含商店道具+探索道具）
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
            // 核心修改：LEFT JOIN保留所有用户道具，关联food_base补充探索食物信息
            String sql = "SELECT ui.item_id, " +
                    "IFNULL(si.name, fb.name) AS name, " +  // 优先取商店名称，无则取食物图鉴名称
                    "IFNULL(si.icon, CONCAT('/food/', fb.name, '.png')) AS icon, " + // 兜底图标路径
                    "ui.amount, " +
                    "IFNULL(si.price, 0) AS price, " + // 非商店道具价格为0（出售时用）
                    "fb.description AS description " + // 补充道具描述
                    "FROM user_items ui " +
                    "LEFT JOIN shop_items si ON ui.item_id = si.id " + // 左连接商店表
                    "LEFT JOIN food_base fb ON ui.item_id = fb.id " + // 左连接食物图鉴表（探索道具主要来源）
                    "WHERE ui.user_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject it = new JsonObject();
                it.addProperty("itemId", rs.getInt("item_id"));
                // 处理名称：若商店/食物表都无，显示默认名称
                String name = rs.getString("name") == null ? "未知道具" : rs.getString("name");
                it.addProperty("name", name);
                // 处理图标：若为空，显示默认图标
                String icon = rs.getString("icon") == null ? "/food/default.png" : rs.getString("icon");
                it.addProperty("icon", icon);
                it.addProperty("amount", rs.getInt("amount"));
                // 新增：补充价格（出售时用）和描述
                it.addProperty("price", rs.getInt("price"));
                String desc = rs.getString("description") == null ? "无描述" : rs.getString("description");
                it.addProperty("description", desc);
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
            out.print(gson.toJson(err)); // 修复：原代码此处漏了gson.toJson，导致返回原生JsonObject字符串格式错误
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        // 出售玩家背包中道具（兼容探索道具：非商店道具价格为0时提示无法出售）
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
                    // 核心修改：查询时左连接shop_items和food_base，兼容探索道具
                    String checkSql = "SELECT ui.amount, IFNULL(si.price, 0) AS price " +
                            "FROM user_items ui " +
                            "LEFT JOIN shop_items si ON ui.item_id = si.id " +
                            "LEFT JOIN food_base fb ON ui.item_id = fb.id " +
                            "WHERE ui.user_id = ? AND ui.item_id = ?";
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

                    int currentAmount = checkRs.getInt("amount");
                    int itemPrice = checkRs.getInt("price");

                    // 新增：非商店道具（价格为0）无法出售
                    if (itemPrice == 0) {
                        res.addProperty("code", 6);
                        res.addProperty("msg", "该道具为探索获得，无法出售");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    if (currentAmount < saleAmount) {
                        res.addProperty("code", 5);
                        res.addProperty("msg", "出售数量超过拥有数量");
                        res.add("data", null);
                        out.print(gson.toJson(res));
                        return;
                    }

                    // 计算出售价格（商店道具半价，探索道具已提前拦截）
                    int sellPrice = (int) (itemPrice * 0.5 * saleAmount);

                    // 更新用户物品数量
                    String updateSql;
                    PreparedStatement updatePs;
                    if (currentAmount == saleAmount) {
                        updateSql = "DELETE FROM user_items WHERE user_id = ? AND item_id = ?";
                        updatePs = conn.prepareStatement(updateSql);
                        updatePs.setInt(1, userId);
                        updatePs.setInt(2, itemId);
                    } else {
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