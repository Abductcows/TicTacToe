package io.github.abductcows.tictactoe.domain;

import io.github.abductcows.tictactoe.api.Move;
import io.github.abductcows.tictactoe.api.Winner;
import io.github.abductcows.tictactoe.api.Arbiter;

import java.util.List;

public class FastArbiter implements Arbiter {

    private final int n;
    private final long[] winningStates;
    private Move currentPlayer = Move.X;

    public FastArbiter(int n) {
        if (n <= 0 || n > 8) throw new IllegalArgumentException("Board side size must be in the range [1, 8]");

        this.n = n;
        this.winningStates = generateWinningStates();
    }

    @Override
    public boolean canPlay(Board currentBoard, int oneDimIndex) {
        return currentBoard.get(oneDimIndex) == Move.Empty;
    }

    @Override
    public boolean registerMove(Board currentBoard, int oneDimIndex) {
        if (!canPlay(currentBoard, oneDimIndex)) return false;

        currentBoard.set(oneDimIndex, currentPlayer);
        currentPlayer = getNextPlayer();
        return true;
    }

    @Override
    public Move getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public Move getNextPlayer() {
//        return Move.X;
        switch (currentPlayer) {
            case X -> {
                return Move.O;
            }
            case O -> {
                return Move.X;
            }
        }
        throw new IllegalArgumentException("Can only get next player from valid player symbols");
    }

    @Override
    public Winner getWinner(Board currentBoard) {
        List<Move> moves = currentBoard.getMoves();
        long xPositions = 0, oPositions = 0;

        // check winner
        for (Move move : moves) {
            xPositions <<= 1;
            oPositions <<= 1;
            if (move == Move.X) xPositions |= 1;
            else if (move == Move.O) oPositions |= 1;
        }

        for (long winState : winningStates) {
            if ((xPositions & winState) == winState) return Winner.X;
            if ((oPositions & winState) == winState) return Winner.O;
        }

        // check draw
        int movesLeft = n * n - Long.bitCount(xPositions) - Long.bitCount(oPositions);
        if (movesLeft == 0) return Winner.Draw;
        return Winner.Undecided;
    }

    private long[] generateWinningStates() {
        long[] winningStates = new long[2 * n + 2];
        int currentState = 0;

        // rows
        long mask = 0b11111111 >> 8 - n;
        for (int i = 0; i < n; ++i) {
            winningStates[currentState++] = mask;
            mask <<= n;
        }

        // columns
        mask = 0;
        for (int i = 0; i < n; ++i) mask = (mask << n) | 1;
        for (int i = 0; i < n; ++i) {
            winningStates[currentState++] = mask;
            mask <<= 1;
        }

        // diagonals
        mask = 1;
        for (int i = 0; i < n - 1; ++i) mask = (mask << n + 1) | 1;
        winningStates[currentState++] = mask;

        mask = 1L << n - 1;
        for (int i = 0; i < n - 1; ++i) mask = (mask << n - 1) | (1L << n - 1);
        winningStates[currentState] = mask;

        return winningStates;
    }
}
