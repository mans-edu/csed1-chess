package edu.eg.csed.mans.chess.UI;

import edu.eg.csed.mans.Main;
import edu.eg.csed.mans.chess.ChessPiece;
import edu.eg.csed.mans.chess.Logic.ChessBoard;
import edu.eg.csed.mans.chess.Logic.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class JChessPanel extends JPanel {
    public ChessPiece draggedPiece = null;
    public Vector<Position> highlightedTiles = null;

    public Rectangle getPaintRectangle() {
        var width = getWidth();
        var height = getHeight();
        var sizeMax = Math.max(width, height);
        var size = Math.min(width, height);

        var x = (sizeMax - height) / 2;
        var y = (sizeMax - width) / 2;

        return new Rectangle(x, y, size, size);
    }

    public Boolean isTileHighlithed(Position position) {
        if (highlightedTiles == null) {
            return false;
        }

        for (var hPos : highlightedTiles) {
            if (hPos.x == position.x && hPos.y == position.y) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        var rect = getPaintRectangle();
        var defaultX = rect.x;
        var tileSize = rect.width / 8;
        var x = defaultX;
        var y = rect.y;

        ChessPiece draggedPiece = null;

        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                var position = new Position(i, j);
                var highlighted = isTileHighlithed(position);

                Color bgColor;
                if ((i + j) % 2 == 0) {
                    if (highlighted) {
                        bgColor = Color.lightGray;
                    } else {
                        bgColor = new Color(240, 217, 181);
                    }
                } else {
                    if (highlighted) {
                        bgColor = Color.gray;
                    } else {
                        bgColor = new Color(181, 136, 99);
                    }
                }

                g.setColor(bgColor);
                g.fillRect(x, y, tileSize, tileSize);

                var piece = Main.s_ChessBoard.getPiece(i, j);
                if (piece != null) {
                    if (piece.isBeingDragged) {
                        draggedPiece = piece;
                    } else {
                        drawChessPiece(g, piece, x, y, tileSize, tileSize);
                    }
                }

                x += tileSize;
            }

            x = defaultX;
            y += tileSize;
        }

        if (draggedPiece != null) {
            drawChessPiece(g, draggedPiece, 0, 0, tileSize, tileSize);
        }
    }

    private void drawChessPiece(Graphics g, ChessPiece piece, int x, int y, int width, int height) {
        if (piece.isBeingDragged) {
            x = piece.currentMousePosition.x - piece.mousePositionDelta.x;
            y = piece.currentMousePosition.y - piece.mousePositionDelta.y;
        }

        if (false) {
            g.setColor(new Color(0x50ff0000, true));
            g.fillOval(x, y, width, height);
        }

        g.drawImage(piece.getImage(), x, y, width, height, null);
    }

    public void createUIComponents() {
        var mouseListner = new JChessPanelMouseListener(this);
        var mouseMotionListner = new JChessPanelMouseMotionListener(this);

        addMouseListener(mouseListner);
        addMouseMotionListener(mouseMotionListner);



        mouseListner.mouseMotionListener = mouseMotionListner;
        //mouseMotionListner.mouseListner = mouseListner;
    }
}
