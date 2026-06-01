package com.library.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DBUtil {
    // 创建C3P0连接池实例（使用默认配置）
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();

    /**
     * 从连接池获取数据库连接
     * @return 数据库连接对象
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭数据库连接，将连接归还到连接池
     * @param conn 数据库连接对象
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // 这里不是真正关闭连接，而是归还到连接池
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据源对象
     * @return C3P0数据源对象
     */
    public static ComboPooledDataSource getDataSource() {
        return dataSource;
    }

    /**
     * 将Timestamp转换为LocalDateTime（北京时间）
     */
    public static LocalDateTime convertToBeijingTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}

