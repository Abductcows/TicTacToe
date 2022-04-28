package io.github.abductcows.tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TicTacToeGUI {

    private Board board;
    private JFrame frame;
    private List<JButton> cells;
    private Move nextMove;

    public void run() {
        init();

        var content = new JPanel(new GridLayout(3, 3));
        cells.forEach(content::add);
        frame.add(content);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void init() {
        board = new Board(3);
        nextMove = Move.X;

        frame = new JFrame("Tic Tac Toe");
        frame.setSize(new Dimension(800, 800));

        initCells();
    }

    private void cleanUp() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    private void initCells() {
        var font = new Font("Sans Serif", Font.BOLD, 46);
        cells = Stream.generate(JButton::new).limit(9).collect(Collectors.toList());
        for (int i = 0; i < cells.size(); i++) {
            var next = cells.get(i);

            next.setFont(font);
            next.setFocusable(false);

            int cellIndex = i;
            next.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {

                    if (e.getButton() == MouseEvent.BUTTON1
                            && board.get(cellIndex) == Move.Empty) {

                        board.set(cellIndex, nextMove);
                        refreshBoard();
                    }
                }
            });
        }
    }

    void refreshBoard() {

        var newBoard = board.getMoves();
        for (int i = 0; i < cells.size(); i++) {
            cells.get(i).setText(newBoard.get(i).toString());
        }

        var winner = board.checkWinner();
        if (winner != Winner.Undecided) {
            showWinnerMessage(winner);
            cleanUp();
            run();
        } else {
            nextMove = nextMove.getNextMove();
        }
    }

    private void showWinnerMessage(Winner winner) {
        JOptionPane.showConfirmDialog(
                null,
                winner.getWinMessage(),
                "Game Over",
                JOptionPane.DEFAULT_OPTION);
    }
}
