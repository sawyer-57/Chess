package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import service.*;
import request.*;
import response.*;

public class ChessHandler {

    private final Gson gson = new Gson();

    private final UserService userService;
    private final GameService gameService;
    private final SessionService sessionService;
    private final DatabaseService databaseService;

    public ChessHandler(UserService userService,
                        GameService gameService,
                        SessionService sessionService,
                        DatabaseService databaseService) {

        this.userService = userService;
        this.gameService = gameService;
        this.sessionService = sessionService;
        this.databaseService = databaseService;
    }

    public Object register(Context ctx) throws Exception {

        RegisterRequest request =
                gson.fromJson(ctx.body(), RegisterRequest.class);

        RegisterResponse response =
                userService.register(request);

        return response;
    }

    public Object login(Context ctx) throws Exception {

        LoginRequest request =
                gson.fromJson(ctx.body(), LoginRequest.class);

        LoginResponse response =
                userService.login(request);

        return response;
    }

    public Object logout(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        LogoutRequest request = new LogoutRequest(authToken);

        sessionService.logout(request);

        return new EmptyResponse();
    }

    public Object listGames(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        ListGamesRequest request = new ListGamesRequest(authToken);

        ListGamesResponse response =
                gameService.listGames(request);

        return response;
    }

    public Object createGame(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        CreateGameRequest request =
                gson.fromJson(ctx.body(), CreateGameRequest.class);

        request = new CreateGameRequest(authToken, request.gameName());

        CreateGameResponse response =
                gameService.createGame(request);

        return response;
    }

    public Object joinGame(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        JoinGameRequest request =
                gson.fromJson(ctx.body(), JoinGameRequest.class);

        request = new JoinGameRequest(
                authToken,
                request.playerColor(),
                request.gameID()
        );

        gameService.joinGame(request);

        return new EmptyResponse();
    }

    public Object clear(Context ctx) throws Exception {

        databaseService.clear();

        return new EmptyResponse();
    }
}