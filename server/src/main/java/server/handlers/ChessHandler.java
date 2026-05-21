package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;

import service.*;
import service.requests.*;
import service.results.*;

public class ChessHandler {

    private final Gson gson = new Gson();

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public ChessHandler(UserService userService,
                        GameService gameService, 
                        ClearService clearService) {

        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;
    }

    public Object register(Context ctx) throws Exception {

        RegisterRequest request =
                gson.fromJson(ctx.body(), RegisterRequest.class);

        RegisterResult result =
                userService.register(request);

        return result;
    }

    public Object login(Context ctx) throws Exception {

        LoginRequest request =
                gson.fromJson(ctx.body(), LoginRequest.class);

        LoginResult result =
                userService.login(request);

        return result;
    }

    public Object logout(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        LogoutRequest request = new LogoutRequest(authToken);

        userService.logout(request);

        return new Object();
    }

    public Object listGames(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        ListGamesRequest request = new ListGamesRequest(authToken);

        ListGamesResult result =
                gameService.listGames(request);

        return result;
    }

    public Object createGame(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        CreateGameRequest request =
                gson.fromJson(ctx.body(), CreateGameRequest.class);

        request = new CreateGameRequest(authToken, request.gameName());

        CreateGameResult result =
                gameService.createGame(request);

        return result;
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

        return new Object();
    }

    public Object clear(Context ctx) throws Exception {

        clearService.clear();

        return new Object();
    }
}