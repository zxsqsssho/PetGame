// 文件：code/src/java/com/petgame/DrawServlet.java
// 作用：抽奖模块
package com.petgame;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/api/gacha/draw")
public class GachaServlet extends HttpServlet {

    private Gson gson = new Gson();
    private Random random = new Random();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JsonObject res = new JsonObject();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.addProperty("code", 401);
            res.addProperty("msg", "未登录");
            out.print(gson.toJson(res));
            return;
        }

        int userId = (int) session.getAttribute("userId");

        JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
        String type = body.get("type").getAsString(); // normal / advanced

        int cost = type.equals("advanced") ? 500 : 100;

        try (Connection conn = DB.getConn()) {
            conn.setAutoCommit(false);

            /* 1️⃣ 校验用户金币 & 等级 */
            String userSql = "SELECT coins FROM users WHERE id=?";
            PreparedStatement psUser = conn.prepareStatement(userSql);
            psUser.setInt(1, userId);
            ResultSet rsUser = psUser.executeQuery();

            if (!rsUser.next()) {
                throw new RuntimeException("用户不存在");
            }

            int coins = rsUser.getInt("coins");

            if (coins < cost) {
                throw new RuntimeException("金币不足");
            }

            /* 2️⃣ 扣金币 */
            PreparedStatement psCost =
                    conn.prepareStatement("UPDATE users SET coins=coins-? WHERE id=?");
            psCost.setInt(1, cost);
            psCost.setInt(2, userId);
            psCost.executeUpdate();

            /* 3️⃣ 查询奖池 */
            String poolSql =
                    "SELECT * FROM gacha_pool WHERE gacha_type=?";
            PreparedStatement psPool = conn.prepareStatement(poolSql);
            psPool.setString(1, type);
            ResultSet rsPool = psPool.executeQuery();

            List<JsonObject> pool = new ArrayList<>();
            int totalWeight = 0;

            while (rsPool.next()) {
                JsonObject p = new JsonObject();
                p.addProperty("reward_type", rsPool.getString("reward_type"));
                p.addProperty("min_id", rsPool.getInt("min_id"));
                p.addProperty("max_id", rsPool.getInt("max_id"));
                p.addProperty("weight", rsPool.getInt("weight"));
                totalWeight += rsPool.getInt("weight");
                p.addProperty("cumWeight", totalWeight);
                pool.add(p);
            }

            /* 4️⃣ 权重随机 */
            int r = random.nextInt(totalWeight);
            JsonObject reward = null;
            for (JsonObject p : pool) {
                if (r < p.get("cumWeight").getAsInt()) {
                    reward = p;
                    break;
                }
            }
            if (reward == null) {
                throw new RuntimeException("奖池配置错误");
            }


            String rewardType = reward.get("reward_type").getAsString();
            int rewardId = reward.get("min_id").getAsInt()
                    + random.nextInt(
                    reward.get("max_id").getAsInt()
                            - reward.get("min_id").getAsInt() + 1
            );

            String rewardName = "";
            String rarity = "";

            /* 5️⃣ 发奖 */
            if ("coin".equals(rewardType)) {
                int amount = rewardId;
                PreparedStatement ps =
                        conn.prepareStatement("UPDATE users SET coins=coins+? WHERE id=?");
                ps.setInt(1, amount);
                ps.setInt(2, userId);
                ps.executeUpdate();

                rewardName = amount + " 金币";
                rarity = "normal";

            } else if ("pet".equals(rewardType)) {
                PreparedStatement ps =
                        conn.prepareStatement("INSERT INTO user_pets(user_id, pet_id) VALUES (?,?)");
                ps.setInt(1, userId);
                ps.setInt(2, rewardId);
                ps.executeUpdate();

                PreparedStatement psName =
                        conn.prepareStatement("SELECT name, rarity FROM pets_base WHERE id=?");
                psName.setInt(1, rewardId);
                ResultSet rs = psName.executeQuery();
                if (rs.next()) {
                    rewardName = rs.getString("name");
                    rarity = rs.getInt("rarity") == 3 ? "epic" :
                            rs.getInt("rarity") == 2 ? "rare" : "normal";
                }

            } else if ("food".equals(rewardType)) {
                PreparedStatement ps =
                        conn.prepareStatement(
                                "INSERT INTO user_items(user_id,item_id,amount) VALUES (?,?,1) " +
                                        "ON DUPLICATE KEY UPDATE amount=amount+1"
                        );
                ps.setInt(1, userId);
                ps.setInt(2, rewardId);
                ps.executeUpdate();

                PreparedStatement psName =
                        conn.prepareStatement("SELECT name FROM food_base WHERE id=?");
                psName.setInt(1, rewardId);
                ResultSet rs = psName.executeQuery();
                if (rs.next()) {
                    rewardName = rs.getString("name");
                }
                rarity = "normal";
            }

            /* 6️⃣ 写抽奖日志 ⭐ */
            PreparedStatement psLog =
                    conn.prepareStatement(
                            "INSERT INTO gacha_logs(user_id,gacha_type,reward_type,reward_id,reward_name) " +
                                    "VALUES (?,?,?,?,?)"
                    );
            psLog.setInt(1, userId);
            psLog.setString(2, type);
            psLog.setString(3, rewardType);
            psLog.setInt(4, rewardId);
            psLog.setString(5, rewardName);
            psLog.executeUpdate();

            conn.commit();

            /* 7️⃣ 返回前端 */
            JsonObject data = new JsonObject();
            data.addProperty("rewardName", rewardName);
            data.addProperty("rarity", rarity);
            data.addProperty("type", rewardType);

            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", data);
            out.print(gson.toJson(res));

        } catch (Exception e) {
            e.printStackTrace();
            res.addProperty("code", 500);
            res.addProperty("msg", e.getMessage());
            out.print(gson.toJson(res));
        }
    }
}
