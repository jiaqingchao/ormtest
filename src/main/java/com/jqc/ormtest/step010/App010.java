package com.jqc.ormtest.step010;

import com.jqc.ormtest.step010.entity.UserEntity;
import com.jqc.ormtest.step010.entity.XxxEntity_Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 主应用程序类
 */
public class App010 {
    /**
     * 应用程序主函数
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new App010().start();
    }

    /**
     * 测试开始
     */
    private void start() throws Exception {
        //加载Mysql驱动
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        //数据库连接地址
        String dbConnStr = "jdbc:mysql://localhost:3306/ormtest?user=root&password=root&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8";
        //创建数据库连接
        Connection conn = DriverManager.getConnection(dbConnStr);
        //简历陈述对象
        Statement st = conn.createStatement();

        //创建SQL查询
        String sql = "select * from t_user limit 200000";

        //执行查询
        ResultSet rs = st.executeQuery(sql);

        XxxEntity_Helper helper = new XxxEntity_Helper();

        //获取开始时间
        long start0 = System.currentTimeMillis();

        while (rs.next()) {
            UserEntity user = helper.create(UserEntity.class, rs);
            //System.out.println(user.getUserId()+ user.getUserName() + user.getPassword());
        }

        //获取结束时间
        long end0 = System.currentTimeMillis();

        //关闭数库连接
        st.close();
        conn.close();

        //打印实例化花费时间
        System.out.println("实例化花费时间 = " + (end0 - start0) + "ms");
    }
}
