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
}