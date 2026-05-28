package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTest {

    private MySqlUserDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlUserDAO();

        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.createStatement();
            stmt.executeUpdate("TRUNCATE user");
        }
    }

    // ---------------- POSITIVE ----------------

    @Test
    void createUserPositive() throws Exception {
        UserData user = new UserData("alice", "hashedPass", "alice@email.com");

        dao.createUser(user);

        UserData result = dao.getUser("alice");

        assertNotNull(result);
        assertEquals("alice", result.username());
        assertEquals("hashedPass", result.password());
        assertEquals("alice@email.com", result.email());
    }

    @Test
    void getUserPositive() throws Exception {
        UserData user = new UserData("bob", "hash", "bob@email.com");

        dao.createUser(user);

        UserData result = dao.getUser("bob");

        assertNotNull(result);
    }

    @Test
    void clearPositive() throws Exception {

        UserData user1 =
                new UserData("alice", "hash", "a@a.com");

        UserData user2 =
                new UserData("bob", "hash", "b@b.com");

        dao.createUser(user1);
        dao.createUser(user2);

        dao.clear();

        assertNull(dao.getUser("alice"));
        assertNull(dao.getUser("bob"));
    }

    // ---------------- NEGATIVE ----------------

    @Test
    void getUserNegativeNotFound() throws Exception {
        UserData result = dao.getUser("ghost");

        assertNull(result);
    }

    @Test
    void createUserNegativeDuplicate() throws Exception {
        UserData user = new UserData("alice", "hash", "a@a.com");

        dao.createUser(user);

        assertThrows(Exception.class, () -> {
            dao.createUser(user);
        });
    }
}