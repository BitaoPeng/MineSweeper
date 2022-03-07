package model;
import java.util.*;

public class Minesweeper extends AbstractMineSweeper{

    private int row;
    private int col;
    private int explosionCount;
    private boolean[][] world;

    public Minesweeper()
    {

    }

    @Override
    public int getWidth() {
        return col;
    }

    @Override
    public int getHeight() {
        return row;
    }

    @Override
    public void startNewGame(Difficulty level) {
        switch (level)
        {
            case EASY:
                startNewGame(8, 8, 10);
                break;
            case  MEDIUM:
                startNewGame(16,16,40);
                break;
            case HARD:
                startNewGame(16,30,99);
        }
    }

    @Override
    public void startNewGame(int row, int col, int explosionCount) {
        //try arraylist

        //designed for getters
        this.row = row;
        this.col = col;
        this.explosionCount = explosionCount;

        //make explosionCount random numbers
        int upperBound = row * col - 1;
        int [] explosion = new int[explosionCount];
        for(int i = 0; i < explosionCount; i++)
        {
            explosion[i] = (int) (Math.random() * upperBound);

            for(int j = 0; j < explosionCount; j++)
            {
                while (explosion[i] == explosion[j])
                {
                    explosion[i] = (int) (Math.random() * upperBound);
                }
            }
        }

        //match every random number into element of array
        world = new boolean[row][col];

        for(int k = 0; k < explosionCount; k++)
        {
            int r = explosion[k] / col;
            int c = explosion[k] - r * col;
            world[r][c] = true;
        }

    }

    @Override
    public void toggleFlag(int x, int y) {

    }

    @Override
    public AbstractTile getTile(int x, int y) {
        return null;
    }

    @Override
    public void setWorld(AbstractTile[][] world) {

    }

    @Override
    public void open(int x, int y) {

    }

    @Override
    public void flag(int x, int y) {

    }

    @Override
    public void unflag(int x, int y) {

    }

    @Override
    public void deactivateFirstTileRule() {

    }

    @Override
    public AbstractTile generateEmptyTile() {
        return null;
    }

    @Override
    public AbstractTile generateExplosiveTile() {
        return null;
    }


}
