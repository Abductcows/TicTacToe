package io.github.abductcows.tictactoe.api;

import io.github.abductcows.tictactoe.domain.Board;

public interface Arbiter {

    Move getCurrentPlayer();
    Move getNextPlayer();

    boolean canPlay(Board currentBoard, int moveIndex);
    boolean registerMove(Board currentBoard, int moveIndex);

    Winner getWinner(Board currentBoard);
}
