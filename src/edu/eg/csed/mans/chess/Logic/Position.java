package edu.eg.csed.mans.chess.Logic;

public class Position {
    public int x ;
    public int y;

    public Position(int _x, int _y) {
        x = _x;
        y = _y;
    }

    int getVectorIndex(){
        return 8 * y + x;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
