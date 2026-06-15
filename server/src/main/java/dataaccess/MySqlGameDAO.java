package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = """
            INSERT INTO game
            (whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());

            String gameJson =
                    gson.toJson(game.game());

            stmt.setString(4, gameJson);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = """
            SELECT *
            FROM game
            WHERE gameID = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String json = rs.getString("game");

                if (json == null || json.isBlank()) {
                    throw new DataAccessException("Game data missing for gameID " + gameID);
                }

                ChessGame game;
                try {
                    game = gson.fromJson(json, ChessGame.class);
                } catch (Exception e) {
                    throw new DataAccessException("Failed to deserialize ChessGame: " + e.getMessage());
                }

                if (game == null) {
                    throw new DataAccessException("ChessGame deserialized as null for gameID " + gameID);
                }

                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                );
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games =
                new ArrayList<>();

        String sql = """
            SELECT *
            FROM game
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                ChessGame game =
                        gson.fromJson(
                                rs.getString("game"),
                                ChessGame.class
                        );

                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                ));
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
            UPDATE game
            SET whiteUsername = ?,
                blackUsername = ?,
                gameName = ?,
                game = ?
            WHERE gameID = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());

            if (game.game() == null) {
                throw new DataAccessException("Cannot update DB: ChessGame is null");
            }

            String gameJson =
                    gson.toJson(game.game());

            stmt.setString(4, gameJson);

            stmt.setInt(5, game.gameID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE game";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
