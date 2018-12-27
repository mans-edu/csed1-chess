package edu.eg.csed.mans.chess.Logic;

import edu.eg.csed.mans.Main;
import edu.eg.csed.mans.chess.ChessPiece;
import edu.eg.csed.mans.chess.MoveImpl;
import edu.eg.csed.mans.chess.MoveType;
import javafx.util.Pair;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AI {
    public static int InitialDepth = 5;

    public static int[][] randomBitStrings = {{0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}};

    public Integer negamax_inner(ChessBoard chessBoard, int playerID, int depth, int alpha, int beta) {
        return 0;
    }

    public MoveImpl negamax(ChessBoard chessBoard, int playerID, int depth, int alpha, int beta) {
        MoveImpl bestMove = new MoveImpl();
        bestMove.type = MoveType.NoMove;
        if (depth < 0) {
            return bestMove;
        }

        // copy the current state of the game!
        var chessBoardCopy = chessBoard.clone();

        Vector<MoveImpl> moves = Main.s_MovementGenerator.generateMoves(chessBoardCopy, playerID);
        if (moves.size() == 0) {
            return bestMove;
        }

        bestMove = moves.get(0);
        Vector<Pair<MoveImpl, Future<Integer>>> results = new Vector<>();

        ExecutorService executor = Executors.newFixedThreadPool(moves.size());

        for (var move : moves) {
            if (chessBoardCopy.makeMove(move.fromPos, move.toPos, false)) {
                int otherPlayerID = playerID == ChessPiece.PLAYER_WHITE ? ChessPiece.PLAYER_BLACK : ChessPiece.PLAYER_WHITE;
                Future<Integer> future = executor.submit(new CallableAIMovementInner(this, chessBoardCopy, otherPlayerID, depth - 1, -beta, -alpha));
                results.add(new Pair<>(move, future));

                chessBoardCopy.restoreState();
                chessBoardCopy.setPlayerToMove(playerID);
            }
        }

        try {
            int bestValue = Integer.MIN_VALUE;
            for (var result : results) {
                int value = result.getValue().get();
                if (-value > bestValue) {
                    bestValue = -value;
                    if (depth == InitialDepth) {
                        bestMove = result.getKey();
                    }
                }
            }
        } catch (Throwable e) {}

        return bestMove;
    }
}
