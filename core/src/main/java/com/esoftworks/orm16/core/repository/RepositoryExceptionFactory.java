package com.esoftworks.orm16.core.repository;

import java.sql.SQLException;

public class RepositoryExceptionFactory {

    public static RepositoryException onDelete(SQLException e, Object... keys) {
        return new RepositoryException(e);
    }

    public static RepositoryException onCreate(SQLException e, Object... keys) {
        return new RepositoryException(e);
    }

    public static RepositoryException onQuery(SQLException e, Object... keys) {
        return new RepositoryException(e);
    }

    public static RepositoryException onUpdate(SQLException e, Object... keys) {
        return new RepositoryException(e);
    }

}
