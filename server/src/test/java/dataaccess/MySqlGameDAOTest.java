package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTest {

    private MySqlGameDAO dao;
    private final Gson gson = new Gson();

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlGameDAO();

        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.createStatement();
            stmt.executeUpdate("TRUNCATE game");
        }
    }

    private GameData makeGame(int id, String name) {
        ChessGame game = new ChessGame();

        return new GameData(id, null, null, name, game);
    }

    // ---------------- POSITIVE ----------------

    @Test
    void createGamePositive() throws Exception {
        GameData game = makeGame(1, "testGame");

        dao.createGame(game);

        GameData result = dao.getGame(1);

        assertNotNull(result);
        assertEquals("testGame", result.gameName());
    }

    @Test
    void listGamesPositive() throws Exception {
        dao.createGame(makeGame(1, "g1"));
        dao.createGame(makeGame(2, "g2"));

        List<GameData> games = dao.listGames();

        assertEquals(2, games.size());
    }

    @Test
    void updateGamePositive() throws Exception {
        GameData game = makeGame(1, "g1");

        dao.createGame(game);

        GameData updated = new GameData(
                1,
                "white",
                "black",
                "g1",
                new ChessGame()
        );

        dao.updateGame(updated);

        GameData result = dao.getGame(1);

        assertEquals("white", result.whiteUsername());
        assertEquals("black", result.blackUsername());
    }

    @Test
    void clearPositive() throws Exception {

        dao.createGame(makeGame(1, "g1"));
        dao.createGame(makeGame(2, "g2"));

        dao.clear();

        List<GameData> games = dao.listGames();

        assertEquals(0, games.size());
    }

    // ---------------- NEGATIVE ----------------

    @Test
    void createGameNegative() throws Exception {

        GameData game = makeGame(1, "g1");

        dao.createGame(game);

        GameData badGame =
                new GameData(
                        0,
                        null,
                        null,
                        null,
                        new ChessGame()
                );

        assertThrows(
                DataAccessException.class,
                () -> dao.createGame(badGame)
        );
    }

    @Test
    void listGamesNegativeEmpty() throws Exception {

        List<GameData> games = dao.listGames();

        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    void getGameNegative() throws Exception {
        assertNull(dao.getGame(999));
    }

    @Test
    void updateGameNegative() throws Exception {

        GameData game =
                new GameData(
                        999,
                        "white",
                        "black",
                        "ghost",
                        new ChessGame()
                );

        assertDoesNotThrow(() -> dao.updateGame(game));
    }
}
