package service;

import dataaccess.*;
import model.*;
import service.requests.*;
import service.results.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;

    private String authToken;

    @BeforeEach
    public void setup()
            throws Exception{

        GameDAO gameDAO =
                new MemoryGameDAO();

        AuthDAO authDAO =
                new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData(
                        "token123",
                        "bob"));

        authToken="token123";

        gameService=
                new GameService(
                        gameDAO,
                        authDAO);
    }

    @Test
    public void createGamePositive()
            throws Exception{

        CreateGameResult result =
                gameService.createGame(
                        new CreateGameRequest(
                                authToken,
                                "Chess Game"));

        assertTrue(
                result.gameID()>0);
    }

    @Test
    public void createGameNegative(){

        assertThrows(
                Exception.class,
                () -> {

                    gameService.createGame(
                            new CreateGameRequest(
                                    "badtoken",
                                    "Game"));
                });
    }

    @Test
    public void listGamesPositive()
            throws Exception{

        gameService.createGame(
                new CreateGameRequest(
                        authToken,
                        "game"));

        ListGamesResult result =
                gameService.listGames(
                        new ListGamesRequest(
                                authToken));

        assertEquals(
                1,
                result.games().size());
    }

    @Test
    public void listGamesNegative(){

        assertThrows(
                Exception.class,
                () -> {

                    gameService.listGames(
                            new ListGamesRequest(
                                    "badtoken"));
                });
    }

    @Test
    public void joinGamePositive()
            throws Exception{

        CreateGameResult result =
                gameService.createGame(
                        new CreateGameRequest(
                                authToken,
                                "game"));

        gameService.joinGame(
                new JoinGameRequest(
                        authToken,
                        "WHITE",
                        result.gameID()));

        assertTrue(true);
    }

    @Test
    public void joinGameNegative()
            throws Exception{

        CreateGameResult result =
                gameService.createGame(
                        new CreateGameRequest(
                                authToken,
                                "game"));

        gameService.joinGame(
                new JoinGameRequest(
                        authToken,
                        "WHITE",
                        result.gameID()));

        assertThrows(
                Exception.class,
                () -> {

                    gameService.joinGame(
                            new JoinGameRequest(
                                    authToken,
                                    "WHITE",
                                    result.gameID()));
                });
    }
}