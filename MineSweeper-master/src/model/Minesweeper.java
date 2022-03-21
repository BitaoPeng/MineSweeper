package model;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Minesweeper extends AbstractMineSweeper{

    private int row;
    private int col;
    private int explosionCount;
    private AbstractTile[][] world;
    private int openNumber;
    private int flagNumber;
    private boolean end;
    private LocalTime startTime;

    public Minesweeper() {
    }

    public Duration setTimer(){
        return Duration.between(startTime, LocalTime.now());
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
                startNewGame(8,8,10);

            case MEDIUM:
                startNewGame(16, 16, 40);

            case HARD:
                startNewGame(10, 10, 2);

        }
    }

    @Override
    public void startNewGame(int row, int col, int explosionCount) {

        this.row = row;
        this.col = col;
        this.explosionCount = explosionCount;
        this.world = new Tile[this.row][this.col];
        this.openNumber = 0;
        this.flagNumber = 0;

        this.end = false;

        int count = 0;

        startTime = LocalTime.now();
        Runnable R = () -> {
            while (!end) {
                viewNotifier.notifyTimeElapsedChanged(setTimer());
            }
        };
        new Thread(R).start();


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

        viewNotifier.notifyNewGame(row, col);
    }

    public int CountExplosiveNeighbour(int x, int y){
        int explosiveNeighbourCount = 0;

        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                //判断是否为相邻的格子，只有格子存在并且相邻才去判断是否为炸弹格子
                if(x - 1 <= i && i <= x + 1 && y - 1 <= j && j <= y + 1){
                    if(world[i][j].isExplosive()){
                        explosiveNeighbourCount++;
                    }
                }
            }
        }
        return explosiveNeighbourCount;
    }

    @Override
    public void toggleFlag(int x, int y) {
        if(!world[x][y].isOpened()){
            if(world[x][y].isFlagged()){
                unflag(x,y);
            }
            else {
                flag(x,y);
            }
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
        //必须要是空格的状态才打开，不然会陷入来回打开的死循环！！！！！！！！
        if(!world[x][y].isOpened()){
            //1.立了旗的空格不能被打开
            if(!world[x][y].isFlagged()){
                world[x][y].open();
                openNumber++;
                //2.如果点开了炸弹
                if(world[x][y].isExplosive()){
                    //3.如果是第一次点开炸弹
                    if(openNumber == 1){
                        startNewGame(row, col, explosionCount);
                        open(x,y);
                    }
                    else{
                        end = true;
                        viewNotifier.notifyExploded(x,y);
                        viewNotifier.notifyGameLost();
                    }
                }
                else{
                    viewNotifier.notifyOpened(x,y,CountExplosiveNeighbour(x,y));
                    if(CountExplosiveNeighbour(x,y)==0){
                        openNeighbour(x,y);
                    }
                }
            }
            if(openNumber == row * col - explosionCount){
                for(int i=0; i<row; i++){
                    for(int j=0; j<col; j++){
                        if(!world[i][j].isOpened()){
                            world[i][j].flag();
                            viewNotifier.notifyFlagged(i,j);
                        }

                    }
                }
                end = true;
                viewNotifier.notifyGameWon();
            }
        }
    }


    public void openNeighbour(int x, int y){
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(x - 1 <= i && i <= x + 1 && y - 1 <= j && j <= y + 1){
                    open(i,j);
                }
            }
        }
    }


    @Override
    public void flag(int x, int y) {
        world[x][y].flag();
        viewNotifier.notifyFlagged(x, y);
        this.flagNumber++;
        viewNotifier.notifyFlagCountChanged(flagNumber);
    }

    @Override
    public void unflag(int x, int y) {
        world[x][y].unflag();
        viewNotifier.notifyUnflagged(x, y);
        this.flagNumber--;
        viewNotifier.notifyFlagCountChanged(flagNumber);
    }

    @Override
    public void deactivateFirstTileRule() {
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
