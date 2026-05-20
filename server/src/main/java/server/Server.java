package server;

import io.javalin.*;
import service.*;
import dataaccess.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        SessionService sessionService = new SessionService(userDAO, authDAO);
        DatabaseService databaseService = new DatabaseService(userDAO, gameDAO, authDAO);

        
        ChessHandler handler = new ChessHandler(
            userService,
            gameService,
            sessionService,
            databaseService
        );

        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);
        javalin.delete("/db", handler::clear);

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(new model.ErrorResponse("Error: " + e.getMessage()));
        });
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.json(new model.ErrorResponse("Error: unauthorized"));
        });
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.json(new model.ErrorResponse("Error: already taken"));
        });
        javalin.exception(InvalidMoveException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.json(new model.ErrorResponse("Error: bad request"));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
