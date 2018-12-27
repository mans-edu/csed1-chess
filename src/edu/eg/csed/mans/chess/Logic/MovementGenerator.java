package edu.eg.csed.mans.chess.Logic;

import edu.eg.csed.mans.chess.ChessPiece;
import edu.eg.csed.mans.chess.MoveImpl;
import edu.eg.csed.mans.chess.MoveType;
import edu.eg.csed.mans.chess.PieceType;

import java.util.Vector;

public class MovementGenerator {
    public static int[][] numberOfMovesMainDiagonalUp = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1},
            {0, 1, 2, 2, 2, 2, 2, 2},
            {0, 1, 2, 3, 3, 3, 3, 3},
            {0, 1, 2, 3, 4, 4, 4, 4},
            {0, 1, 2, 3, 4, 5, 5, 5},
            {0, 1, 2, 3, 4, 5, 6, 6},
            {0, 1, 2, 3, 4, 5, 6, 7},

    };

    public static int[][] numberOfMovesMainDiagonalDown = {
            {7, 6, 5, 4, 3, 2, 1, 0},
            {6, 6, 5, 4, 3, 2, 1, 0},
            {5, 5, 5, 4, 3, 2, 1, 0},
            {4, 4, 4, 4, 3, 2, 1, 0},
            {3, 3, 3, 3, 3, 2, 1, 0},
            {2, 2, 2, 2, 2, 2, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    public static int[][] numberOfMovesAntiDiagonalUp = {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 6},
            {0, 1, 2, 3, 4, 5, 5, 5},
            {0, 1, 2, 3, 4, 4, 4, 4},
            {0, 1, 2, 3, 3, 3, 3, 3},
            {0, 1, 2, 2, 2, 2, 2, 2},
            {0, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    public static int[][] numberOfMovesAntiDiagonalDown = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 0},
            {2, 2, 2, 2, 2, 2, 1, 0},
            {3, 3, 3, 3, 3, 2, 1, 0},
            {4, 4, 4, 4, 3, 2, 1, 0},
            {5, 5, 5, 4, 3, 2, 1, 0},
            {6, 6, 5, 4, 3, 2, 1, 0},
            {7, 6, 5, 4, 3, 2, 1, 0},
    };

    private void positionOverrideMovement(ChessBoard chessBoard, Position position, int[] deltaX, int[] deltaY, int playerID, Vector<MoveImpl> result) {
        int newX, newY;
        for(int i = 0; i < 8; ++i) {
            newX = position.x + deltaX[i];
            newY = position.y + deltaY[i];

            if(newX < 8 && newX >= 0 && newY < 8 && newY >= 0) {
                var targetPiece = chessBoard.getPiece(newX, newY);
                if(targetPiece == null || targetPiece.getPlayerID() != playerID) {
                    MoveImpl newMove = new MoveImpl();
                    newMove.fromPos = position;
                    newMove.toPos = new Position(newX, newY);
                    if(targetPiece != null) {
                        newMove.type = MoveType.Capture;
                    } else {
                        newMove.type = MoveType.Move;
                    }

                    result.add(new MoveImpl(newMove));
                }
            }
        }
    }

    private void generatePawnMoves(ChessBoard chessBoard, int playerID, Vector<MoveImpl> result) {
        Vector<Position> pawns = new Vector<>();
        var internalBoard = chessBoard.getInternalBoard();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                var piece = internalBoard[y * 8 + x];
                if(piece != null && (piece.getType() == PieceType.Pawn) && piece.getPlayerID() == playerID) {
                    pawns.add(new Position(x, y));
                }
            }
        }

        int colorModifier = 1;
        if(playerID == ChessPiece.PLAYER_WHITE) { // white //
            colorModifier = -1;
        }

        for (var pawnPos : pawns) {
            MoveImpl newMove = new MoveImpl();
            newMove.fromPos = pawnPos;

            // forward push (1 tile)
            if(chessBoard.getPiece(pawnPos.x, pawnPos.y + colorModifier) == null) {
                newMove.toPos = new Position(pawnPos.x, pawnPos.y + colorModifier);
                newMove.type  = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            // capture push (1 tile diagonally)
            for (int i = -1; i < 2; i++) {
                int newX = pawnPos.x + i;
                if (i != 0 && newX >= 0 && newX < 8) {
                    var piece = chessBoard.getPiece(newX, pawnPos.y + colorModifier);
                    if (piece != null && piece.getPlayerID() != playerID) {
                        newMove.toPos = new Position(newX, pawnPos.y + colorModifier);
                        newMove.type  = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                }
            }

            // forward push (2 tiles)
            if(!chessBoard.getPiece(pawnPos).hasMoved()
                    && chessBoard.getPiece(pawnPos.x, pawnPos.y + colorModifier) == null
                    && chessBoard.getPiece(pawnPos.x, pawnPos.y + 2 * colorModifier) == null) {
                newMove.toPos = new Position(pawnPos.x, pawnPos.y + 2 * colorModifier);
                newMove.type  = MoveType.PawnRush;
                result.add(new MoveImpl(newMove));
            }

            //EnPassant capture
            if(chessBoard.lastPawnRush.y == pawnPos.y && Math.abs(chessBoard.lastPawnRush.x - pawnPos.x) == 1)
            {
                newMove.toPos = new Position(chessBoard.lastPawnRush.x, chessBoard.lastPawnRush.y + colorModifier);
                newMove.type  = MoveType.EnPassant;
                result.add(new MoveImpl(newMove));
            }
        }
    }

    public void generateKingMoves(ChessBoard chessBoard, int playerID, Vector<MoveImpl> result) {
        Position kingPos = chessBoard.kingPos[playerID];

        /*
        (-1, -1)(0, -1)(1, -1)
        (-1, 0) (King) (1, 0)
        (-1, 1) (0, 1) (1, 1)*/
        int kingDeltaX[] = {0,  0, 1, 1,  1, -1, -1, -1};
        int kingDeltaY[] = {1, -1, 0, 1, -1, -1,  0,  1};
        positionOverrideMovement(chessBoard, kingPos, kingDeltaX, kingDeltaY, playerID, result);

        // castling
        var kingPiece = chessBoard.getPiece(kingPos);
        if(!kingPiece.hasMoved())
        {
            var kingSideRook = chessBoard.getPiece(7, kingPos.y);
            var queenSideRook = chessBoard.getPiece(0, kingPos.y);
            if(kingSideRook != null && !kingSideRook.hasMoved() && chessBoard.getPiece(kingPos.x+1, kingPos.y) == null)
            {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = kingPos;
                newMove.toPos = new Position(kingPos.x + 2, kingPos.y);
                newMove.type = MoveType.KingSideCastling;
                result.add(new MoveImpl(newMove));
            }

            if(queenSideRook != null && !queenSideRook.hasMoved() && chessBoard.getPiece(kingPos.x-1, kingPos.y) == null)
            {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = kingPos;
                newMove.toPos = new Position(kingPos.x - 2, kingPos.y);
                newMove.type = MoveType.QueenSideCastling;
                result.add(new MoveImpl(newMove));
            }
        }
    }

    private void generateKnightMoves(ChessBoard chessBoard, int playerID, Vector<MoveImpl> result) {
        int knightDeltaX[] = {-2, -2, -1, -1, 1,  1,  2, 2};
        int knightDeltaY[] = {-1,  1, -2,  2, 2, -2, -1, 1};

        Vector<Position> knights = new Vector<>();
        for(int x = 0; x < 8; ++x) {
            for(int y = 0; y < 8; ++y) {
                var piece = chessBoard.getPiece(x,y);
                if(piece != null && piece.getType() == PieceType.Knight && piece.getPlayerID() == playerID) {
                    knights.add(new Position(x, y));
                }
            }
        }

        for (var knightPos : knights) {
            positionOverrideMovement(chessBoard, knightPos, knightDeltaX, knightDeltaY, playerID, result);
        }
    }

    private void generateBishopSliderMoves(ChessBoard chessBoard, int playerID, Vector<MoveImpl> result) {
        Vector<Position> bishops = new Vector<>();
        for(int x = 0; x < 8; ++x) {
            for(int y = 0; y < 8; ++y) {
                var piece = chessBoard.getPiece(x,y);
                if(piece != null && (piece.getType() == PieceType.Bishop || piece.getType() == PieceType.Queen) && piece.getPlayerID() == playerID) {
                    bishops.add(new Position(x, y));
                }
            }
        }

        for(var bishopPos : bishops) {
            int moveNum = numberOfMovesMainDiagonalUp[bishopPos.x][bishopPos.y];
            for(int i = 1; i <= moveNum; ++i) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = bishopPos;
                newMove.toPos.x = bishopPos.x-i;
                newMove.toPos.y = bishopPos.y-i;
                var piece = chessBoard.getPiece(bishopPos.x-i, bishopPos.y-i);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }

                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            moveNum = numberOfMovesMainDiagonalDown[bishopPos.x][bishopPos.y];
            for(int i = 1; i <= moveNum; ++i) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = bishopPos;
                newMove.toPos.x = bishopPos.x + i;
                newMove.toPos.y = bishopPos.y + i;
                var piece = chessBoard.getPiece(bishopPos.x + i, bishopPos.y + i);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }
                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            moveNum = numberOfMovesAntiDiagonalUp[bishopPos.x][bishopPos.y];
            for(int i = 1; i <= moveNum; ++i) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = bishopPos;
                newMove.toPos.x = bishopPos.x + i;
                newMove.toPos.y = bishopPos.y - i;
            	var piece = chessBoard.getPiece(bishopPos.x+i, bishopPos.y-i);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }
                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            moveNum = numberOfMovesAntiDiagonalDown[bishopPos.x][bishopPos.y];
            for(int i = 1; i <= moveNum; ++i) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = bishopPos;
                newMove.toPos.x = bishopPos.x-i;
                newMove.toPos.y = bishopPos.y+i;
            	var piece = chessBoard.getPiece(bishopPos.x-i, bishopPos.y+i);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }
                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }
        }
    }

    private void generateRookSliderMoves(ChessBoard chessBoard, int playerID, Vector<MoveImpl> result) {
        Vector<Position> rooks = new Vector<>();
        for(int x = 0; x < 8; ++x) {
            for(int y = 0; y < 8; ++y) {
                var piece = chessBoard.getPiece(x, y);
                if(piece != null && (piece.getType() == PieceType.Rook || piece.getType() == PieceType.Queen) && piece.getPlayerID() == playerID) {
                    rooks.add(new Position(x, y));
                }
            }
        }

        for(var rookPos : rooks) {
            for(int y = rookPos.y - 1; y >= 0; --y) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = rookPos;
                newMove.toPos.x = rookPos.x;
                newMove.toPos.y = y;
                var piece = chessBoard.getPiece(rookPos.x, y);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }

                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            for (int y = rookPos.y + 1; y < 8; ++y) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = rookPos;
                newMove.toPos.x = rookPos.x;
                newMove.toPos.y = y;
                var piece = chessBoard.getPiece(rookPos.x, y);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }
                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            for(int x = rookPos.x - 1; x >= 0; --x) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = rookPos;
                newMove.toPos.x = x;
                newMove.toPos.y = rookPos.y;
                var piece = chessBoard.getPiece(x, rookPos.y);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID) {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }
                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }

            for(int x = rookPos.x + 1; x < 8; ++x) {
                MoveImpl newMove = new MoveImpl();
                newMove.fromPos = rookPos;
                newMove.toPos.x = x;
                newMove.toPos.y = rookPos.y;
                var piece = chessBoard.getPiece(x, rookPos.y);
                if(piece != null) {
                    if(piece.getPlayerID() != playerID)
                    {
                        newMove.type = MoveType.Capture;
                        result.add(new MoveImpl(newMove));
                    }
                    break;
                }

                newMove.type = MoveType.Move;
                result.add(new MoveImpl(newMove));
            }
        }
    }

    public Vector<MoveImpl> generateMoves(ChessBoard chessBoard, int playerID) {
        Vector<MoveImpl> result = new Vector<>();
        generatePawnMoves(chessBoard, playerID, result);
        generateKingMoves(chessBoard, playerID, result);
        generateKnightMoves(chessBoard, playerID, result);
        generateBishopSliderMoves(chessBoard, playerID, result);
        generateRookSliderMoves(chessBoard, playerID, result);
        return result;
    }
}
