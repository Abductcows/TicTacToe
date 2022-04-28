package io.github.abductcows.tictactoe;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {

        TicTacToeGUI a = new TicTacToeGUI();
        SwingUtilities.invokeLater(a::run);
    }
}
