package com.esoftworks.orm16.bookstore.model.expected;

import com.esoftworks.orm16.bookstore.model.Author;
import com.esoftworks.orm16.bookstore.model.PersonalName;
import com.esoftworks.orm16.core.jdbc.JdbcDatabase;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static com.esoftworks.orm16.bookstore.model.ReferenceForm.MALE;
import static com.esoftworks.orm16.bookstore.model.ReferenceForm.UNKNOWN;
import static org.junit.jupiter.api.Assertions.*;

public class ReferenceAuthorRepositoryTest {

    private static HikariDataSource dataSource;
    private ReferenceAuthorRepository authors;
    private JdbcDatabase database;

    @BeforeAll
    public static void createDatabase() throws SQLException {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:hsqldb:mem:mymemdb");
        config.setUsername("SA");
        config.setPassword("");
        dataSource = new HikariDataSource(config);

    }

    @BeforeEach
    public void createTables() throws SQLException {
        database = new JdbcDatabase(dataSource);
        database.update("""
                        CREATE TABLE author (
                            uuid UUID NOT NULL PRIMARY KEY,
                            name_first VARCHAR(32) NOT NULL,
                            name_last VARCHAR(32) NOT NULL,
                            name_reference VARCHAR(16) NOT NULL
                        )""");
        authors = new ReferenceAuthorRepository(database);
    }

    @Test
    public void shouldFindRecordById() {
        var author = new Author(UUID.randomUUID(), new PersonalName("Jane", "Doe", UNKNOWN));
        authors.add(author);
        Optional<Author> result = authors.get(author.uuid());
        assertTrue(result.isPresent());
        var actual = result.get();
        assertEquals(author, actual);
    }

    @Test
    public void shouldUpdateRecord() {
        var author = new Author(UUID.randomUUID(), new PersonalName("Jane", "Doe", UNKNOWN));
        authors.add(author);

        var modified = author.with(new PersonalName("John", "Doe", MALE));
        authors.update(modified);

        Optional<Author> result = authors.get(author.uuid());
        assertTrue(result.isPresent());
        var actual = result.get();
        assertEquals(modified, actual);
    }

    @Test
    public void shouldDeleteRecord() {
        var author = new Author(UUID.randomUUID(), new PersonalName("Jane", "Doe", UNKNOWN));
        authors.add(author);
        authors.remove(author.uuid());
        Optional<Author> result = authors.get(author.uuid());
        assertFalse(result.isPresent());
    }

    @AfterEach
    public void destroyTables() throws SQLException {
        database.update("DROP TABLE author");
    }

}
