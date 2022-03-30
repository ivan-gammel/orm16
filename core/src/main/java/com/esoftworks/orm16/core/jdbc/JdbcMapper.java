package com.esoftworks.orm16.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JdbcMapper<T> {

    T map(ResultSet rs) throws SQLException;

}
