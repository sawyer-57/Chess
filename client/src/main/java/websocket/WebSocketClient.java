package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import ui.ChessClient;
import chess.ChessGame;

import ui.ChessBoardUI;

public class WebSocketClient {

    private WebSocket socket;
    private final Gson gson = new Gson();

    private String authToken;
    private Integer gameID;

    private ui.ChessClient ui;

    public void setUI(ui.ChessClient ui) {
        this.ui = ui;
    }

    public void connectIfNeeded(String url, ChessClient ui) {
        if (socket != null) {
            return;
        }

        setUI(ui);
        connect(url);
    }

    public void connect(String serverUrl) {
        HttpClient client = HttpClient.newHttpClient();

        socket = client.newWebSocketBuilder()
                .buildAsync(URI.create(serverUrl), new WebSocket.Listener() {

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        System.out.println("WS connected");
                        WebSocket.Listener.super.onOpen(webSocket);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket,
                                                     CharSequence data,
                                                     boolean last) {

                        ServerMessage msg = gson.fromJson(data.toString(), ServerMessage.class);

                        switch (msg.getServerMessageType()) {

                            case LOAD_GAME -> {
                                LoadGameMessage load =
                                        gson.fromJson(data.toString(), LoadGameMessage.class);

                                System.out.println("\n[BOARD UPDATE]");

                                if (ui != null) {
                                    ui.updateGame(load.getGame());
                                }
                            }

                            case NOTIFICATION -> {
                                NotificationMessage note =
                                        gson.fromJson(data.toString(), NotificationMessage.class);

                                System.out.println("\n[NOTIFICATION] " + note.getMessage());
                            }

                            case ERROR -> {
                                ErrorMessage err =
                                        gson.fromJson(data.toString(), ErrorMessage.class);

                                System.out.println("\n[ERROR] " + err.getErrorMessage());
                            }
                        }

                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }

                }).join();
    }

    public void send(Object command) {
        if (socket == null) {
            throw new IllegalStateException("WebSocket not connected");
        }

        String json = gson.toJson(command);
        socket.sendText(json, true);
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
        socket.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
    }
}