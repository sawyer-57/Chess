package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board; 
    private TeamColor teamTurn; 

    public ChessGame() {
        board = new ChessBoard(); 
        board.resetBoard(); 
        teamTurn = TeamColor.WHITE; 
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition); 
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition); 

        Collection<ChessMove> legalMoves = new ArrayList<> (); 
        for (ChessMove move : possibleMoves) {
            ChessPiece originalPiece = board.getPiece(startPosition);
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

            board.addPiece(move.getEndPosition(), originalPiece);
            board.addPiece(startPosition, null);

            if (!isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }

            board.addPiece(startPosition, originalPiece);
            board.addPiece(move.getEndPosition(), capturedPiece);
        }
        return legalMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition()); 
        //check if move is valid
        if (piece == null) {
            throw new InvalidMoveException(); 
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException(); 
        }
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition()); 
        //check if move is legal 
        if (!legalMoves.contains(move)) {
            throw new InvalidMoveException(); 
        }

        board.addPiece(move.getEndPosition(), piece); 
        board.addPiece(move.getStartPosition(), null);
        //check for promotion
        if (move.getPromotionPiece() != null) {
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()); 
            board.addPiece(move.getEndPosition(), promotedPiece);
        }
        //switch turns 
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }

        for (ChessPosition pos : allPositions()) {
            ChessPiece piece = board.getPiece(pos);

            if (piece == null || piece.getTeamColor() == teamColor) {
                continue;
            }

            Collection<ChessMove> moves = piece.pieceMoves(board, pos);

            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyValidMove(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null
                        && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    private Collection<ChessPosition> allPositions() {
        Collection<ChessPosition> positions = new ArrayList<>();

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                positions.add(new ChessPosition(r, c));
            }
        }
        return positions;
    }

    private boolean hasAnyValidMove(TeamColor teamColor) {
        for (ChessPosition pos : allPositions()) {
            ChessPiece piece = board.getPiece(pos);

            if (piece == null || piece.getTeamColor() != teamColor) {
                continue;
            }

            if (!validMoves(pos).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; 
        }
        ChessGame other = (ChessGame) obj; 

        return java.util.Objects.equals(board, other.board) && teamTurn == other.teamTurn; 
    }

    @Override 
    public int hashCode() {
        return java.util.Objects.hash(board, teamTurn); 
    }
}
