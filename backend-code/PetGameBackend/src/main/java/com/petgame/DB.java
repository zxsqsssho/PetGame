package com.petgame;

import java.sql.Connection;
import java.sql.DriverManager;

//code/src/java/DB.java
public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/petgame?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String USER = "root";// 改成你的数据库账号
    private static final String PASSWORD = "root";// 改成你的数据库密码

    public static Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

