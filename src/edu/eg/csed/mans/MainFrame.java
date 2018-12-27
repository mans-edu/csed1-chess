package edu.eg.csed.mans;
import edu.eg.csed.mans.chess.UI.JChessPanel;

import javax.swing.*;

public class MainFrame extends JFrame {
    private JChessPanel m_ChessPanel;

    public MainFrame(String title) {
        super(title);
    }

    public void createUIComponents() {
        // setting up chess panel
        m_ChessPanel = new JChessPanel();
        m_ChessPanel.createUIComponents();
        setContentPane(m_ChessPanel);
    }
}
