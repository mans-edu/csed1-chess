package edu.eg.csed.mans.chess;

import java.awt.*;

public class ChessPiece {
    public static int PLAYER_NONE = -1; // none (used to detect if ai is activated or not)
    public static int PLAYER_WHITE = 0;
    public static int PLAYER_BLACK = 1;

    private Integer m_PlayerID;
    private PieceType m_Type;
    private Image m_Image;
    private Boolean m_HasMoved = false;

    public Boolean isBeingDragged = false;
    public Point currentMousePosition = null;
    public Point mousePositionDelta = null;

    public ChessPiece(int playerID, PieceType pieceType) {
        m_PlayerID = playerID;
        m_Type = pieceType;

        m_Image = ImageFactory.GetImage(GetImageIdentifier(m_Type, m_PlayerID));
    }

    public int getPlayerID() {
        return m_PlayerID;
    }

    public Image getImage() {
        return m_Image;
    }

    public PieceType getType() {
        return m_Type;
    }

    public Boolean hasMoved() {
        return m_HasMoved;
    }

    public void setHasMoved(Boolean value) {
        m_HasMoved = true;
    }

    protected static String GetPlayerIdentifier(int playerID) {
        if (playerID == PLAYER_WHITE) {
            return "white";
        } else {
            return "black";
        }
    }

    protected static String GetPieceIdentifier(PieceType pieceType) {
        switch (pieceType) {
            case Pawn:
                return "pawn";
            case Knight:
                return "knight";
            case Rook:
                return "rook";
            case Bishop:
                return "bishop";
            case Queen:
                return "queen";
            case King:
                return "king";
        }

        return "";
    }

    protected static String GetImageIdentifier(PieceType pieceType, int playerID) {
        return String.format("resources/%s_%s.png" , GetPlayerIdentifier(playerID), GetPieceIdentifier(pieceType));
    }
}
