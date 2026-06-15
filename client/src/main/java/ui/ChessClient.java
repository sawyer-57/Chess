package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.*;

import websocket.WebSocketClient;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;

public class ChessClient {

    private final ServerFacade server;
    private final WebSocketClient wsClient = new WebSocketClient();

    private String authToken;
    private String username;
    private Integer currentGameID;
    private String playerColor;
    private ChessGame currentGame;

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

            wsClient.connectIfNeeded(
                    "ws://localhost:8000/ws",
                    this);

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

            wsClient.setUI(this);

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

            wsClient.close();

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

            currentGameID = gameID;

            try {
                server.joinGame(authToken, color, gameID);
            } catch (Exception e) {
                System.out.println("Unable to join game: " + e.getMessage());
                return;
            }

            currentGameID = gameID;
            playerColor = color;

            wsClient.connect("ws://localhost:8000/ws");
            wsClient.setSession(authToken, gameID);
            wsClient.sendConnect();

            gameLoop(scanner);
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

            currentGameID = gameID;
            playerColor = "OBSERVER";

            wsClient.connect("ws://localhost:8000/ws");
            wsClient.setSession(authToken, gameID);
            wsClient.sendConnect();

            playerColor = "OBSERVER";
            gameLoop(scanner);

            System.out.println("Observing game " + gameID);

            System.out.println("Waiting for server to send board update...");
        } catch (Exception e) {
            System.out.println("Unable to observe game: " + e.getMessage());
        }
    }

    private void makeMove(Scanner scanner) {
        try {
            if (currentGameID == null) {
                System.out.println("You are not in a game");
                return;
            }

            int sr, sc, er, ec;

            try {
                System.out.print("start row: ");
                sr = Integer.parseInt(scanner.nextLine());

                System.out.print("start col: ");
                sc = Integer.parseInt(scanner.nextLine());

                System.out.print("end row: ");
                er = Integer.parseInt(scanner.nextLine());

                System.out.print("end col: ");
                ec = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {
                System.out.println("Invalid input: please enter numbers only (1–8).");
                return;
            }

            ChessPiece.PieceType newPiece = null;

            if (er == 8 || er == 1) {
                ChessPosition position = new ChessPosition(sr, sc);
                ChessPiece piece = currentGame.getBoard().getPiece(position);
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    System.out.print("promotion piece: ");
                    String prom = scanner.nextLine();
                    switch(prom.toUpperCase()) {
                        case "QUEEN":
                            newPiece = ChessPiece.PieceType.QUEEN;
                            break;

                        case "ROOK":
                            newPiece = ChessPiece.PieceType.ROOK;
                            break;

                        case "KNIGHT":
                            newPiece = ChessPiece.PieceType.KNIGHT;
                            break;

                        case "BISHOP":
                            newPiece = ChessPiece.PieceType.BISHOP;
                            break;

                        default:
                            System.out.print("Invalid Promotion Piece");

                    }

                }
            }

            ChessMove move = new ChessMove(
                    new ChessPosition(sr, sc),
                    new ChessPosition(er, ec),
                    newPiece
            );

            MakeMoveCommand cmd = new MakeMoveCommand(
                    authToken,
                    currentGameID,
                    move
            );

            wsClient.send(cmd);

        } catch (Exception e) {
            System.out.println("Move failed: " + e.getMessage());
        }
    }

    private void resign() {
        if (currentGameID == null) {
            return;
        }

        System.out.print("Are you sure you want to resign? (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim().toLowerCase();

        if (!input.equals("yes")) {
            System.out.println("Resign cancelled");
            return;
        }

        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                currentGameID
        );

        wsClient.send(cmd);
    }

    private void leaveGame() {
        if (currentGameID == null) {
            return;
        }

        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                currentGameID
        );

        wsClient.send(cmd);

        currentGameID = null;
        playerColor = null;
        currentGame = null;

        System.out.println("Left game");
    }

    private void highlightMoves(Scanner scanner) {

        try {
            System.out.print("row: ");
            int row = Integer.parseInt(scanner.nextLine());

            System.out.print("col: ");
            int col = Integer.parseInt(scanner.nextLine());

            ChessPosition pos = new ChessPosition(row, col);

            ChessPiece piece = currentGame.getBoard().getPiece(pos);

            if (piece == null) {
                System.out.println("No piece there");
                return;
            }

            var moves = currentGame.validMoves(pos);

            Set<ChessPosition> highlights = new HashSet<>();

            for (ChessMove move : moves) {
                highlights.add(move.getEndPosition());
            }

            ChessBoardUI.drawBoardWithHighlights(
                    currentGame,
                    playerColor.equals("BLACK"),
                    highlights
            );

        } catch (Exception e) {
            System.out.println("Highlight failed: " + e.getMessage());
        }
    }

    private void gameLoop(Scanner scanner) {
        while (currentGameID != null) {

            System.out.print("game> ");

            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {

                case "help":
                    printGameHelp();
                    break;

                case "redraw":
                    ChessBoardUI.drawBoard(currentGame, playerColor.equals("BLACK"));
                    break;

                case "move":
                    makeMove(scanner);
                    break;

                case "resign":
                    resign();
                    break;

                case "leave":
                    leaveGame();
                    return;

                case "highlight":
                    highlightMoves(scanner);
                    break;

                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private void printGameHelp() {
        System.out.println("""
            help
            redraw
            move
            resign
            leave
            highlight
            """);
    }

    public void updateGame(ChessGame game) {
        if (game == null) {
            System.out.println("[WARN] Received null game from server");
            return;
        }

        this.currentGame = game;

        boolean blackView =
                playerColor != null && playerColor.equals("BLACK");

        ChessBoardUI.drawBoard(currentGame, blackView);
    }

}