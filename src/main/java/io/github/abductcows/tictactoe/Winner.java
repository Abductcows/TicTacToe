package io.github.abductcows.tictactoe;

enum Winner {
    X, O, Draw, Undecided;

    String getWinMessage() {
        return switch (this) {
            case X -> "X Won";
            case O -> "O won";
            case Draw -> "It's a Draw";
            case Undecided -> throw new IllegalStateException("win message requested for Undecided state");
        };
    }
}
