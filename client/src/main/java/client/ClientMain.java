package client;


import chess.*;
import ui.ChessClient;


public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);


        ServerFacade server =
                new ServerFacade(8080);
        ChessClient client =
                new ChessClient(server);
        client.run();
    }
}
