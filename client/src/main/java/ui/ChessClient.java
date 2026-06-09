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
            System.out.println("Unable to register: " + e.getMessage());
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
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void postlogin(
            Scanner scanner) {
        while (authToken != null) {
            System.out.print("postlogin> ");

            String command =
                    scanner.nextLine()
                            .trim()
                            .toLowerCase();
            switch (command) {
                case "help":
                    printPostloginHelp();
                    break;

                case "logout":
                    logout();
                    break;

                case "create":
                    createGame(scanner);
                    break;

                case "list":
                    listGames();
                    break;

                case "play":
                    playGame(scanner);
                    break;

                case "observe":
                    observeGame(scanner);
                    break;

                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private void printPostloginHelp() {
        System.out.println("""
                help
                logout
                create
                list
                play
                observe
                """);
    }

    private void logout() {
        try {
            server.logout(authToken);
            authToken = null;
            System.out.println("Logged out");
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void createGame(
            Scanner scanner) {
        try {
            System.out.print("game name: ");
            String gameName = scanner.nextLine();

            server.createGame(
                    authToken,
                    gameName);

            System.out.println("Game created");
        } catch (Exception e) {
            System.out.println("Unable to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            var result = server.listGames(authToken);

            currentGames = result.games();

            for (int i = 0; i < currentGames.size(); i++) {
                GameData game = currentGames.get(i);

                System.out.printf(
                        "%d. %s White:%s Black:%s%n",
                        i + 1,
                        game.gameName(),
                        game.whiteUsername(),
                        game.blackUsername());
            }
        } catch (Exception e) {
            System.out.println("Unable to list games: " + e.getMessage());
        }
    }

    private void playGame(
            Scanner scanner) {
        try {
            System.out.print("game number: ");

            String input = scanner.nextLine();

            int number;

            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid game number. Please enter a number.");
                return;
            }

            if (number < 1 || number > currentGames.size()) {
                System.out.println("Game number does not exist.");
                return;
            }

            System.out.print("WHITE or BLACK: ");
            String color = scanner.nextLine()
                                    .toUpperCase();
            int gameID = currentGames.get(number - 1).gameID();

            server.joinGame(
                    authToken,
                    color,
                    gameID);

            ChessBoardUI.drawBoard(color.equals("BLACK"));
        } catch (Exception e) {
            System.out.println("Unable to join game: " + e.getMessage());
        }
    }

    private void observeGame(
            Scanner scanner) {
        try {
            System.out.print("game number: ");

            String input = scanner.nextLine();

            int number;

            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid game number. Please enter a number.");
                return;
            }

            if (number < 1 || number > currentGames.size()) {
                System.out.println("Game number does not exist.");
                return;
            }

            int gameID = currentGames.get(number - 1).gameID();

            System.out.println("Observing game " + gameID);

            ChessBoardUI.drawBoard(false);
        } catch (Exception e) {
            System.out.println("Unable to observe game: " + e.getMessage());
        }
    }
}