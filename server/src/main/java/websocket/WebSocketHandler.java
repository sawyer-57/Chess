package websocket;

import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsMessageContext;

import com.google.gson.Gson;
import io.javalin.websocket.*;

import websocket.commands.UserGameCommand;

import model.GameData;
import dataaccess.GameDAO;
import dataaccess.MySqlGameDAO;
import model.AuthData;
import dataaccess.AuthDAO;
import dataaccess.MySqlAuthDAO;

import websocket.messages.LoadGameMessage;
import websocket.messages.ErrorMessage;

public class WebSocketHandler {

    private final Gson gson = new Gson();
    private final GameDAO gameDAO = new MySqlGameDAO();
    private final AuthDAO authDAO = new MySqlAuthDAO();

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

            LoadGameMessage message =
                    new LoadGameMessage(gameData.game());

            ctx.send(gson.toJson(message));

        } catch (Exception e) {
            ErrorMessage error =
                    new ErrorMessage(e.getMessage());

            ctx.send(gson.toJson(error));

        }
    }

}