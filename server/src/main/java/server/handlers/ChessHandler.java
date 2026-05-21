package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;

import service.*;
import service.requests.*;
import service.results.*;

import exception.*; 

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

    public void register(Context ctx) throws Exception {

        RegisterRequest request =
                gson.fromJson(ctx.body(), RegisterRequest.class);

        if (request == null
                || request.username() == null
                || request.password() == null
                || request.email() == null) {
                throw new BadRequestException();
        }

        RegisterResult result =
                userService.register(request);

        ctx.json(result);
    }

    public void login(Context ctx) throws Exception {

        LoginRequest request =
                gson.fromJson(ctx.body(), LoginRequest.class);

        if (request == null
                || request.username() == null
                || request.password() == null) {
                throw new BadRequestException();
        }

        LoginResult result =
                userService.login(request);

        ctx.json(result);
    }

    public void logout(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        LogoutRequest request = new LogoutRequest(authToken);

        userService.logout(request);

        ctx.status(200);
    }

    public void listGames(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        ListGamesRequest request = new ListGamesRequest(authToken);

        ListGamesResult result =
                gameService.listGames(request);

        ctx.json(result);
    }

    public void createGame(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        CreateGameRequest body = 
                gson.fromJson(ctx.body(), CreateGameRequest.class);

        if (body == null || body.gameName() == null) {
                throw new BadRequestException();
        }

        CreateGameRequest request =
                new CreateGameRequest(authToken, body.gameName());

        CreateGameResult result =
                gameService.createGame(request);

        ctx.json(result);
    }

    public void joinGame(Context ctx) throws Exception {

        String authToken = ctx.header("authorization");

        JoinGameRequest body = 
                gson.fromJson(ctx.body(), JoinGameRequest.class);

        if (body == null
                || body.playerColor() == null
                || body.gameID() <= 0) {
                throw new BadRequestException();
        }

        String color = body.playerColor();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
                throw new BadRequestException();
        }

        JoinGameRequest request =
                new JoinGameRequest(authToken, color, body.gameID());

        gameService.joinGame(request);

        ctx.status(200);
    }

    public void clear(Context ctx) throws Exception {

        clearService.clear();

        ctx.status(200);
    }
}