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
        }
        else if (command.getCommandType() ==
                UserGameCommand.CommandType.MAKE_MOVE) {

            MakeMoveCommand moveCommand =
                    gson.fromJson(message, MakeMoveCommand.class);

            makeMove(moveCommand, ctx);
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

    private void makeMove(MakeMoveCommand command,
                          WsMessageContext ctx) {

        try {

            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new RuntimeException("Error: unauthorized");
            }

            String mover = authData.username();

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                throw new RuntimeException("Error: game not found");
            }

            if (!mover.equals(gameData.whiteUsername()) &&
                    !mover.equals(gameData.blackUsername())) {
                throw new RuntimeException("Error: observers cannot move");
            }

            ChessGame.TeamColor playerColor;

            if (mover.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            var game = gameData.game();

            ChessPiece piece =
                    game.getBoard().getPiece(command.getMove().getStartPosition());

            if (piece == null || piece.getTeamColor() != playerColor) {
                throw new RuntimeException("Error: cannot move opponent piece");
            }

            try {
                game.makeMove(command.getMove());
            } catch (Exception e) {
                throw new RuntimeException("Error: invalid move");
            }

            gameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game);

            gameDAO.updateGame(gameData);

            LoadGameMessage loadMessage =
                    new LoadGameMessage(game);

            connections.broadcast(
                    command.getGameID(),
                    gson.toJson(loadMessage)
            );

            NotificationMessage notification =
                    new NotificationMessage(
                            mover + " moved " +
                                    command.getMove().getStartPosition() +
                                    " to " +
                                    command.getMove().getEndPosition()
                    );

            connections.broadcastExcept(
                    command.getGameID(),
                    mover,
                    gson.toJson(notification)
            );

        } catch (Exception e) {

            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

}