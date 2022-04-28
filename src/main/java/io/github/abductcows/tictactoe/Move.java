package io.github.abductcows.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum Move {

    X("X"), O("O"), Empty("");

    private final String stringFormat;

    Move(String stringFormat) {
        this.stringFormat = stringFormat;
    }

    @Override
    public String toString() {
        return stringFormat;
    }

    static List<Move> getEmptyBoard(int sideSize) {
        return Stream.generate(() -> Empty)
                .limit((long) sideSize * sideSize)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    Move getNextMove() {
        return switch (this) {
            case X -> O;
            case O -> X;
            case Empty -> throw new IllegalStateException("next move called on Empty move");
        };
    }

    Winner asWinner() {
        return switch (this) {

            case X -> Winner.X;
            case O -> Winner.O;
            case Empty -> Winner.Undecided;
        };
    }
}
