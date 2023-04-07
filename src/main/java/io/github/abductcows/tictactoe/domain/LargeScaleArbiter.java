package io.github.abductcows.tictactoe.domain;

import io.github.abductcows.tictactoe.api.Arbiter;
import io.github.abductcows.tictactoe.api.Move;
import io.github.abductcows.tictactoe.api.Winner;

import java.util.List;

public class LargeScaleArbiter implements Arbiter {

    private final int n;
    private Move currentPlayer = Move.X;

    public LargeScaleArbiter(int n) {
        if (n <= 0) throw new IllegalArgumentException("Board side size must be positive");
        this.n = n;
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
        var emptyCount = moves.stream().filter(Move.Empty::equals).count();
        if (emptyCount > moves.size() - n) return Winner.Undecided;

        for (var row = 0; row < n; ++row) {
            var winner = tryGetNInARow(moves, n * row, 1);
            if (winner != Winner.Undecided) return winner;
        }

        for (var col = 0; col < n; ++col) {
            var winner = tryGetNInARow(moves, col, n);
            if (winner != Winner.Undecided) return winner;
        }

        {
            var mainDiagonal = tryGetNInARow(moves, 0, n + 1);
            if (mainDiagonal != Winner.Undecided) return mainDiagonal;
        }

        {
            var secondDiagonal = tryGetNInARow(moves, n - 1, n - 1);
            if (secondDiagonal != Winner.Undecided) return secondDiagonal;
        }

        if (emptyCount == 0) return Winner.Draw;
        return Winner.Undecided;
    }


    private Winner tryGetNInARow(List<Move> moves, int start, int step) {
        var player = moves.get(start);
        if (player == Move.Empty) return Winner.Undecided;
        for (int i = 1; i < n; ++i) {
            var next = moves.get(start + i * step);
            if (next != player) return Winner.Undecided;
        }
        return asWinner(player);
    }

    private Winner asWinner(Move player) {
        if (player == Move.X) return Winner.X;
        if (player == Move.O) return Winner.O;
        throw new IllegalArgumentException("Can only get winner from valid player symbols");
    }
}
