package client;

import com.google.gson.Gson;
import service.requests.*;
import service.results.*;
import model.*;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

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
}
