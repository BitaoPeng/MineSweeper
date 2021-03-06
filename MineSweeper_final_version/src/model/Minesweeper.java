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
    private boolean timeStop;
    private LocalTime startTime;

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
            case EASY -> {
                startNewGame(8,8,10);
            }
            case MEDIUM -> {
                startNewGame(16, 16, 40);
            }
            case HARD -> {
                startNewGame(16, 30, 2);
            }
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
        this.timeStop = false;

        startTime = LocalTime.now();//程序开始
        Runnable R = () -> {//R是线程的接口
            while (!timeStop) {
                viewNotifier.notifyTimeElapsedChanged(setTimer());
            }
        };
        new Thread(R).start();//创建线程（相当于创建一个子程序，可以和后面的程序一起进行）

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
            for(int j = 0; j < col; j++){
                if(world[i][j] == null)
                    world[i][j] = generateEmptyTile();
            }
        }

        viewNotifier.notifyNewGame(row, col);
    }

    public Duration setTimer(){
        return Duration.between(startTime, LocalTime.now());
    }

    public int CountExplosiveNeighbour(int x, int y){
        int explosiveNeighbourCount=0;
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                //判断是否为相邻的格子，只有格子存在并且相邻才去判断是否为炸弹格子
                if(x-1<=i && i<=x+1 && y-1<=j && j<=y+1){
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
        //1.必须要是空格的状态才打开，不然会陷入来回打开的死循环！！！！！！！！
        if(!world[x][y].isOpened()){
            //2.立了旗的空格不能被打开
            if(!world[x][y].isFlagged()){
                world[x][y].open();
                openNumber++;
                //3.如果点开了炸弹
                if(world[x][y].isExplosive()){
                    //4.如果是第一次点开炸弹
                    if(openNumber==1){
                        startNewGame(row, col, explosionCount);
                        open(x,y);
                    }
                    else{//游戏失败
                        timeStop=true;
                        //把剩余全部的炸弹展示出来
                        for(int i=0; i<row; i++){
                            for(int j=0; j<col; j++){
                                if(world[i][j].isExplosive()){
                                    viewNotifier.notifyExploded(i,j);
                                }
                            }
                        }
                        viewNotifier.notifyGameLost();
                    }
                }
                else{//如果点开的是空格子，打开邻居炸弹
                    viewNotifier.notifyOpened(x,y,CountExplosiveNeighbour(x,y));
                    if(CountExplosiveNeighbour(x,y)==0){
                        openNeighbour(x,y);
                    }
                }
            }
            //判断游戏是否胜利
            isWining();
        }
    }

    public void isWining(){
        if(openNumber == row * col - explosionCount){
            for(int i=0; i<row; i++){
                for(int j=0; j<col; j++){
                    if(!world[i][j].isOpened()){
                        world[i][j].flag();
                        viewNotifier.notifyFlagged(i,j);
                    }
                }
            }
            timeStop=true;
            viewNotifier.notifyGameWon();
        }
    }

    public void openNeighbour(int x, int y){
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                if(x-1<=i && i<=x+1 && y-1<=j && j<=y+1){
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
