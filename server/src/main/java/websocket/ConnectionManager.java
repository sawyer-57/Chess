package websocket;

import io.javalin.websocket.WsContext;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // gameID → (username → connection)
    private final ConcurrentHashMap<Integer,
            ConcurrentHashMap<String, WsContext>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String username, WsContext session) {
        connections
                .computeIfAbsent(gameID, k -> new ConcurrentHashMap<>())
                .put(username, session);
    }

    public void remove(Integer gameID, String username) {
        if (!connections.containsKey(gameID)) {
            return;
        }

        ConcurrentHashMap<String, WsContext> gameMap = connections.get(gameID);
        gameMap.remove(username);

        if (gameMap.isEmpty()) {
            connections.remove(gameID);
        }
    }

    public void broadcast(Integer gameID, String message) {
        if (!connections.containsKey(gameID)) {
            return;
        }

        for (WsContext ctx : connections.get(gameID).values()) {
            try {
                ctx.send(message);
            } catch (Exception e) {
            }
        }
    }

    public void broadcastExcept(Integer gameID, String username, String message) {
        if (!connections.containsKey(gameID)) {
            return;
        }

        for (var entry : connections.get(gameID).entrySet()) {
            if (!entry.getKey().equals(username)) {
                try {
                    entry.getValue().send(message);
                } catch (Exception e) {
                }
            }
        }
    }
}