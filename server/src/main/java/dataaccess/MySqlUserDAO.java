package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {
    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = """
        INSERT INTO user
        (username, password, email)
        VALUES (?, ?, ?)
        """;

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            stmt.setString(3, user.email());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error creating user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = """
        SELECT username, password, email
        FROM user
        WHERE username = ?
        """;

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            var rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }

            return null;

        } catch (Exception e) {
            throw new DataAccessException("Error getting user", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Error clearing users", e);
        }
    }
}
