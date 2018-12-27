package edu.eg.csed.mans.chess.Logic;

import edu.eg.csed.mans.chess.ChessPiece;
import edu.eg.csed.mans.chess.PieceType;

public class BoardBitState {
    BitBoard boards[][] = new BitBoard[2][6];

    BitBoard getBitBoard(ChessPiece piece)
    {
        return boards[piece.getPlayerID()][PieceTypeToInt(piece.getType())];
    }

    BitBoard getBitBoard(int playerID, PieceType type)
    {
        return boards[playerID][type.ordinal()];
    }

    public static int PieceTypeToInt(PieceType type) {
        switch (type) {
            case Rook: return 1;
            case Knight: return 2;
            case Bishop: return 3;
            case Queen: return 4;
            case King: return 5;
            default: return 0;
        }
    }
}
