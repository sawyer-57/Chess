package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;

import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import ui.ChessClient;

public class WebSocketClient extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();

    private String authToken;
    private Integer gameID;

    private ui.ChessClient ui;

    public void setUI(ui.ChessClient ui) {
        this.ui = ui;
    }

    public void connectIfNeeded(String url, ChessClient ui) {

        if (session != null && session.isOpen()) {
            return;
        }

        setUI(ui);
        connect(url);
    }

    public void connect(String serverUrl) {
        try {

            WebSocketContainer container =
                    ContainerProvider.getWebSocketContainer();

            session =
                    container.connectToServer(
                            this,
                            URI.create(serverUrl));

            session.setMaxIdleTimeout(0);

            System.out.println("WS connected");

            session.addMessageHandler(
                    new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(String data) {

                            ServerMessage msg =
                                    gson.fromJson(
                                            data,
                                            ServerMessage.class);

                            switch (msg.getServerMessageType()) {

                                case LOAD_GAME -> {
                                    LoadGameMessage load =
                                            gson.fromJson(
                                                    data,
                                                    LoadGameMessage.class);

                                    System.out.println("\n[BOARD UPDATE]");

                                    if (ui != null) {
                                        ui.updateGame(load.getGame());
                                    }
                                }

                                case NOTIFICATION -> {
                                    NotificationMessage note =
                                            gson.fromJson(
                                                    data,
                                                    NotificationMessage.class);

                                    System.out.println(
                                            "\n[NOTIFICATION] "
                                                    + note.getMessage());
                                }

                                case ERROR -> {
                                    ErrorMessage err =
                                            gson.fromJson(
                                                    data,
                                                    ErrorMessage.class);

                                    System.out.println(
                                            "\n[ERROR] "
                                                    + err.getErrorMessage());
                                }
                            }
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Object command) {
        try {

            if (session == null) {
                throw new IllegalStateException(
                        "WebSocket not connected");
            }

            String json = gson.toJson(command);

            session.getBasicRemote().sendText(json);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSession(String authToken, Integer gameID) {
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void sendConnect() {
        UserGameCommand connect = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );

        send(connect);
    }

    public void close() {
        try {
            if (session != null) {
                session.close();
                session = null;
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onOpen(Session session,
                       EndpointConfig config) {
        this.session = session;
    }

    @Override
    public void onClose(Session session,
                        jakarta.websocket.CloseReason reason) {

        System.out.println("WS closed: " + reason);

        this.session = null;
    }
}