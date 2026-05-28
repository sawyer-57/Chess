package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySqlAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = """
            INSERT INTO auth (authToken, username)
            VALUES (?, ?)
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Failed to create auth", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = """
            SELECT authToken, username
            FROM auth
            WHERE authToken = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new AuthData(
                        rs.getString("authToken"),
                        rs.getString("username")
                );
            }

            return null;

        } catch (Exception e) {
            throw new DataAccessException("Failed to get auth", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = """
            DELETE FROM auth
            WHERE authToken = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Failed to delete auth", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE auth";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Failed to clear auth table", e);
        }
    }
}
