package io.github.abductcows.tictactoe.api;

public enum Move {

    X("X"), O("O"), Empty("");

    private final String stringFormat;

    Move(String stringFormat) {
        this.stringFormat = stringFormat;
    }

    @Override
    public String toString() {
        return stringFormat;
    }
}
