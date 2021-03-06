package com.example.jdbc;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.time.Instant;
import com.esoftworks.orm16.core.repository.Repository;
import com.esoftworks.orm16.core.jdbc.*;
import com.example.Document;

import static java.lang.String.valueOf;
import static java.lang.String.join;
import static com.esoftworks.orm16.core.repository.RepositoryExceptionFactory.*;

public record DocumentRepository(JdbcDatabase database)
        implements Repository<Document, UUID> {

    public DocumentRepository {
        if (database == null) throw new NullPointerException("database");
    }


    public static class DocumentMapper {
        public static Document map(ResultSet rs) throws SQLException {
            return new Document(
                    (UUID) rs.getObject("uuid"),
                    rs.getTimestamp("created").toInstant(),
                    rs.getString("subject"),
                    rs.getString("content")
            );
        }
    }


    public Optional<Document> get(UUID uuid) {
        try {
            return database.query(
                            """
                                SELECT uuid, created, subject, content
                                FROM documents
                                WHERE uuid=?""",
                            DocumentMapper::map,
                            uuid
                    )
                    .stream()
                    .findFirst();
        } catch (SQLException e) {
            throw onQuery(e, uuid);
        }
    }

    @Override
    public Document add(Document value) {
        try {
            database.update(
                    """
                        INSERT INTO documents (uuid, created, subject, content)
                        VALUES (?, ?, ?, ?)""",
                    value.uuid(),
                    Timestamp.from(value.created()),
                    value.subject(),
                    value.content()
            );
            return value;
        } catch (SQLException e) {
            throw onCreate(e, value.uuid());
        }
    }

    @Override
    public Document update(Document value) {
        try {
            database.update(
                    """
                        UPDATE documents
                        SET created=?, subject=?, content=?
                        WHERE uuid=?""",
                    Timestamp.from(value.created()),
                    value.subject(),
                    value.content(),
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
            int changes = database.update("DELETE FROM documents WHERE uuid=?", uuid);
            return changes > 0;
        } catch (SQLException e) {
            throw onDelete(e, uuid);
        }
    }



}
