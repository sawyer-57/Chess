package websocket;

import io.javalin.websocket.WsContext;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<String, WsContext> connections =
            new ConcurrentHashMap<>();

    public void add(String username, WsContext session) {
        connections.put(username, session);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public WsContext get(String username) {
        return connections.get(username);
    }
}