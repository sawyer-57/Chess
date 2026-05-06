package chess;

import java.util.Collection;
import java.util.List;

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
            //up right diagonal 
            int row = myPosition.getRow()+1;
            int col = myPosition.getColumn()+1;
            Collection<ChessMove> moves = new Collection<ChessMove> (); 
            while (row < 8 && col < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
                col++;
            }
            //up left diagonal 
            row = myPosition.getRow()+1;
            col = myPosition.getColumn()-1;
            while (row < 8 && col >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
                col--;
            }
            //down right diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()+1;
            while (row >= 0 && col < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
                col++;
            }
            //down left diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()-1;
            while (row >= 0 && col >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
                col--;
            }
            return moves;
        }

        //ROOK MOVES 
        else if (piece.getPieceType() == PieceType.ROOK) {
            //up 
            int row = myPosition.getRow()+1; 
            int col = myPosition.getColumn();
            Collection<ChessMove> moves = new Collection<ChessMove> ();
            while (row < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
            }
            //down
            row = myPosition.getRow()-1;
            while (row >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
            }
            //right
            row = myPosition.getRow();
            col = myPosition.getColumn()+1;
            while (col < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                col++;
            }
            //left
            row = myPosition.getRow();
            col = myPosition.getColumn()-1; 
            while (col >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                col--;
            }
            return moves;            
        }

        //QUEEN MOVES 
        else if (piece.getPieceType() == PieceType.QUEEN) {
            //up
            int row = myPosition.getRow()+1;
            int col = myPosition.getColumn();
            Collection<ChessMove> moves = new Collection<ChessMove> ();
            while (row < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
            }
            //down
            row = myPosition.getRow()-1;
            col = myPosition.getColumn();
            while (row >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
            }
            //right
            row = myPosition.getRow();
            col = myPosition.getColumn()+1;
            while (col < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                col++;
            }
            //left
            row = myPosition.getRow();
            col = myPosition.getColumn()-1; 
            while (col >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                col--;
            }
            //up right diagonal
            row = myPosition.getRow()+1;
            col = myPosition.getColumn()+1;
            while (row < 8 && col < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
                col++;
            }
            //up left diagonal
            row = myPosition.getRow()+1;
            col = myPosition.getColumn()-1;
            while (row < 8 && col >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
                col--;
            }
            //down right diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()+1;
            while (row >= 0 && col < 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
                col++;
            }
            //down left diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()-1;
            while (row >= 0 && col >= 0) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) != null) break; 
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
                col--;
            }
            return moves;                
        }

        


        
    }
        return List.of();
    }
}
