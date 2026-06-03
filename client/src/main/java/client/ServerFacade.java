package client;

import com.google.gson.Gson;
import service.requests.*;
import service.results.*;
import model.*;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(
            String username,
            String password,
            String email)
            throws Exception {

        RegisterRequest request =
                new RegisterRequest(
                        username,
                        password,
                        email);
        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(serverUrl + "/user")
                                .toURL()
                                .openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream body =
                     connection.getOutputStream()) {
            body.write(
                    gson.toJson(request).getBytes());
        }
        if (connection.getResponseCode() != 200) {
            throw new Exception("register failed");
        }

        return gson.fromJson(
                new InputStreamReader(
                        connection.getInputStream()),
                RegisterResult.class);
    }


    public LoginResult login(
            String username,
            String password)
            throws Exception {

        LoginRequest request =
                new LoginRequest(
                        username,
                        password);
        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(serverUrl + "/session")
                                .toURL()
                                .openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream body =
                    connection.getOutputStream()) {
            body.write(
                    gson.toJson(request).getBytes());
        }

        if (connection.getResponseCode() != 200) {
            throw new Exception("login failed");
        }

        return gson.fromJson(
                new InputStreamReader(
                        connection.getInputStream()),
                LoginResult.class);
    }

    public void logout(String authToken)
            throws Exception {
        HttpURLConnection connection =
                (HttpURLConnection)
                       URI.create(serverUrl + "/session")
                               .toURL()
                               .openConnection();
        connection.setRequestMethod("DELETE");

        connection.setRequestProperty(
                "Authorization",
                authToken);

        if (connection.getResponseCode() != 200) {
            throw new Exception("logout failed");
        }
    }

    public CreateGameResult createGame(
            String authToken,
            String gameName)
            throws Exception {

        CreateGameRequest request =
                new CreateGameRequest(
                        authToken,
                        gameName);
        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(serverUrl + "/game")
                                .toURL()
                                .openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.setRequestProperty(
                "Authorization",
                authToken);

        try (OutputStream body =
                    connection.getOutputStream()) {
            body.write(
                    gson.toJson(request).getBytes());
        }

        if (connection.getResponseCode() != 200) {
            throw new Exception("create game failed");
        }

        return gson.fromJson(
                new InputStreamReader(
                        connection.getInputStream()),
                CreateGameResult.class);
    }

    public ListGamesResult listGames(
            String authToken)
            throws Exception {

        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(serverUrl + "/game")
                                .toURL()
                                .openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty(
                "Authorization",
                authToken);

        if (connection.getResponseCode() != 200) {
            throw new Exception("list games failed");
        }

        return gson.fromJson(
                new InputStreamReader(
                        connection.getInputStream()),
                ListGamesResult.class);
    }

    public void joinGame(
            String authToken,
            String playerColor,
            int gameID)
            throws Exception {

        JoinGameRequest request =
                new JoinGameRequest(
                        authToken,
                        playerColor,
                        gameID);

        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(serverUrl + "/game")
                                .toURL()
                                .openConnection();

        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        connection.setRequestProperty(
                "Authorization",
                authToken);

        try (OutputStream body =
                connection.getOutputStream()) {
            body.write(
                    gson.toJson(request).getBytes());
        }

        if (connection.getResponseCode() != 200) {
            throw new Exception("join game failed");
        }
    }

    public void clear() throws Exception {
        HttpURLConnection connection =
                (HttpURLConnection)
                        URI.create(serverUrl + "/db")
                                .toURL()
                                .openConnection();

        connection.setRequestMethod("DELETE");

        if (connection.getResponseCode() != 200) {
            throw new Exception("clear failed");
        }
    }

}
