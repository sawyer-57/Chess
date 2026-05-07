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
            //up right diagonal 
            int row = myPosition.getRow()+1;
            int col = myPosition.getColumn()+1;
            List<ChessMove> moves = new ArrayList<> (); 
            while (row <= 8 && col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row++;
                col++;
            }
            //up left diagonal 
            row = myPosition.getRow()+1;
            col = myPosition.getColumn()-1;
            while (row <= 8 && col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row++;
                col--;
            }
            //down right diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()+1;
            while (row >= 1 && col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row--;
                col++;
            }
            //down left diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()-1;
            while (row >= 1 && col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }                     
                    break; 
                }
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
            List<ChessMove> moves = new ArrayList<> ();
            while (row <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row++;
            }
            //down
            row = myPosition.getRow()-1;
            while (row >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row--;
            }
            //right
            row = myPosition.getRow();
            col = myPosition.getColumn()+1;
            while (col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                col++;
            }
            //left
            row = myPosition.getRow();
            col = myPosition.getColumn()-1; 
            while (col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                col--;
            }
            return moves;            
        }

        //QUEEN MOVES 
        else if (piece.getPieceType() == PieceType.QUEEN) {
            //up
            int row = myPosition.getRow()+1;
            int col = myPosition.getColumn();
            List<ChessMove> moves = new ArrayList<> ();
            while (row <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row++;
            }
            //down
            row = myPosition.getRow()-1;
            col = myPosition.getColumn();
            while (row >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row--;
            }
            //right
            row = myPosition.getRow();
            col = myPosition.getColumn()+1;
            while (col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                col++;
            }
            //left
            row = myPosition.getRow();
            col = myPosition.getColumn()-1; 
            while (col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                col--;
            }
            //up right diagonal
            row = myPosition.getRow()+1;
            col = myPosition.getColumn()+1;
            while (row <= 8 && col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row++;
                col++;
            }
            //up left diagonal
            row = myPosition.getRow()+1;
            col = myPosition.getColumn()-1;
            while (row <= 8 && col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row++;
                col--;
            }
            //down right diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()+1;
            while (row >= 1 && col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row--;
                col++;
            }
            //down left diagonal
            row = myPosition.getRow()-1;
            col = myPosition.getColumn()-1;
            while (row >= 1 && col >= 1) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    }
                    break; 
                }
                row--;
                col--;
            }
            return moves;                
        }

        //KING MOVES 
        else if (piece.getPieceType() == PieceType.KING) {
            int row = myPosition.getRow(); 
            int col = myPosition.getColumn();
            List<ChessMove> moves = new ArrayList<> ();

            int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece target = board.getPiece(newPosition);
                    if (target == null || target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }   
            return moves; 
        }

        //KNIGHT MOVES 
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            int row = myPosition.getRow(); 
            int col = myPosition.getColumn(); 
            List<ChessMove> moves = new ArrayList<> ();
            int[][] directions = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol); 
                    ChessPiece target = board.getPiece(newPosition);
                    if (target == null || target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            return moves;
        }

        //PAWN MOVES 
        else if (piece.getPieceType() == PieceType.PAWN) {
            int row = myPosition.getRow(); 
            int col = myPosition.getColumn();
            List<ChessMove> moves = new ArrayList<> ();

            int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            // Move forward
            int forwardRow = row + direction;
            if (forwardRow >= 1 && forwardRow <= 8) {
                ChessPosition newPosition = new ChessPosition(forwardRow, col);
                if (board.getPiece(newPosition) == null) {
                    if (forwardRow == 8 || forwardRow == 1) {
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // Double move from starting position
                    int startingRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
                    if (row == startingRow) {
                        int twoRow = row + 2 * direction;
                        ChessPosition twoForward = new ChessPosition(twoRow, col);

                        if (board.getPiece(twoForward) == null) {
                            moves.add(new ChessMove(myPosition, twoForward, null));
                        }
                    }
                }
            }
            // Captures
            int[][] capturedirs = {{direction, 1}, {direction, -1}};
            for (int[] dir : capturedirs) {
                int captureRow = row + dir[0];
                int captureCol = col + dir[1];
                if (captureRow >= 1 && captureRow <= 8 && captureCol >= 1 && captureCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(captureRow, captureCol);
                    ChessPiece target = board.getPiece(newPosition);
                    if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                        if (captureRow == 8 || captureRow == 1) {
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        } else {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }
            return moves;
        }
        
        return List.of();
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
