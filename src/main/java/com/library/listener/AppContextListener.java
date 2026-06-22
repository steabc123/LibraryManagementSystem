package com.library.listener;

import com.library.util.DBUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ComboPooledDataSource dataSource = DBUtil.getDataSource();
            dataSource.close();
            System.out.println("C3P0连接池已关闭");
        } catch (Exception e) {
            System.err.println("关闭C3P0连接池时发生异常: " + e.getMessage());
        }
    }
}
