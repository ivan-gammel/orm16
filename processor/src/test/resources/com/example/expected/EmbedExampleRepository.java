package com.example.jdbc;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import com.example.PersonalName;
import com.esoftworks.orm16.core.repository.Repository;
import com.esoftworks.orm16.core.jdbc.*;
import com.example.EmbedExample;

import static java.lang.String.valueOf;
import static java.lang.String.join;
import static com.esoftworks.orm16.core.repository.RepositoryExceptionFactory.*;

public record EmbedExampleRepository(JdbcDatabase database)
        implements Repository<EmbedExample, UUID> {

    public EmbedExampleRepository {
        if (database == null) throw new NullPointerException("database");
    }


    public static class PersonalNameMapper {
        public static PersonalName map(ResultSet rs) throws SQLException {
            return new PersonalName(
                    rs.getString("name_first"),
                    rs.getString("name_last")
            );
        }
    }

    public static class EmbedExampleMapper {
        public static EmbedExample map(ResultSet rs) throws SQLException {
            return new EmbedExample(
                    (UUID) rs.getObject("uuid"),
                    PersonalNameMapper.map(rs)
            );
        }
    }


    public Optional<EmbedExample> get(UUID uuid) {
        try {
            return database.query(
                            """
                                SELECT uuid, name_first, name_last
                                FROM embed_examples
                                WHERE uuid=?""",
                            EmbedExampleMapper::map,
                            uuid
                    )
                    .stream()
                    .findFirst();
        } catch (SQLException e) {
            throw onQuery(e, uuid);
        }
    }

    @Override
    public EmbedExample add(EmbedExample value) {
        try {
            database.update(
                    """
                        INSERT INTO embed_examples (uuid, name_first, name_last)
                        VALUES (?, ?, ?)""",
                    value.uuid(),
                    value.name().first(),
                    value.name().last()
            );
            return value;
        } catch (SQLException e) {
            throw onCreate(e, value.uuid());
        }
    }

    @Override
    public EmbedExample update(EmbedExample value) {
        try {
            database.update(
                    """
                        UPDATE embed_examples
                        SET name_first=?, name_last=?
                        WHERE uuid=?""",
                    value.name().first(),
                    value.name().last(),
                    value.uuid()
            );
            return value;
        } catch (SQLException e) {
            throw onUpdate(e, value.uuid());
        }
    }

    @Override
    public boolean remove(UUID uuid) {
        try {
            int changes = database.update("DELETE FROM embed_examples WHERE uuid=?", uuid);
            return changes > 0;
        } catch (SQLException e) {
            throw onDelete(e, uuid);
        }
    }



}
