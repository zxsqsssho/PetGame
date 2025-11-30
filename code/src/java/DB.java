public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/petgame?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String USER = "root";// 改成你的数据库账号
    private static final String PASSWORD = "root";// 改成你的数据库密码

    public static Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/*
Vue 前端如何调后端？

示例：登录请求

import axios from "axios";

axios.post("http://localhost:8080/login", {
  username: this.username,
  password: this.password
})
.then(res => {
  console.log(res.data);
})
.catch(err => {
  console.error(err);
});


Vue 不需要改任何东西，你原来写的 Vue 前端全部可以继续使用。
 */
