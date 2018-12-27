package edu.eg.csed.mans.chess.UI;

import edu.eg.csed.mans.Main;
import edu.eg.csed.mans.chess.ChessPiece;
import edu.eg.csed.mans.chess.Logic.Position;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

public class JChessPanelMouseListener implements MouseListener {
    JChessPanel m_ChessPanel;
    Position m_SourcePosition;

    public JChessPanelMouseMotionListener mouseMotionListener = null;

    public JChessPanelMouseListener(JChessPanel chessPanel) {
        m_ChessPanel = chessPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // we don only handle left clicks
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        var rect = m_ChessPanel.getPaintRectangle();
        var tileSize = rect.width / 8;

        int gridX = (e.getX() - rect.x) / tileSize;
        int gridY = (e.getY() - rect.y) / tileSize;

        if (gridX < 0 || gridX >= 8 || gridY < 0 || gridY >= 8) {
            return;
        }

        var piece = Main.s_ChessBoard.getPiece(gridX, gridY);
        if (piece == null || piece.getPlayerID() != Main.s_ChessBoard.getPlayerToMove() || piece.getPlayerID() == Main.s_ChessBoard.getAIPlayerID()) {
            return;
        }

        var validMoves = Main.s_MovementGenerator.generateMoves(Main.s_ChessBoard, piece.getPlayerID());
        Vector<Position> highlightedTiles = new Vector<>();
        Boolean pieceCanMove = false;
        for (var move : validMoves) {
            if (move.fromPos.x == gridX && move.fromPos.y == gridY) {
                highlightedTiles.add(move.toPos);
            }
        }

        if (highlightedTiles.size() == 0) {
            return;
        }

        m_SourcePosition = new Position(gridX, gridY);
        m_ChessPanel.draggedPiece = piece;
        m_ChessPanel.highlightedTiles = highlightedTiles;
        piece.isBeingDragged = true;
        piece.currentMousePosition = e.getPoint();

        int deltaX = e.getPoint().x - (rect.x + gridX * tileSize);
        int deltaY = e.getPoint().y - (rect.y + gridY * tileSize);

        piece.mousePositionDelta = new Point(deltaX, deltaY);

        m_ChessPanel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 || m_ChessPanel.draggedPiece == null) {
            return;
        }

        var rect = m_ChessPanel.getPaintRectangle();
        var tileSize = rect.width / 8;

        int gridX = (e.getX() - rect.x) / tileSize;
        int gridY = (e.getY() - rect.y) / tileSize;

        if (gridX >= 0 && gridX < 8 && gridY >= 0 || gridY < 8) {
            var m_DestPosition = new Position(gridX, gridY);
            if (m_SourcePosition.x != m_DestPosition.x || m_SourcePosition.y != m_DestPosition.y) {
                Main.s_ChessBoard.makeMove(m_SourcePosition, m_DestPosition, true);
            }
        }

        m_ChessPanel.draggedPiece.isBeingDragged = false;
        m_ChessPanel.draggedPiece.currentMousePosition = null;
        m_ChessPanel.draggedPiece = null;
        m_ChessPanel.highlightedTiles = null;
        m_ChessPanel.repaint();

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
