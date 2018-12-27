package edu.eg.csed.mans.chess.Logic;

import java.math.BigInteger;

public class BitBoard {
    private long m_Board;

    private static Long BitMasks[][] = new Long[8][8];

    private static BitBoard AColumn = new BitBoard(
            "10000000" +
                    "10000000" +
                    "10000000" +
                    "10000000" +
                    "10000000" +
                    "10000000" +
                    "10000000" +
                    "10000000");

    private static BitBoard HColumn = new BitBoard(
            "00000001" +
                    "00000001" +
                    "00000001" +
                    "00000001" +
                    "00000001" +
                    "00000001" +
                    "00000001" +
                    "00000001");

    public static void SetBitMasks() {
        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j) {
                BitMasks[i][j] = 1L << (i + 8 * j);
            }
        }
    }

    public BitBoard(long board) {
        m_Board = board;
    }

    public BitBoard(String board_string) {
        for(int i = 0; i < Math.min(board_string.length(), 64); ++i) {
            m_Board = m_Board << 1L;
            if(board_string.charAt(i) == '1') {
                m_Board |= 1L;
            }
        }
    }

    public BitBoard() {
        m_Board = 0L;
    }

    public long rightShift(int n) {
        m_Board <<= n;
        return m_Board;
    }

    public long leftShift(int n) {
        m_Board >>= n;
        return m_Board;
    }

    public long safeRightShift() {
        m_Board &= ~AColumn.m_Board;
        m_Board <<= 1;
        return m_Board;
    }

    public long safeLeftShift() {
        m_Board &= ~HColumn.m_Board;
        m_Board >>= 1;
        return m_Board;
    }

    void setBitTrue(int x, int y)
    {
        m_Board |= BitMasks[x][y];
    }
    void setBitFalse(int x, int y)
    {
        m_Board &= ~BitMasks[x][y];
    }
}
