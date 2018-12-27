package edu.eg.csed.mans.chess.Logic;

import edu.eg.csed.mans.chess.MoveImpl;

import java.util.concurrent.Callable;

public class CallableAIMovementInner implements Callable<Integer> {
    AI m_AI;
    ChessBoard m_ChessBoard;
    int m_PlayerID;
    int m_Depth;
    int m_Beta;
    int m_Alpha;

    public CallableAIMovementInner(AI ai, ChessBoard chessBoard, int playerID, int depth, int beta, int alpha) {
        this.m_AI = ai;
        this.m_ChessBoard = chessBoard;
        this.m_PlayerID = playerID;
        this.m_Depth = depth;
        this.m_Beta = beta;
        this.m_Alpha = alpha;
    }

    public Integer call() {
        return m_AI.negamax_inner(m_ChessBoard, m_PlayerID, m_Depth, m_Beta, m_Alpha);
    }
}
