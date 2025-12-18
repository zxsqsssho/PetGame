// 文件：code/src/java/com/petgame/DrawServlet.java
// 作用：抽奖模块，普通抽奖 /api/draw/normal 和高级抽奖 /api/draw/advanced（均为 POST）
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
import java.util.Random;

@WebServlet(urlPatterns={"/api/draw/normal", "/api/draw/advanced"})
public class DrawServlet extends HttpServlet {
    private Gson gson = new Gson();
    private Random random = new Random();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 抽奖逻辑
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
        String path = req.getServletPath();
        boolean advanced = path.endsWith("/advanced");
        int cost = advanced ? 500 : 100;
        try (Connection conn = DB.getConn()) {
            // 扣除金币
            String costSql = "UPDATE users SET coins = coins - ? WHERE id=?";
            PreparedStatement psCost = conn.prepareStatement(costSql);
            psCost.setInt(1, cost);
            psCost.setInt(2, userId);
            psCost.executeUpdate();
            // 随机决定奖品（此处简化示例，实际可查询奖池）
            String resultType; int resultId = 0;
            if (!advanced && random.nextDouble() < 0.1) {
                // 普通抽奖10%机会得稀有宠物
                resultType = "pet"; resultId = 6 + random.nextInt(4); // e.g. pets id 6-9
            } else if (advanced && random.nextDouble() < 0.2) {
                // 高级抽奖20%机会得史诗宠物
                resultType = "pet"; resultId = 10 + random.nextInt(5); // id 10-14
            } else if (random.nextBoolean()) {
                // 获得道具
                resultType = "item"; resultId = random.nextInt(5)+1;
            } else {
                // 获得金币
                resultType = "coin"; resultId = 50 + random.nextInt(101); // 50~150金
            }
            JsonObject data = new JsonObject();
            data.addProperty("type", resultType);
            if ("coin".equals(resultType)) {
                // 加钱
                String up = "UPDATE users SET coins = coins + ? WHERE id=?";
                PreparedStatement ps3 = conn.prepareStatement(up);
                ps3.setInt(1, resultId);
                ps3.setInt(2, userId);
                ps3.executeUpdate();
                data.addProperty("coinsGained", resultId);
            } else if ("item".equals(resultType)) {
                // 写入背包
                int amt = 1;
                String sel = "SELECT id,amount FROM user_items WHERE user_id=? AND item_id=?";
                PreparedStatement ps4 = conn.prepareStatement(sel);
                ps4.setInt(1, userId); ps4.setInt(2, resultId);
                ResultSet rs4 = ps4.executeQuery();
                if (rs4.next()) {
                    String upd = "UPDATE user_items SET amount=amount+1 WHERE id=?";
                    PreparedStatement ps5 = conn.prepareStatement(upd);
                    ps5.setInt(1, rs4.getInt("id"));
                    ps5.executeUpdate();
                } else {
                    String ins = "INSERT INTO user_items(user_id,item_id,amount) VALUES(?,?,1)";
                    PreparedStatement ps5 = conn.prepareStatement(ins);
                    ps5.setInt(1, userId); ps5.setInt(2, resultId);
                    ps5.executeUpdate();
                }
                data.addProperty("itemId", resultId);
            } else if ("pet".equals(resultType)) {
                String ins = "INSERT INTO user_pets(user_id,pet_id) VALUES(?,?)";
                PreparedStatement ps6 = conn.prepareStatement(ins);
                ps6.setInt(1, userId);
                ps6.setInt(2, resultId);
                ps6.executeUpdate();
                data.addProperty("petId", resultId);
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
