package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WebSocketClient {

    private WebSocket socket;
    private final Gson gson = new Gson();

    private String authToken;
    private Integer gameID;

    private MessageHandler handler;

    public interface MessageHandler {
        void onMessage(String message);
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

                        String msg = data.toString();
                        System.out.println("WS message: " + data);

                        if (handler != null) {
                            handler.onMessage(msg);
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

    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
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