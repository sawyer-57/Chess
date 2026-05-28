package server;

import io.javalin.Javalin;
import com.google.gson.Gson;

import server.handlers.ChessHandler;
import service.*; 
import dataaccess.*;
import exception.*;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
        });

        // Register your endpoints and exception handlers here.
        UserDAO userDAO = new MySqlUserDAO();
        GameDAO gameDAO = new MySqlGameDAO();
        AuthDAO authDAO = new MySqlAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        
        ChessHandler handler = new ChessHandler(
            userService,
            gameService, 
            clearService
        );

        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);
        javalin.delete("/db", handler::clear);

        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(
                    new model.ErrorResponse(
                            "Error: unauthorized")));
        });

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(
                    new model.ErrorResponse(
                            "Error: already taken")));
        });

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(
                    new model.ErrorResponse(
                            "Error: bad request")));
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(
                    new model.ErrorResponse(
                            "Error: " + e.getMessage())));
        });
    }

    public int run(int desiredPort) {
        try {
            new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
