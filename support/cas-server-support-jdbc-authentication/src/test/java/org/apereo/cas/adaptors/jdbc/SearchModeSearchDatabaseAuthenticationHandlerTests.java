package org.apereo.cas.adaptors.jdbc;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SearchModeSearchDatabaseAuthenticationHandler}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    DatabaseAuthenticationTestConfiguration.class
})
@DirtiesContext
public class SearchModeSearchDatabaseAuthenticationHandlerTests {
    private SearchModeSearchDatabaseAuthenticationHandler handler;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    private static String getSqlInsertStatementToCreateUserAccount(final int i) {
        return String.format("insert into cassearchusers (username, password) values('%s', '%s');", "user" + i, "psw" + i);
    }

    @BeforeEach
    public void initialize() throws SQLException {
        this.handler = new SearchModeSearchDatabaseAuthenticationHandler("", null, null, null, this.dataSource, "username", "password", "cassearchusers");

        val c = this.dataSource.getConnection();
        val s = c.createStatement();
        c.setAutoCommit(true);

        s.execute(getSqlInsertStatementToCreateUserAccount(0));
        for (var i = 0; i < 10; i++) {
            s.execute(getSqlInsertStatementToCreateUserAccount(i));
        }

        c.close();
    }

    @AfterEach
    public void afterEachTest() throws SQLException {
        val c = this.dataSource.getConnection();
        val s = c.createStatement();
        c.setAutoCommit(true);
        s.execute("delete from casusers;");
        c.close();
    }

    @Test
    public void verifyNotFoundUser() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("hello", "world");

        assertThrows(FailedLoginException.class, () -> this.handler.authenticate(c));
    }

    @Test
    public void verifyFoundUser() {
        val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user3", "psw3");

        //noinspection CodeBlock2Expr
        assertDoesNotThrow(() -> {
            assertNotNull(this.handler.authenticate(c));
        });
    }

    @Test
    public void verifyMultipleUsersFound() {
        assertDoesNotThrow(() -> {
            val c = CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("user0", "psw0");
            assertNotNull(this.handler.authenticate(c));
        });
    }

    @Entity(name = "cassearchusers")
    public static class UsersTable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;
        private String password;
    }
}
