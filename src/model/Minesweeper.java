package model;
import java.util.*;

public class Minesweeper extends AbstractMineSweeper{

    private int row;
    private int col;
    private int explosionCount;
    private AbstractTile[][] world;

    public Minesweeper() {

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

        this.row = row;
        this.col = col;
        this.explosionCount = explosionCount;

        int count = 0;

        //generate two random number at the same time(row number, column number), set the selected tile as explosive one
        Random random = new Random();
        //int[][] index = new int[explosionCount][explosionCount];
        while (count < explosionCount){
            int r = random.nextInt(row);
            int c = random.nextInt(col);
            if(world[r][c] == null){
                world[r][c] = generateExplosiveTile();
                count++;
            }
        }

        for(int i = 0; i < row; i++){
            for(int j =0; j < col; j++){
                if(world[i][j] == null)
                    world[i][j] = generateEmptyTile();
            }
        }

    }

    @Override
    public void toggleFlag(int x, int y) {
        if(world[x][y].isFlagged() == true){
            world[x][y].unflag();
        }
        else {
            world[x][y].flag();
        }
    }

    @Override
    public AbstractTile getTile(int x, int y) {
        return world[x][y];
    }

    @Override
    public void setWorld(AbstractTile[][] world) {
        this.world = world;
    }

    @Override
    public void open(int x, int y) {
        world[x][y].open();
    }

    @Override
    public void flag(int x, int y) {
        world[x][y].flag();
    }

    @Override
    public void unflag(int x, int y) {
        world[x][y].unflag();
    }

    @Override
    public void deactivateFirstTileRule() {
        int count = 0;
        for(int i = 0; i < row; i++){
            for(int j =0; j < col; j++){
                if(world[i][j].isOpened() == false){
                    count++;
                }
            }
        }
        if(count == row * col - 1){
            if()
        }
    }

    @Override
    public AbstractTile generateEmptyTile() {
        return new Tile(false);
    }

    @Override
    public AbstractTile generateExplosiveTile() {
        return new Tile(true);
    }


}
