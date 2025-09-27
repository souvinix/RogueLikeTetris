package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.graphics.Texture;

import de.noahwantoch.rogueliketetris.main.GameHandler;

public class Tetramino_Tile {
    //later, special-effects will be bound to specific tiles
    private final Texture texture;
    private int x; //relative to playfield, tile-coord: 0, 1, 2, 3, 4, ...
    private int y; //relative to playfield, tile-coord: 0, 1, 2, 3, 4, ...

    public Tetramino_Tile(Texture texture){
        this.texture = texture;
    }

    public void draw(Playfield playfield){ //requires batch.begin and batch.end
        GameHandler.batch.draw(texture, playfield.getX() + x * Playfield.TILE_SIZE_PX, playfield.getY() + y * Playfield.TILE_SIZE_PX, Playfield.TILE_SIZE_PX, Playfield.TILE_SIZE_PX);
    }

    public void setPosition(int x, int y){
        setX(x);
        setY(y);
    }

    public void setX(int x){ this.x = x; }

    public void setY(int y){ this.y = y; }

    public int getX(){ return x; }
    public int getY(){ return y; }
}
