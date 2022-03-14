package model;

import view.TileView;

public class Tile extends AbstractTile {
    private boolean isOpen;
    private boolean isFlag;
    private boolean isExplosive;

    public Tile(boolean isExplosive)
    {
        this.isExplosive = isExplosive;
        isFlag = false;
        isOpen = false;
    }

    @Override
    public boolean open() {
        isOpen = true;
        return true;
    }

    @Override
    public void flag() {
        isFlag = true;
    }

    @Override
    public void unflag() {
        isFlag = false;
    }

    @Override
    public boolean isFlagged() {
        return isFlag;
    }

    @Override
    public boolean isExplosive() {
        return isExplosive;
    }

    @Override
    public boolean isOpened() {
        return isOpen;
    }

}
