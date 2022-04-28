package io.github.abductcows.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {

    private final List<Move> board;
    public final int sideSize;

    public Board(int sideSize) {
        this(Move.getEmptyBoard(sideSize));
    }

    public Board(List<Move> initial) {
        board = new ArrayList<>(initial);
        sideSize = (int) Math.round(Math.sqrt(board.size()));
    }

    public Board(Move[] initial) {
        this(Arrays.asList(initial));
    }

    public Move get(int index) {
        return board.get(index);
    }

    public void set(int index, Move move) {
        board.set(index, move);
    }

    public List<Move> getMoves() {
        return Collections.unmodifiableList(board);
    }

    Winner checkWinner() {
        var emptyCount = board.stream().filter(Move.Empty::equals).count();
        if (emptyCount > board.size() - sideSize) return Winner.Undecided;

        for (var row = 0; row < sideSize; row++) {
            var maybeWinner = tryGetSizeInARow(row * sideSize, 1);
            if (maybeWinner != Winner.Undecided) return maybeWinner;
        }

        for (var col = 0; col < sideSize; col++) {
            var maybeWinner = tryGetSizeInARow(col, sideSize);
            if (maybeWinner != Winner.Undecided) return maybeWinner;
        }

        {
            var mainDiagonal = tryGetSizeInARow(0, sideSize + 1);
            if (mainDiagonal != Winner.Undecided) return mainDiagonal;
        }

        {
            var secondDiagonal = tryGetSizeInARow(sideSize - 1, sideSize - 1);
            if (secondDiagonal != Winner.Undecided) return secondDiagonal;
        }

        if (emptyCount == 0) {
            return Winner.Draw;
        }
        return Winner.Undecided;
    }

    Winner tryGetSizeInARow(int start, int step) {
        var e = board.get(start);
        for (int i = 1; i < sideSize; i++) {
            var next = board.get(start + i * step);
            if (next != e) return Winner.Undecided;
        }
        return e.asWinner();
    }
}
