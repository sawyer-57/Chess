package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTest {

    private MySqlAuthDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlAuthDAO();

        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.createStatement();
            stmt.executeUpdate("TRUNCATE auth");
        }
    }

    // ---------------- POSITIVE ----------------

    @Test
    void createAuthPositive() throws Exception {
        AuthData auth = new AuthData("token123", "alice");

        dao.createAuth(auth);

        AuthData result = dao.getAuth("token123");

        assertNotNull(result);
        assertEquals("alice", result.username());
    }

    @Test
    void deleteAuthPositive() throws Exception {
        AuthData auth = new AuthData("token123", "alice");

        dao.createAuth(auth);
        dao.deleteAuth("token123");

        assertNull(dao.getAuth("token123"));
    }

    // ---------------- NEGATIVE ----------------

    @Test
    void createAuthNegativeDuplicate() throws Exception {

        AuthData auth = new AuthData("token123", "alice");

        dao.createAuth(auth);

        assertThrows(
                DataAccessException.class,
                () -> dao.createAuth(auth)
        );
    }

    @Test
    void getAuthNegative() throws Exception {
        assertNull(dao.getAuth("badtoken"));
    }

    @Test
    void deleteAuthNegativeDoesNotCrash() throws Exception {
        // should not throw even if token doesn't exist
        assertDoesNotThrow(() -> dao.deleteAuth("missing"));
    }
}