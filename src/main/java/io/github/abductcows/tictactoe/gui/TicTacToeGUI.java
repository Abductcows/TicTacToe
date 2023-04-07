package io.github.abductcows.tictactoe.gui;

import io.github.abductcows.tictactoe.api.Arbiter;
import io.github.abductcows.tictactoe.api.Winner;
import io.github.abductcows.tictactoe.domain.Board;
import io.github.abductcows.tictactoe.domain.FastArbiter;
import io.github.abductcows.tictactoe.domain.LargeScaleArbiter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TicTacToeGUI {

    private JFrame frame;
    private List<JButton> cells;

    private Board board;
    private Arbiter arbiter;
    private final int n;

    public TicTacToeGUI(int sideSize) {
        if (sideSize <= 0) throw new IllegalArgumentException("TicTacToe side size must be > 0");
        this.n = sideSize;
    }

    public void run() {
        resetLogic();
        resetGUI();
    }

    private void resetLogic() {
        this.board = Board.getEmptyBoard(n);
        this.arbiter = n <= 8 ? new FastArbiter(n) : new LargeScaleArbiter(n);
    }

    private void resetGUI() {
        if (this.frame != null) this.frame.dispose();
        this.frame = new JFrame("Tic Tac Toe");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int wSize = (int) (0.85 * Math.min(screenSize.width, screenSize.height));
        frame.setSize(wSize, wSize);

        this.cells = generateCells();
        int fontSize = wSize / n / 2;
        var cellFont = new Font("Sans Serif", Font.BOLD, fontSize);

        var content = new JPanel(new GridLayout(n, n));
        for (var cell : cells) {
            cell.setFont(cellFont);
            content.add(cell);
        }

        frame.add(content);
        frame.setLocationRelativeTo(null);
        updateCurrentPlayerMessage();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private List<JButton> generateCells() {
        var cells = Stream.generate(JButton::new).limit((long) n * n).collect(Collectors.toList());

        for (int i = 0, limit = cells.size(); i < limit; ++i) {
            var next = cells.get(i);
            next.setFocusable(false);

            final int cellIndex = i;
            next.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && arbiter.canPlay(board, cellIndex)) {
                        arbiter.registerMove(board, cellIndex);
                        progressGame();
                    }
                }
            });
        }

        return cells;
    }

    private void progressGame() {
        var currentBoard = board.getMoves();
        for (int i = 0, n = cells.size(); i < n; ++i) {
            cells.get(i)
                .setText(currentBoard.get(i).toString());
        }

        var winner = arbiter.getWinner(board);
        if (winner != Winner.Undecided) {
            showWinnerMessage(winner);
            run();
        }
        updateCurrentPlayerMessage();
    }

    private void showWinnerMessage(Winner winner) {
        JOptionPane.showConfirmDialog(
            null,
            getWinMessage(winner),
            "Game Over",
            JOptionPane.DEFAULT_OPTION);
    }

    private String getWinMessage(Winner winner) {
        return switch (winner) {
            case X -> "X Won";
            case O -> "O won";
            case Draw -> "It's a Draw";
            case Undecided -> throw new IllegalStateException("win message requested for Undecided state");
        };
    }

    private void updateCurrentPlayerMessage() {
        frame.setTitle("TicTacToe - " + arbiter.getCurrentPlayer() + " to play");
    }
}
