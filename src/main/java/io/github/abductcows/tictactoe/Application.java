package io.github.abductcows.tictactoe;

import io.github.abductcows.tictactoe.gui.TicTacToeGUI;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        int size = 3;

        if (args.length > 0) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("TicTacToe size must be an integer > 0");
            }
        }

        SwingUtilities.invokeLater(new TicTacToeGUI(size)::run);
    }
}
