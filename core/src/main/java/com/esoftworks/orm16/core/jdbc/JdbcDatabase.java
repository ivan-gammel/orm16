package com.esoftworks.orm16.core.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcDatabase {

    private final DataSource dataSource;

    public JdbcDatabase(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(String sql, JdbcMapper<T> mapper, Object... params) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet resultSet = stmt.executeQuery()) {
                    var list = new ArrayList<T>();
                    while (resultSet.next()) {
                        list.add(mapper.map(resultSet));
                    }
                    return list;
                }
            }
        }
    }

    public int count(String sql, Object... params) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.first()) {
                        return resultSet.getInt(1);
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    public int update(String sql, Object... params) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                return stmt.executeUpdate();
            }
        }
    }
}
