package com.ppdai.das.console.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class DbUtil {

    public static void close(Connection conn) {
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement stat) {
        try {
            if (null != stat) {
                stat.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(PreparedStatement stat) {
        try {
            if (null != stat) {
                stat.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        try {
            if (null != conn) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
