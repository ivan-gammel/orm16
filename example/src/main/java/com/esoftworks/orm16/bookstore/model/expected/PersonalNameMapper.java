package com.esoftworks.orm16.bookstore.model.expected;

import com.esoftworks.orm16.bookstore.model.PersonalName;
import com.esoftworks.orm16.bookstore.model.ReferenceForm;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class PersonalNameMapper {

    private PersonalNameMapper() {}

    public static PersonalName map(ResultSet rs, int offset) throws SQLException {
        return new PersonalName(
                rs.getString(0 + offset),
                rs.getString(1 + offset),
                ReferenceForm.valueOf(rs.getString(2 + offset))
        );
    }

}
