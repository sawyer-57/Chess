package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        //BISHOP MOVES
        if (piece.getPieceType() == PieceType.BISHOP) {

            int[][] dirs = {
                    {1, 1}, {1, -1},
                    {-1, 1}, {-1, -1}
            };

            return slideMoves(board, myPosition, piece, dirs);
        }

        //ROOK MOVES 
        else if (piece.getPieceType() == PieceType.ROOK) {

            int[][] dirs = {
                    {1, 0}, {-1, 0},
                    {0, 1}, {0, -1}
            };

            return slideMoves(board, myPosition, piece, dirs);
        }

        //QUEEN MOVES 
        else if (piece.getPieceType() == PieceType.QUEEN) {

            int[][] dirs = {
                    {1, 0}, {-1, 0},
                    {0, 1}, {0, -1},
                    {1, 1}, {1, -1},
                    {-1, 1}, {-1, -1}
            };

            return slideMoves(board, myPosition, piece, dirs);
        }

        //KING MOVES 
        else if (piece.getPieceType() == PieceType.KING) {

            int[][] dirs = {
                    {1, 0}, {1, 1}, {0, 1}, {-1, 1},
                    {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
            };

            return stepMoves(board, myPosition, piece, dirs);
        }

        //KNIGHT MOVES 
        else if (piece.getPieceType() == PieceType.KNIGHT) {

            int[][] dirs = {
                    {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                    {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
            };

            return stepMoves(board, myPosition, piece, dirs);
        }

        //PAWN MOVES 
        else if (piece.getPieceType() == PieceType.PAWN) {

            List<ChessMove> moves = new ArrayList<>();
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            int dir = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

            int forwardRow = row + dir;

            if (forwardRow >= 1 && forwardRow <= 8) {

                ChessPosition oneStep = new ChessPosition(forwardRow, col);

                if (board.getPiece(oneStep) == null) {

                    addPawnPromotionIfNeeded(moves, myPosition, oneStep, piece);

                    int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;

                    if (row == startRow) {
                        ChessPosition twoStep = new ChessPosition(row + 2 * dir, col);
                        if (board.getPiece(twoStep) == null) {
                            moves.add(new ChessMove(myPosition, twoStep, null));
                        }
                    }
                }
            }

            int[][] caps = {{dir, 1}, {dir, -1}};

            for (int[] c : caps) {
                int r = row + c[0];
                int c2 = col + c[1];

                if (r < 1 || r > 8 || c2 < 1 || c2 > 8) continue;

                ChessPosition pos = new ChessPosition(r, c2);
                ChessPiece target = board.getPiece(pos);

                if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                    addPawnPromotionIfNeeded(moves, myPosition, pos, piece);
                }
            }

            return moves;
        }
        
        return List.of();
    }

    private List<ChessMove> slideMoves(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece,
            int[][] directions) {

        List<ChessMove> moves = new ArrayList<>();

        for (int[] dir : directions) {
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPos);

                if (target == null) {
                    moves.add(new ChessMove(start, newPos, null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(start, newPos, null));
                    }
                    break;
                }

                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }

    private List<ChessMove> stepMoves(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece,
            int[][] directions) {

        List<ChessMove> moves = new ArrayList<>();

        for (int[] dir : directions) {
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            if (row < 1 || row > 8 || col < 1 || col > 8) continue;

            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPos);

            if (target == null || target.getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(start, newPos, null));
            }
        }

        return moves;
    }

    private void addPawnPromotionIfNeeded(
            List<ChessMove> moves,
            ChessPosition from,
            ChessPosition to,
            ChessPiece piece) {

        int row = to.getRow();

        if (row == 1 || row == 8) {
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ChessPiece other = (ChessPiece) obj;

        return pieceColor == other.pieceColor &&
           type == other.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(pieceColor, type);
    }
}
