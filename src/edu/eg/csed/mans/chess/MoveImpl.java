package edu.eg.csed.mans.chess;

import edu.eg.csed.mans.chess.Logic.Position;

public class MoveImpl {
    public MoveType type;

    public Position fromPos = new Position(-1, -1);
    public Position toPos = new Position(-1, -1);

    public MoveImpl() {}
    public MoveImpl(MoveImpl other) {
        type = other.type;
        fromPos = other.fromPos;
        toPos = other.toPos;
    }
}
