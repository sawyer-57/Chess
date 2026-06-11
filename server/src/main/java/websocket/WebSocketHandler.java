package websocket;

import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsMessageContext;

import com.google.gson.Gson;
import io.javalin.websocket.*;

import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;

import model.GameData;
import dataaccess.GameDAO;
import dataaccess.MySqlGameDAO;
import model.AuthData;
import dataaccess.AuthDAO;
import dataaccess.MySqlAuthDAO;

import websocket.messages.LoadGameMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import chess.ChessGame;
import chess.ChessPiece;

public class WebSocketHandler {

    private final Gson gson = new Gson();
    private final GameDAO gameDAO = new MySqlGameDAO();
    private final AuthDAO authDAO = new MySqlAuthDAO();
    private final ConnectionManager connections =
            new ConnectionManager();

    public void onConnect(WsConnectContext ctx) {
        System.out.println("WebSocket connected");
    }

    public void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }

    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();

        UserGameCommand command =
                gson.fromJson(message, UserGameCommand.class);

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            connect(command, ctx);
        } else if (command.getCommandType() ==
                UserGameCommand.CommandType.MAKE_MOVE) {

            MakeMoveCommand moveCommand =
                    gson.fromJson(message, MakeMoveCommand.class);

            makeMove(moveCommand, ctx);
        } else if (command.getCommandType() ==
                UserGameCommand.CommandType.RESIGN) {

            resign(command, ctx);
        }

        System.out.println("Received command: "
                + command.getCommandType());
    }


    private void connect(UserGameCommand command,
                         WsMessageContext ctx) {
        try {

            AuthData authData =
                    authDAO.getAuth(command.getAuthToken());

            if (authData == null) {
                throw new RuntimeException("Unauthorized");
            }

            GameData gameData =
                    gameDAO.getGame(command.getGameID());

            if (gameData == null) {
                throw new RuntimeException("Game not found");
            }

            String username =
                    authData.username();

            connections.remove(command.getGameID(), username);
            connections.add(command.getGameID(), username, ctx);

            LoadGameMessage message =
                    new LoadGameMessage(gameData.game());

            ctx.send(gson.toJson(message));

            NotificationMessage joinNotification =
                    new NotificationMessage(username + " joined the game");

            connections.broadcastExcept(
                    command.getGameID(),
                    username,
                    gson.toJson(joinNotification)
            );

        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));

        }
    }

    private void makeMove(MakeMoveCommand command, WsMessageContext ctx) {

        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new RuntimeException("Error: unauthorized");
            }

            String mover = authData.username();

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null || gameData.game() == null) {
                throw new RuntimeException("Error: game not found");
            }

            ChessGame game = gameData.game();

            if (game.isResigned()) {
                ctx.send(gson.toJson(new ErrorMessage("Error: game is over")));
                return;
            }

            if (!mover.equals(gameData.whiteUsername()) &&
                    !mover.equals(gameData.blackUsername())) {
                throw new RuntimeException("Error: observers cannot move");
            }

            ChessGame.TeamColor color =
                    mover.equals(gameData.whiteUsername())
                            ? ChessGame.TeamColor.WHITE
                            : ChessGame.TeamColor.BLACK;

            ChessPiece piece =
                    game.getBoard().getPiece(command.getMove().getStartPosition());

            if (piece == null || piece.getTeamColor() != color) {
                throw new RuntimeException("Error: cannot move opponent piece");
            }

            if (game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                    game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                    game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                throw new RuntimeException("Error: game is over");
            }

            try {
                game.makeMove(command.getMove());
            } catch (Exception e) {
                throw new RuntimeException("Error: invalid move");
            }

            gameDAO.updateGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            ));

            connections.broadcast(
                    command.getGameID(),
                    gson.toJson(new LoadGameMessage(game))
            );

            ChessGame.TeamColor opponent =
                    (color == ChessGame.TeamColor.WHITE)
                            ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;

            boolean checkmate = game.isInCheckmate(opponent);
            boolean stalemate = game.isInStalemate(opponent);
            boolean check = game.isInCheck(opponent);

            connections.broadcastExcept(
                    command.getGameID(),
                    mover,
                    gson.toJson(new NotificationMessage(
                            mover + " moved " +
                                    command.getMove().getStartPosition() +
                                    " to " +
                                    command.getMove().getEndPosition()
                    ))
            );

            if (checkmate) {
                connections.broadcast(
                        command.getGameID(),
                        gson.toJson(new NotificationMessage("checkmate"))
                );
            } else if (stalemate) {
                connections.broadcast(
                        command.getGameID(),
                        gson.toJson(new NotificationMessage("stalemate"))
                );
            } else if (check) {
                connections.broadcast(
                        command.getGameID(),
                        gson.toJson(new NotificationMessage("check"))
                );
            }

        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    private void resign(UserGameCommand command, WsMessageContext ctx) {
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new RuntimeException("Error: unauthorized");
            }

            String username = authData.username();

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null || gameData.game() == null) {
                throw new RuntimeException("Error: game not found");
            }

            if (!username.equals(gameData.whiteUsername()) &&
                    !username.equals(gameData.blackUsername())) {
                throw new RuntimeException("Error: observer cannot resign");
            }

            ChessGame game = gameData.game();

            if (game.isResigned()) {
                throw new RuntimeException("Error: game is over");
            }

            game.resign();

            gameDAO.updateGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            ));

            connections.broadcast(
                    command.getGameID(),
                    gson.toJson(new NotificationMessage(username + " resigned"))
            );

        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }
}