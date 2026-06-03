package ui;

import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade server;

    private String authToken;
    private String username;

    private List<GameData> currentGames =
            new ArrayList<>();

    public ChessClient(ServerFacade server) {
        this.server = server;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("prelogin> ");

            String input =
                    scanner.nextLine()
                            .trim()
                            .toLowerCase();

            switch (input) {

                case "help":
                    printPreloginHelp();
                    break;

                case "register":
                    register(scanner);
                    break;

                case "login":
                    login(scanner);
                    break;

                case "quit":
                    return;

                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private void printPreloginHelp() {
        System.out.println("""
                help
                register
                login
                quit
                """);
    }

    private void register(
            Scanner scanner) {
        try {
            System.out.print("username: ");
            String username = scanner.nextLine();

            System.out.print("password: ");
            String password = scanner.nextLine();

            System.out.print("email: ");
            String email = scanner.nextLine();

            var result = server.register(
                                username,
                                password,
                                email);
            this.username = result.username();
            this.authToken = result.authToken();

            System.out.println("Registered successfully");

            postlogin(scanner);
        } catch (Exception e) {
            System.out.println("Unable to register");
        }
    }

    private void login(
            Scanner scanner) {
        try {
            System.out.print("username: ");
            String username = scanner.nextLine();

            System.out.print("password: ");
            String password = scanner.nextLine();

            var result = server.login(
                                username,
                                password);
            this.username = result.username();
            this.authToken = result.authToken();

            System.out.println("Logged in successfully");

            postlogin(scanner);
        } catch (Exception e) {
            System.out.println("Login failed");
        }
    }


}