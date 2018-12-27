package edu.eg.csed.mans.chess;

public class PieceFactory {


    public static ChessPiece CreatePiece(PieceType pieceType, int playerID) {
        switch (pieceType) {
            case Pawn:
            case Rook:
            case Knight:
            case Bishop:
            case Queen:
            case King:
                return new ChessPiece(playerID, pieceType);
        }

        return null;
    }
}
