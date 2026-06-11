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

import websocket.messages.LoadGameMessage;

public class WebSocketHandler {

    private final Gson gson = new Gson();
    private final GameDAO gameDAO = new MySqlGameDAO();

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

            GameData gameData =
                    gameDAO.getGame(command.getGameID());

            LoadGameMessage message =
                    new LoadGameMessage(gameData.game());

            ctx.send(gson.toJson(message));

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}