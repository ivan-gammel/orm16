package com.esoftworks.orm16.bookstore.model.expected;

import com.esoftworks.orm16.bookstore.model.Author;
import com.esoftworks.orm16.core.jdbc.JdbcDatabase;
import com.esoftworks.orm16.core.repository.Repository;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static com.esoftworks.orm16.core.repository.RepositoryExceptionFactory.*;

public record ReferenceAuthorRepository(JdbcDatabase database) implements Repository<Author, UUID> {

    public Optional<Author> get(UUID key) {
        try {
            return database.query(
                            """
                                SELECT
                                uuid,
                                name_first,
                                name_last,
                                name_reference
                                FROM author WHERE
                                uuid=?""",
                        AuthorMapper::map,
                        key)
                    .stream()
                    .findFirst();
        } catch (SQLException e) {
            throw onQuery(e, key);
        }
    }

    @Override
    public Author add(Author value) {
        try {
            database.update(
                    """
                        INSERT INTO author (
                        uuid,
                        name_first,
                        name_last,
                        name_reference
                        ) VALUES(?, ?, ?, ?)""",
                    value.uuid(),
                    value.name().first(),
                    value.name().last(),
                    value.name().reference().toString()

            );
            return value;
        } catch (SQLException e) {
            throw onCreate(e, value.uuid());
        }
    }

    @Override
    public Author update(Author value) {
        try {
            database.update(
                    """
                        UPDATE author SET
                        name_first=?,
                        name_last=?,
                        name_reference=?
                        WHERE
                        uuid=?""",
                    value.name().first(),
                    value.name().last(),
                    value.name().reference().toString(),
                    value.uuid()
            );
            return value;
        } catch (SQLException e) {
            throw onUpdate(e, value.uuid());
        }
    }

    @Override
    public boolean remove(UUID key) {
        try {
            int changes = database.update(
                    """
                        DELETE FROM author WHERE
                        uuid=?""",
                    key
            );
            return changes > 0;
        } catch (SQLException e) {
            throw onDelete(e, key);
        }
    }


}
