package client;

import org.junit.jupiter.api.*;
import server.Server;

import service.results.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDatabase()
            throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerPositive()
            throws Exception {
        RegisterResult result =
                facade.register(
                        "player1",
                        "password",
                        "player1@email.com");
        assertNotNull(result);
        assertNotNull(result.authToken());
    }
    @Test
    void registerNegativeDuplicate()
            throws Exception {
        facade.register(
                "player1",
                "password",
                "player1@email.com");
        assertThrows(
                Exception.class,
                () -> facade.register(
                        "player1",
                        "password",
                        "player1@email.com"));
    }


    @Test
    void loginPositive()
            throws Exception {
        facade.register(
                "player1",
                "password",
                "player1@email.com");
        LoginResult result =
                facade.login(
                        "player1",
                        "password");
        assertNotNull(result);
        assertNotNull(result.authToken());
    }
    @Test
    void loginNegativeBadPassword() {
        assertThrows(
                Exception.class,
                () -> facade.login(
                        "player1",
                        "wrongPassword"));
    }


    @Test
    void logoutPositive()
            throws Exception {
        RegisterResult auth =
                facade.register(
                        "player1",
                        "password",
                        "player1@email.com");
        assertDoesNotThrow(
                () -> facade.logout(
                        auth.authToken()));
    }
    @Test
    void logoutNegativeBadToken() {
        assertThrows(
                Exception.class,
                () -> facade.logout(
                        "badToken"));
    }


    @Test
    void createGamePositive()
            throws Exception {
        RegisterResult auth =
                facade.register(
                        "player1",
                        "password",
                        "player1@email.com");
        CreateGameResult result =
                facade.createGame(
                        auth.authToken(),
                        "GameOne");
        assertTrue(result.gameID() > 0);
    }
    @Test
    void createGameNegativeBadToken() {
        assertThrows(
                Exception.class,
                () -> facade.createGame(
                        "badToken",
                        "GameOne"));
    }


    @Test
    void listGamesPositive()
            throws Exception {
        RegisterResult auth =
                facade.register(
                        "player1",
                        "password",
                        "player1@email.com");
        facade.createGame(
                auth.authToken(),
                "GameOne");
        ListGamesResult result =
                facade.listGames(
                        auth.authToken());
        assertEquals(
                1,
                result.games().size());
    }
    @Test
    void listGamesNegativeBadToken() {
        assertThrows(
                Exception.class,
                () -> facade.listGames(
                        "badToken"));
    }


    @Test
    void joinGamePositive()
            throws Exception {
        RegisterResult auth =
                facade.register(
                        "player1",
                        "password",
                        "player1@email.com");
        CreateGameResult game =
                facade.createGame(
                        auth.authToken(),
                        "GameOne");
        assertDoesNotThrow(
                () -> facade.joinGame(
                        auth.authToken(),
                        "WHITE",
                        game.gameID()));
    }
    @Test
    void joinGameNegativeBadGame()
            throws Exception {
        RegisterResult auth =
                facade.register(
                        "player1",
                        "password",
                        "player1@email.com");
        assertThrows(
                Exception.class,
                () -> facade.joinGame(
                        auth.authToken(),
                        "WHITE",
                        99999));
    }
}
