package io.github.abductcows.tictactoe.gui;

import io.github.abductcows.tictactoe.api.Arbiter;
import io.github.abductcows.tictactoe.api.Winner;
import io.github.abductcows.tictactoe.domain.Board;
import io.github.abductcows.tictactoe.domain.FastArbiter;
import io.github.abductcows.tictactoe.domain.LargeScaleArbiter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TicTacToeGUI {

    private JFrame frame;
    private List<JButton> cells;

    private Clip audioClip = null;
    private List<Path> songList = List.of();
    private int currentSongIndex = 0;

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
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        startMusic();
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


    private void startMusic() {

        try (var fileStream = Files.walk(Paths.get(getClass().getResource("/").toURI()))) {
            songList = fileStream
                .filter(p -> p.toString().endsWith(".wav"))
                .collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (!songList.isEmpty()) {
            createMusicMenu();
            if (audioClip == null) playSong();
        }
    }

    private void playSong() {
        if (songList.isEmpty()) return;
        try {
            audioClip = AudioSystem.getClip();
            var input = songList.get(currentSongIndex);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(input.toFile())));
            audioClip.open(inputStream);

            FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);

            float volume = 0.25f; // 30% volume
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            audioClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMusicMenu() {

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();
        // Create a "Music" menu
        JMenu musicMenu = new JMenu("Music");
        // Music menu items
        JMenuItem playMenuItem = new JMenuItem("Play");
        playMenuItem.addActionListener(e -> {
            if (audioClip != null && !audioClip.isRunning()) {
                audioClip.start();
            }
        });
        JMenuItem pauseMenuItem = new JMenuItem("Pause");
        pauseMenuItem.addActionListener(e -> {
            if (audioClip != null && audioClip.isRunning()) {
                audioClip.stop();
            }
        });
        JMenuItem nextMenuItem = new JMenuItem("Next");
        nextMenuItem.addActionListener(e -> {
            if (audioClip == null) {
                currentSongIndex = 0;
                playSong();
            } else {
                currentSongIndex = (currentSongIndex + 1) % songList.size();
                playCurrentSongUnconditionally();
            }
        });
        JMenuItem stopMenuItem = new JMenuItem("Stop");
        stopMenuItem.addActionListener(e -> {
            if (audioClip != null) {
                audioClip.stop();
                audioClip.setMicrosecondPosition(0);
            }
        });

        musicMenu.add(playMenuItem);
        musicMenu.add(pauseMenuItem);
        musicMenu.add(nextMenuItem);
        musicMenu.add(stopMenuItem);

        // Create a "Song list" menu
        JMenu songMenu = new JMenu("Songs");
        for (int i = 0; i < songList.size(); ++i) {
            String songName = songList.get(i).getFileName().toString();
            JMenuItem menuItem = new JMenuItem(songName);
            int songIndex = i;
            menuItem.addActionListener(e -> {
                currentSongIndex = songIndex;
                playCurrentSongUnconditionally();
            });
            songMenu.add(menuItem);
        }

        menuBar.add(musicMenu);
        menuBar.add(songMenu);

        frame.setJMenuBar(menuBar);
    }

    private void playCurrentSongUnconditionally() {
        if (audioClip != null) audioClip.close();
        playSong();
    }
}
