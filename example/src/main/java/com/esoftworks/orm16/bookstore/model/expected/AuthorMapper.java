package com.esoftworks.orm16.bookstore.model.expected;

import com.esoftworks.orm16.bookstore.model.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class AuthorMapper {

    private AuthorMapper() {}

    public static Author map(ResultSet rs) throws SQLException {
        return map(rs, 1);
    }

    public static Author map(ResultSet rs, int offset) throws SQLException {
        return new Author(
                rs.getObject(0 + offset, UUID.class),
                PersonalNameMapper.map(rs, 1 + offset)
        );
    }

}
