package edu.eg.csed.mans.chess.UI;

import edu.eg.csed.mans.chess.ChessPiece;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class JChessPanelMouseMotionListener implements MouseMotionListener {
    JChessPanel m_ChessPanel;

    public JChessPanelMouseMotionListener(JChessPanel chessPanel) {
        m_ChessPanel = chessPanel;
    }

    public void mouseDragged(MouseEvent e) {
        if (m_ChessPanel.draggedPiece != null) {
            m_ChessPanel.draggedPiece.currentMousePosition = e.getPoint();
            m_ChessPanel.repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
    }
}
