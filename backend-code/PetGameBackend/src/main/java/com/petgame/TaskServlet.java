// 文件：code/src/java/com/petgame/TaskServlet.java
// 任务模块作用：接口 /api/tasks/list（GET）返回当前用户所有任务及其完成状态，/api/tasks/claim（POST）用于领取完成任务的奖励。
//            后台逻辑：任务列表 从 tasks 表和 user_tasks 表联合查询（LEFT JOIN 获取所有任务及状态），返回如 {id, title, desc, status}。
//            领取奖励：请求中包含任务 taskId，验证该任务状态为未领且完成条件满足（此处可简化直接更新状态），
//          然后增加用户金币/经验（tasks 表有奖励），更新 users 表和将 user_tasks.status 置 1。
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

@WebServlet(urlPatterns={"/api/tasks/list", "/api/tasks/claim"})
public class TaskServlet extends HttpServlet {
    private Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取任务列表及状态
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
            // 左连接获取所有系统任务及用户状态
            String sql = "SELECT t.id, t.title, t.description, ut.status " +
                    "FROM tasks t LEFT JOIN user_tasks ut ON t.id=ut.task_id AND ut.user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            JsonArray arr = new JsonArray();
            while (rs.next()) {
                JsonObject tk = new JsonObject();
                tk.addProperty("id", rs.getInt("id"));
                tk.addProperty("title", rs.getString("title"));
                tk.addProperty("description", rs.getString("description"));
                int status = rs.getInt("status");
                tk.addProperty("completed", status==1);
                arr.add(tk);
            }
            res.addProperty("code", 0);
            res.addProperty("msg", "success");
            res.add("data", arr);
            out.print(gson.toJson(res));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 领取任务奖励
        resp.setContentType("application/json;charset=UTF-8");
        JsonObject jsonReq = gson.fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())), JsonObject.class);
        int taskId = jsonReq.get("taskId").getAsInt();
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
            // 查询任务奖励
            String sql = "SELECT reward_coins, reward_exp FROM tasks WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int coin = rs.getInt("reward_coins");
                int exp = rs.getInt("reward_exp");
                // 更新用户
                String upd = "UPDATE users SET coins = coins+?, exp = exp+? WHERE id=?";
                PreparedStatement ps2 = conn.prepareStatement(upd);
                ps2.setInt(1, coin);
                ps2.setInt(2, exp);
                ps2.setInt(3, userId);
                ps2.executeUpdate();
                // 记录已领取
                String ins = "INSERT INTO user_tasks(user_id,task_id,status) VALUES(?,?,1) " +
                        "ON DUPLICATE KEY UPDATE status=1";
                PreparedStatement ps3 = conn.prepareStatement(ins);
                ps3.setInt(1, userId);
                ps3.setInt(2, taskId);
                ps3.executeUpdate();
                JsonObject data = new JsonObject();
                data.addProperty("coinsGained", coin);
                data.addProperty("expGained", exp);
                res.addProperty("code", 0);
                res.addProperty("msg", "success");
                res.add("data", data);
            } else {
                res.addProperty("code", 8);
                res.addProperty("msg", "任务不存在");
                res.add("data", null);
            }
            out.print(gson.toJson(res));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
