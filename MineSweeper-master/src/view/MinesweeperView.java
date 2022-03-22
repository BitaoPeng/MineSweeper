package view;

import model.Difficulty;
import model.PlayableMinesweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import notifier.IGameStateNotifier;

public class MinesweeperView implements IGameStateNotifier {
    public static final int TILE_SIZE = 50;
    public static final class AssetPath {
        public static final String CLOCK_ICON = "./src/view/clock.png";
        public static final String FLAG_ICON = "./src/view/flag.png";
        public static final String BOMB_ICON = "./src/view/bomb.png";
    }
    private PlayableMinesweeper gameModel;
    private final JFrame window;
    private TileView[][] tiles;
    private final JPanel world = new JPanel();
    private final JLabel timerView = new JLabel();
    private final JLabel flagCountView = new JLabel();
    private final JPanel game = new JPanel();

    public MinesweeperView() {
        this.window = new JFrame("Minesweeper");
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new FlowLayout());
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("New Game");
        menuBar.add(gameMenu);

        JMenuItem easyGame = new JMenuItem("Easy");
        gameMenu.add(easyGame);
        easyGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null) 
                gameModel.startNewGame(Difficulty.EASY);
        });
        JMenuItem mediumGame = new JMenuItem("Medium");
        gameMenu.add(mediumGame);
        mediumGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.MEDIUM);
        });
        JMenuItem hardGame = new JMenuItem("Hard");
        gameMenu.add(hardGame);
        hardGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.HARD);
        });
        
        this.window.setJMenuBar(menuBar);

        try {
            JLabel clockIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.CLOCK_ICON))));
            clockIcon.setSize(new DimensionUIResource(10, 10));
            timerPanel.add(clockIcon);
            timerPanel.add(new JLabel("TIME ELAPSED: "));
            timerPanel.add(this.timerView);
        } catch (IOException e) {
            System.out.println("Unable to locate clock resource");
        }
        JPanel flagPanel = new JPanel();
        flagPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        try {
            JLabel clockIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.FLAG_ICON))));
            clockIcon.setSize(new DimensionUIResource(10, 10));
            flagPanel.add(clockIcon);
            flagPanel.add(new JLabel("FLAG: "));
            flagPanel.add(this.flagCountView);
        } catch (IOException e) {
            System.out.println("Unable to locate flag resource");
        }

        this.window.setLayout(new GridBagLayout());
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        this.window.add(timerPanel, layoutConstraints);
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 0;
        this.window.add(flagPanel, layoutConstraints);
        layoutConstraints.fill = GridBagConstraints.BOTH;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 1.0;
        this.window.add(world, layoutConstraints);
        this.window.setSize(500, 1);
        this.window.setVisible(true);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.world.setVisible(true);
    }

    public void setGameModel(PlayableMinesweeper newGameModel) {
        this.gameModel = newGameModel;
        this.gameModel.setGameStateNotifier(this);
    }

    @Override
    public void notifyNewGame(int row, int col) {
        this.flagCountView.setText("0");
        this.window.setSize(col * TILE_SIZE, row * TILE_SIZE + 30);
        this.world.removeAll();
        
        this.tiles = new TileView[row][col];
        for (int i=0; i<row; ++i) {
            for (int j=0; j<col; ++j) {
                TileView temp = new TileView(i, j);
                temp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent arg0) {
                        if (arg0.getButton() == MouseEvent.BUTTON1){
                            if (gameModel!=null)
                                gameModel.open(temp.getPositionX(), temp.getPositionY());
                        } 
                        else if (arg0.getButton() == MouseEvent.BUTTON3) {
                            if (gameModel!=null)
                                gameModel.toggleFlag(temp.getPositionX(), temp.getPositionY());
                        } 
                    }
                });
                this.tiles[i][j] = temp;
                this.world.add(temp);
            }
        }
        this.world.setLayout(new GridLayout(row, col));
        this.world.setVisible(false);
        this.world.setVisible(true);
        this.world.repaint();
    }
    @Override
    public void notifyGameLost() {
        this.removeAllTileEvents();
        JOptionPane.showMessageDialog(game, "Bomb! You Lost the game!", "Lost", JOptionPane.ERROR_MESSAGE);
        throw new UnsupportedOperationException();
    }
    @Override
    public void notifyGameWon() {
        this.removeAllTileEvents();
        JOptionPane.showMessageDialog(game, "Congratulations! You win the game!", "Win", JOptionPane.INFORMATION_MESSAGE);
        throw new UnsupportedOperationException();
    }

    private void removeAllTileEvents() {
        for (TileView[] tile : this.tiles)
            for (TileView tileView : tile) tileView.removalAllMouseListeners();
    }

    @Override
    public void notifyFlagCountChanged(int newFlagCount) {
        this.flagCountView.setText(Integer.toString(newFlagCount));
    }

    @Override
    public void notifyTimeElapsedChanged(Duration newTimeElapsed){
        timerView.setText(
                    String.format("%d:%02d", newTimeElapsed.toMinutesPart(), newTimeElapsed.toSecondsPart()));
        
    }

    @Override
    public void notifyOpened(int x, int y, int explosiveNeighbourCount) {
        this.tiles[x][y].notifyOpened(explosiveNeighbourCount);
    }

    @Override
    public void notifyFlagged(int x, int y) {
        this.tiles[x][y].notifyFlagged();
    }

    @Override
    public void notifyUnflagged(int x, int y) {
        this.tiles[x][y].notifyUnflagged();
    }

    @Override
    public void notifyExploded(int x, int y) {
        this.tiles[x][y].notifyExplode();
    }

}