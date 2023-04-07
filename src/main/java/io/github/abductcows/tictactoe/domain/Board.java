package io.github.abductcows.tictactoe.domain;

import io.github.abductcows.tictactoe.api.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

    private List<Move> moves;
    public final int sideSize;

    private Board(int sideSize) {
        this.sideSize = sideSize;
        this.moves = null;
    }

    public Move get(int index) {
        return moves.get(index);
    }

    public void set(int index, Move move) {
        moves.set(index, move);
    }

    public List<Move> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    public static Board getEmptyBoard(int sideSize) {
        Board result = new Board(sideSize);
        result.moves = Stream.generate(() -> Move.Empty)
            .limit((long) sideSize * sideSize)
            .collect(Collectors.toCollection(ArrayList::new));

        return result;
    }
}
