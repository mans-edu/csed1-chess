package edu.eg.csed.mans;

import edu.eg.csed.mans.chess.Logic.ChessBoard;
import edu.eg.csed.mans.chess.Logic.MovementGenerator;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static ChessBoard s_ChessBoard;
    public static MovementGenerator s_MovementGenerator;

    public static void main(String[] args) {
        s_ChessBoard = new ChessBoard();
        s_MovementGenerator = new MovementGenerator();

        // initializing the frame
        MainFrame frame = new MainFrame("Chess Application");
        frame.setVisible(true);

        try {
            frame.createUIComponents();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // set the size to 800x600, position to center
        frame.setSize(new Dimension(800, 600));
        frame.setMinimumSize(new Dimension(400, 200));
        frame.setMaximumSize(new Dimension(1024, 768));

        // set the position to the center of the screen
        frame.setLocationRelativeTo(null);

        // set close operation
        // default operation will return (void)0 to the main thread to close it)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
