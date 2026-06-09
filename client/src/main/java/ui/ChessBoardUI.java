package ui;

import chess.*;

public class ChessBoardUI {
    public static void drawBoard(
            boolean blackPerspective) {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();

        int[] rows;
        char[] cols;

        if (blackPerspective) {
            rows = new int[]{1,2,3,4,5,6,7,8};
            cols = new char[]{'h','g','f','e','d','c','b','a'};
        } else {
            rows = new int[]{8,7,6,5,4,3,2,1};
            cols = new char[]{'a','b','c','d','e','f','g','h'};
        }
        printColumnLabels(cols);
        for (int row : rows) {
            System.out.print(row + " ");

            for (char col : cols) {
                int colIndex = col - 'a' + 1;

                ChessPosition pos = new ChessPosition(row, colIndex);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSquare = (row + colIndex) % 2 != 0;
                if (isLightSquare) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                System.out.print(getPieceString(piece));
                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            }

            System.out.print(" " + row);
            System.out.println();
        }
        printColumnLabels(cols);
    }

    public static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        switch (type) {
            case KING:
                return (color == ChessGame.TeamColor.WHITE)
                        ? EscapeSequences.WHITE_KING
                        : EscapeSequences.BLACK_KING;

            case QUEEN:
                return (color == ChessGame.TeamColor.WHITE)
                        ? EscapeSequences.WHITE_QUEEN
                        : EscapeSequences.BLACK_QUEEN;

            case BISHOP:
                return (color == ChessGame.TeamColor.WHITE)
                        ? EscapeSequences.WHITE_BISHOP
                        : EscapeSequences.BLACK_BISHOP;

            case KNIGHT:
                return (color == ChessGame.TeamColor.WHITE)
                        ? EscapeSequences.WHITE_KNIGHT
                        : EscapeSequences.BLACK_KNIGHT;

            case ROOK:
                return (color == ChessGame.TeamColor.WHITE)
                        ? EscapeSequences.WHITE_ROOK
                        : EscapeSequences.BLACK_ROOK;

            case PAWN:
                return (color == ChessGame.TeamColor.WHITE)
                        ? EscapeSequences.WHITE_PAWN
                        : EscapeSequences.BLACK_PAWN;

            default:
                return EscapeSequences.EMPTY;
        }

    }

    private static void printColumnLabels(char[] cols) {
        System.out.print("   ");

        for (char c : cols) {
            System.out.print(" " + c + " ");
        }

        System.out.println();
    }
}