package edu.eg.csed.mans.chess.Logic;

import edu.eg.csed.mans.Main;
import edu.eg.csed.mans.chess.*;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class ChessBoard { // singleton
    private int m_PlayerToMove = ChessPiece.PLAYER_WHITE;
    private int m_AIPlayerID = ChessPiece.PLAYER_NONE;

    // Backup states
    private ChessPiece[] m_PreviousInternalBoard;
    public Position m_PreviousLastPawnRush = new Position(0, 0);
    public Position[] m_PreviousKingPos = { new Position(-1, -1), new Position(-1, -1)};
    private BoardStatus m_PreviousBoardStatus = BoardStatus.InProgress;

    // Current states
    private ChessPiece[] m_CurrentInternalBoard;
    public Position lastPawnRush = new Position(0, 0);
    public Position[] kingPos = { new Position(-1, -1), new Position(-1, -1)};
    private BoardStatus m_BoardStatus = BoardStatus.InProgress;

    public ChessBoard() {
        // initializing a 1D array with the size of the array
        m_CurrentInternalBoard = new ChessPiece[8 * 8];
        m_PreviousInternalBoard = new ChessPiece[8 * 8];
        InitializeBoard();
    }

    public ChessBoard(ChessBoard other) {
        m_CurrentInternalBoard = new ChessPiece[8 * 8];

        for (int i = 0; i < 64; i++) {
            m_CurrentInternalBoard[i] = other.m_CurrentInternalBoard[i];
        }
    }

    public ChessPiece[] getInternalBoard() {
        return m_CurrentInternalBoard;
    }

    public ChessPiece getPiece(int x, int y) {
        var index = y * 8 + x;
        assert(index >= 0 && index < 64);
        return m_CurrentInternalBoard[index];
    }

    public ChessPiece getPiece(Position pos) {
        return getPiece(pos.x, pos.y);
    }
    
    private void InitializeBoard() {
        // Add Pawns
        for (int x = 0; x < 8; x++) {
            m_CurrentInternalBoard[1 * 8 + x] = PieceFactory.CreatePiece(PieceType.Pawn, ChessPiece.PLAYER_BLACK);
            m_CurrentInternalBoard[6 * 8 + x] = PieceFactory.CreatePiece(PieceType.Pawn, ChessPiece.PLAYER_WHITE);
        }

        // Adding Rooks
        m_CurrentInternalBoard[0 * 8 + 0] = PieceFactory.CreatePiece(PieceType.Rook, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[0 * 8 + 7] = PieceFactory.CreatePiece(PieceType.Rook, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[7 * 8 + 0] = PieceFactory.CreatePiece(PieceType.Rook, ChessPiece.PLAYER_WHITE);
        m_CurrentInternalBoard[7 * 8 + 7] = PieceFactory.CreatePiece(PieceType.Rook, ChessPiece.PLAYER_WHITE);

        // Add Knights
        m_CurrentInternalBoard[0 * 8 + 1] = PieceFactory.CreatePiece(PieceType.Knight, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[0 * 8 + 6] = PieceFactory.CreatePiece(PieceType.Knight, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[7 * 8 + 1] = PieceFactory.CreatePiece(PieceType.Knight, ChessPiece.PLAYER_WHITE);
        m_CurrentInternalBoard[7 * 8 + 6] = PieceFactory.CreatePiece(PieceType.Knight, ChessPiece.PLAYER_WHITE);

        // Add Bishops
        m_CurrentInternalBoard[0 * 8 + 2] = PieceFactory.CreatePiece(PieceType.Bishop, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[0 * 8 + 5] = PieceFactory.CreatePiece(PieceType.Bishop, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[7 * 8 + 2] = PieceFactory.CreatePiece(PieceType.Bishop, ChessPiece.PLAYER_WHITE);
        m_CurrentInternalBoard[7 * 8 + 5] = PieceFactory.CreatePiece(PieceType.Bishop, ChessPiece.PLAYER_WHITE);

        // Add Queens
        m_CurrentInternalBoard[0 * 8 + 3] = PieceFactory.CreatePiece(PieceType.Queen, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[7 * 8 + 3] = PieceFactory.CreatePiece(PieceType.Queen, ChessPiece.PLAYER_WHITE);

        // Add Kings
        m_CurrentInternalBoard[0 * 8 + 4] = PieceFactory.CreatePiece(PieceType.King, ChessPiece.PLAYER_BLACK);
        m_CurrentInternalBoard[7 * 8 + 4] = PieceFactory.CreatePiece(PieceType.King, ChessPiece.PLAYER_WHITE);

        kingPos[ChessPiece.PLAYER_WHITE] = new Position(4, 7);
        kingPos[ChessPiece.PLAYER_BLACK] = new Position(4, 0);
    }

    @Override
    public ChessBoard clone() {
        return new ChessBoard(this);
    }

    private void copyState(ChessPiece[] source, ChessPiece[] dest) {
        for (int i = 0; i < 64; i++) {
            dest[i] = source[i];
        }
    }

    private void backupState() {
        copyState(m_CurrentInternalBoard, m_PreviousInternalBoard);
        //m_previousbitState = m_bitState;
        m_PreviousLastPawnRush = lastPawnRush;
        m_PreviousKingPos[0] = kingPos[0];
        m_PreviousKingPos[1] = kingPos[1];
        m_PreviousBoardStatus = m_BoardStatus;
    }

    public void restoreState() {
        copyState(m_PreviousInternalBoard, m_CurrentInternalBoard);
        //m_BitState = m_PreviousbitState;
        lastPawnRush = m_PreviousLastPawnRush;
        kingPos[0] = m_PreviousKingPos[0];
        kingPos[1] = m_PreviousKingPos[1];
        m_BoardStatus = m_PreviousBoardStatus;
    }

    public Boolean makeMove(Position sourcePos, Position destPos, Boolean checkMate) {
        // if the game still running?
        if (m_BoardStatus != BoardStatus.InProgress) {
            return false;
        }

        var sourcePiece = getPiece(sourcePos);
        var destinationPiece = getPiece(sourcePos);

        AtomicReference<MoveType> moveType = new AtomicReference<MoveType>(MoveType.Move);
        if(sourcePiece.getPlayerID() == m_PlayerToMove && isMoveLegal(sourcePos, destPos, moveType)) {
            backupState();
            sourcePiece.setHasMoved(true);

            if (moveType.get() == MoveType.Move || moveType.get() == MoveType.Capture) {
                //sourceBitBoard.setBitFalse(sourcePos.x, sourcePos.y);
                if(sourcePiece.getType() == PieceType.Pawn && (destPos.y == 0 || destPos.y == 7)) {
                    //m_bitState.getBitBoard(m_playerToMove, PieceType::Queen).setBitTrue(destPos.x, destPos.y);
                    //sourcePiece.type = PieceType::Queen;
                } else {
                    //sourceBitBoard.setBitTrue(destPos.x, destPos.y);
                }

                m_CurrentInternalBoard[sourcePos.getVectorIndex()] = null;
                m_CurrentInternalBoard[destPos.getVectorIndex()] = sourcePiece;
                lastPawnRush = new Position(-1, -1);
            } else if (moveType.get() == MoveType.PawnRush) {
                //sourceBitBoard.setBitFalse(sourcePos.x, sourcePos.y);
                //sourceBitBoard.setBitTrue(destPos.x, destPos.y);
                m_CurrentInternalBoard[sourcePos.getVectorIndex()] = null;
                m_CurrentInternalBoard[destPos.getVectorIndex()] = sourcePiece;
                lastPawnRush = destPos;
            } else if (moveType.get() == MoveType.EnPassant) {
                //sourceBitBoard.setBitFalse(sourcePos.x, sourcePos.y);
                //sourceBitBoard.setBitTrue(destPos.x, destPos.y);
                m_CurrentInternalBoard[sourcePos.getVectorIndex()] = null;
                m_CurrentInternalBoard[destPos.getVectorIndex()] = sourcePiece;

                if (destinationPiece.getPlayerID() == ChessPiece.PLAYER_WHITE) {
                    //m_bitState.getBitBoard(getPiece(destPos.x, destPos.y + 1)).setBitFalse(destPos.x, destPos.y + 1);
                    m_CurrentInternalBoard[(destPos.y + 1) * 8 + destPos.x] = null;
                } else {
                    //m_bitState.getBitBoard(getPiece(destPos.x, destPos.y - 1)).setBitFalse(destination.x, destination.y - 1);
                    m_CurrentInternalBoard[(destPos.y - 1) * 8 + destPos.x] = null;
                }
                lastPawnRush = new Position(-1, -1);
            } else if (moveType.get() == MoveType.KingSideCastling) {
                //sourceBitBoard.setBitFalse(sourcePos.x, sourcePos.y);
                //sourceBitBoard.setBitTrue(destPos.x, destPos.y);
                //var rookBitBoard = m_bitState.getBitBoard(sourcePiece.getPlayerID(), PieceType.Rook);
                //rookBitBoard.setBitFalse(7, source.y);
                //rookBitBoard.setBitTrue(5, destination.y);

                // king
                m_CurrentInternalBoard[sourcePos.getVectorIndex()] = null;
                m_CurrentInternalBoard[destPos.getVectorIndex()] = sourcePiece;

                // rook
                m_CurrentInternalBoard[sourcePos.y * 8 + 7] = m_CurrentInternalBoard[sourcePos.y * 8 + 5];
                m_CurrentInternalBoard[sourcePos.y * 8 + 5] = null;
                lastPawnRush = new Position(-1, -1);
            } else if (moveType.get() == MoveType.QueenSideCastling) {
                //sourceBitBoard.setBitFalse(sourcePos.x, sourcePos.y);
                //sourceBitBoard.setBitTrue(destPos.x, destPos.y);
                //var rookBitBoard = m_bitState.getBitBoard(sourcePiece.owner, PieceType::Rook);
                //rookBitBoard.setBitFalse(0, source.y);
                //rookBitBoard.setBitTrue(3, destination.y);

                // king
                m_CurrentInternalBoard[sourcePos.getVectorIndex()] = null;
                m_CurrentInternalBoard[destPos.getVectorIndex()] = sourcePiece;

                // rook
                m_CurrentInternalBoard[sourcePos.y * 8 + 0] = m_CurrentInternalBoard[sourcePos.y * 8 + 5];
                m_CurrentInternalBoard[sourcePos.y * 8 + 3] = null;
                lastPawnRush = new Position(-1, -1);
            }

            if (sourcePiece.getType() == PieceType.King) {
                kingPos[m_PlayerToMove] = destPos;
            }

            var checkType = isKingChecked(kingPos[m_PlayerToMove], m_PlayerToMove);
            if(checkType != CheckType.NoCheck) {
                restoreState();
                return false;
            }

            if (isMated(checkType)) {
                if (m_PlayerToMove == ChessPiece.PLAYER_WHITE) {
                    m_BoardStatus = BoardStatus.WhiteWon;
                } else {
                    m_BoardStatus = BoardStatus.BlackWon;
                }
            }

            if (m_PlayerToMove == ChessPiece.PLAYER_WHITE) {
                m_PlayerToMove = ChessPiece.PLAYER_BLACK;
            } else {
                m_PlayerToMove = ChessPiece.PLAYER_WHITE;
            }
        }

        return false;
    }

    public int getPlayerToMove() {
        return m_PlayerToMove;
    }

    public void setPlayerToMove(int playerID) {
        m_PlayerToMove = playerID;
    }

    public int getAIPlayerID() {
        return m_AIPlayerID;
    }

    Boolean isMoveLegal(Position source, Position destination, AtomicReference<MoveType> move)
    {
        var piece = getPiece(source);
        move.set(MoveType.Move);
        var targetPiece = getPiece(destination);
        int deltaX = destination.x - source.x;
        int deltaY = destination.y - source.y;
        if (targetPiece != null) {
            if (targetPiece.getPlayerID() != piece.getPlayerID()) {
                move.set(MoveType.Capture);
            } else {
                return false;
            }
        }


        int playerSign = -1;
        if (piece.getPlayerID() == ChessPiece.PLAYER_BLACK) {
            playerSign = 1;
        }

        switch(piece.getType()) {
            case Pawn:
                if (deltaY == 1 * playerSign) {
                    if ((deltaX == 1 || deltaX == -1)) {
                        if (targetPiece != null) {
                            return true;
                        }

                        if (destination.x == lastPawnRush.x && source.y == lastPawnRush.y) {
                            move.set(MoveType.EnPassant);
                            return true;
                        } else {
                            return false;
                        }
                    } else if (deltaX == 0 && targetPiece == null) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (deltaY == 2 * playerSign && !piece.hasMoved() && deltaX == 0 && getPiece(source.x, source.y + 1 * playerSign) == null) {
                    move.set(MoveType.PawnRush);
                    return true;
                }

                break;

            case Rook:
                if (deltaX == 0 && deltaY != 0) {
                    var maxY = Math.max(source.y, destination.y);
                    for (int i = Math.min(source.y, destination.y)+1; i < maxY; ++i) {
                        if (getPiece(source.x, i) != null) {
                            return false;
                        }
                    }
                    return true;
                } else if (deltaX != 0 && deltaY == 0) {
                    var maxX = Math.max(source.x, destination.x);
                    for (int i = Math.min(source.x, destination.x)+1; i < maxX; ++i) {
                        if (getPiece(i, source.y) != null) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return false;
                }

            case Bishop:
                if (deltaX / deltaY == 1) {
                    if (deltaY < 0) {
                        var p = findPieceAtMainDiagonalRay(source, true);
                        if (p.x <= destination.x || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (deltaY > 0) {
                        var p = findPieceAtMainDiagonalRay(source, false);
                        if (p.x >= destination.x || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (deltaX / deltaY == -1) {
                    if (deltaY < 0) {
                        var p = findPieceAtAntiDiagonalRay(source, true);
                        if (p.x >= destination.x  || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (deltaY > 0) {
                        var p = findPieceAtAntiDiagonalRay(source, false);
                        if (p.x <= destination.x || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }

                break;
            case Queen:
                if (deltaX == 0 && deltaY != 0) {
                    var maxY = Math.max(source.y, destination.y);
                    for (int i = Math.min(source.y, destination.y)+1; i < maxY; ++i)
                    {
                        if (getPiece(source.x, i)!= null)
                            return false;
                    }
                    return true;
                } else if (deltaX != 0 && deltaY == 0) {
                    var maxX = Math.max(source.x, destination.x);
                    for (int i = Math.min(source.x, destination.x)+1; i < maxX; ++i)
                    {
                        if (getPiece(i, source.y)!= null)
                            return false;
                    }
                    return true;
                } else if (deltaX/deltaY == 1) {
                    if (deltaY < 0) {
                        var p = findPieceAtMainDiagonalRay(source, true);
                        if (p.x <= destination.x || (p.x == source.x && p.y == source.y))
                            return true;
                        else
                            return false;
                    } else if (deltaY > 0) {
                        var p = findPieceAtMainDiagonalRay(source, false);
                        if (p.x >= destination.x || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (deltaX/deltaY == -1) {
                    if (deltaY < 0) {
                        var p = findPieceAtAntiDiagonalRay(source, true);
                        if (p.x >= destination.x  || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (deltaY > 0) {
                        var p = findPieceAtAntiDiagonalRay(source, false);
                        if (p.x <= destination.x || (p.x == source.x && p.y == source.y)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }

                break;
            case King:
                if (((deltaX * deltaX == 1 && deltaY * deltaY == 1) || (deltaX * deltaX == 0 && deltaY * deltaY == 1) || (deltaX * deltaX == 1 && deltaY * deltaY == 0))) {
                    return true;
                } else if (deltaY == 0 && deltaX == 2 && !piece.hasMoved() && !getPiece(7, source.y).hasMoved()) {
                    for (int i = 0; i < 2; ++i) {
                        if (isKingChecked(new Position(source.x+i, source.y), piece.getPlayerID()) != CheckType.NoCheck) {
                            return false;
                        }
                    }

                    move.set(MoveType.KingSideCastling);
                    return true;
                } else if (deltaY == 0 && deltaX == -2 && !piece.hasMoved() && !getPiece(0, source.y).hasMoved()) {
                    for (int i = 0; i < 2; ++i) {
                        if (isKingChecked(new Position(source.x-i, source.y), piece.getPlayerID()) != CheckType.NoCheck) {
                            return false;
                        }
                    }

                    move.set(MoveType.QueenSideCastling);
                    return true;
                } else {
                    return false;
                }

            case Knight:
                if (deltaX * deltaY == 2 || deltaX * deltaY == -2) {
                    return true;
                } else {
                    return false;
                }

            default:
                break;
        }

        return false;
    }

    Position findPieceAtMainDiagonalRay(Position pos, Boolean up) {
        Position ret = new Position(pos.x, pos.y);
        if (up) {
            int moves = MovementGenerator.numberOfMovesMainDiagonalUp[pos.x][pos.y];
            for (int i = 1; i <= moves; ++i) {
                if (getPiece(pos.x - i, pos.y - i) != null) {
                    ret.x = pos.x - i;
                    ret.y = pos.y - i;
                    break;
                }
            }
        } else {
            int moves = MovementGenerator.numberOfMovesMainDiagonalDown[pos.x][pos.y];
            for(int i = 1; i <= moves; ++i) {

                if(getPiece(pos.x+i, pos.y + i) != null) {
                    ret.x = pos.x + i;
                    ret.y = pos.y + i;
                    break;
                }
            }
        }

        return ret;
    }

    Position findPieceAtAntiDiagonalRay(Position pos, Boolean up) {
        Position ret = new Position(pos.x, pos.y);
        if(up) {
            int moves = MovementGenerator.numberOfMovesAntiDiagonalUp[pos.x][pos.y];
            for(int i = 1; i <= moves; ++i) {
                if(getPiece(pos.x + i, pos.y - i) != null) {
                    ret.x = pos.x + i;
                    ret.y = pos.y - i;
                    break;
                }
            }
        } else {
            int moves = MovementGenerator.numberOfMovesAntiDiagonalDown[pos.x][pos.y];
            for (int i = 1; i <= moves; ++i) {
                if (getPiece(pos.x - i, pos.y + i) != null) {
                    ret.x = pos.x - i;
                    ret.y = pos.y + i;
                    break;
                }
            }
        }
        return ret;
    }

    Position findPieceAtColumnRay(Position pos, Boolean up) {
        Position ret = new Position(pos.x, pos.y);
        if (up) {
            for (int y = pos.y-1; y >= 0; --y) {
                if(getPiece(pos.x, y) != null) {
                    ret.y = y;
                    break;
                }
            }
        } else {
            for (int y = pos.y+1; y < 8; ++y) {
                if(getPiece(pos.x, y) != null) {
                    ret.y = y;
                    break;
                }
            }
        }

        return ret;
    }

    Position findPieceAtRowRay(Position pos, Boolean left)
    {
        Position ret = new Position(pos.x, pos.y);
        if (left) {
            for( int x = pos.x - 1; x >= 0; --x) {
                if (getPiece(x, pos.y) != null) {
                    ret.x = x;
                    break;
                }
            }
        } else {
            for (int x = pos.x + 1; x < 8; ++x) {
                if (getPiece(x, pos.y) != null) {
                    ret.x = x;
                    break;
                }
            }
        }

        return ret;
    }

    CheckType isKingChecked(Position kingPos, int playerID) {
        int check = 0;
        var blockingPiece = getPiece(findPieceAtColumnRay(kingPos, true));

        if(blockingPiece != null && blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Rook || blockingPiece.getType() == PieceType.Queen))
            ++check;
        blockingPiece = getPiece(findPieceAtColumnRay(kingPos, false));
        if(blockingPiece != null &&blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Rook || blockingPiece.getType() == PieceType.Queen))
            ++check;
        blockingPiece = getPiece(findPieceAtRowRay(kingPos, true));
        if(blockingPiece != null && blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Rook || blockingPiece.getType() == PieceType.Queen))
            ++check;
        blockingPiece = getPiece(findPieceAtRowRay(kingPos, false));
        if(blockingPiece != null && blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Rook || blockingPiece.getType() == PieceType.Queen))
            ++check;

        blockingPiece = getPiece(findPieceAtMainDiagonalRay(kingPos, false));
        if(blockingPiece != null && blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Bishop || blockingPiece.getType() == PieceType.Queen))
            ++check;

        blockingPiece = getPiece(findPieceAtMainDiagonalRay(kingPos, true));
        if(blockingPiece != null && blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Bishop || blockingPiece.getType() == PieceType.Queen))
            ++check;

        blockingPiece = getPiece(findPieceAtAntiDiagonalRay(kingPos, false));
        if(blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Bishop || blockingPiece.getType() == PieceType.Queen))
            ++check;

        blockingPiece = getPiece(findPieceAtAntiDiagonalRay(kingPos, true));
        if(blockingPiece.getPlayerID() != playerID && (blockingPiece.getType() == PieceType.Bishop || blockingPiece.getType() == PieceType.Queen))
            ++check;

        /* if((g_MoveGenerator.getPawnAttackersAt(kingPos.x, kingPos.y, playerID)
                & m_bitState.getBitBoard(getOtherPlayer(playerID), PieceType::Pawn)).getBoard())
            ++check;

        if((g_MoveGenerator.getKnightAttackersAt(kingPos.x, kingPos.y)
                & m_bitState.getBitBoard(getOtherPlayer(playerID), PieceType::Knight)).getBoard())
            ++check;

        if((g_MoveGenerator.getKingAttackerAt(kingPos.x, kingPos.y)
                & m_bitState.getBitBoard(getOtherPlayer(playerID), PieceType::King)).getBoard())
            ++check;*/

        if (check == 0) {
            return CheckType.NoCheck;
        } else if (check == 1) {
            return CheckType.SingleCheck;
        } else {
            return CheckType.DoubleCheck;
        }
    }

    Boolean isMated(CheckType checkType) {
        if (checkType == CheckType.SingleCheck) {
            Vector<MoveImpl> moves = Main.s_MovementGenerator.generateMoves(this, m_PlayerToMove);
            for (var move : moves) {
                if (makeMove(move.fromPos, move.toPos, false)) {
                    restoreState();
                    m_PlayerToMove = getOtherPlayer(m_PlayerToMove);
                    return false;
                }
            }
        } else if (checkType == CheckType.DoubleCheck) {
            Vector<MoveImpl> moves = new Vector<>();
            Main.s_MovementGenerator.generateKingMoves(this, m_PlayerToMove, moves);
            for (var move : moves) {
                if (makeMove(move.fromPos, move.toPos, false)) {
                    restoreState();
                    m_PlayerToMove = getOtherPlayer(m_PlayerToMove);
                    return false;
                }

                restoreState();
                m_PlayerToMove = getOtherPlayer(m_PlayerToMove);
            }
        }

        return checkType != CheckType.NoCheck;
    }

    private int getOtherPlayer(int playerID) {
        if (playerID == ChessPiece.PLAYER_WHITE) {
            return ChessPiece.PLAYER_BLACK;
        }

        return ChessPiece.PLAYER_WHITE;
    }
}
