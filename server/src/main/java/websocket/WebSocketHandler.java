package websocket;

import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsMessageContext;

public class WebSocketHandler {

    public void onConnect(WsConnectContext ctx) {
        System.out.println("WebSocket connected");
    }

    public void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }

    public void onMessage(WsMessageContext ctx) {
        System.out.println("Message received: " + ctx.message());
    }
}