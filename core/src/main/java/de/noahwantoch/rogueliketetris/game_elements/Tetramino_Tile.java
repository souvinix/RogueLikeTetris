package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.noahwantoch.rogueliketetris.main.GameHandler;

public class Tetramino_Tile {
    //later, special-effects will be bound to specific tiles
    private Texture texture;
    private int x; //relative to playfield, tile-coord: 0, 1, 2, 3, 4, ...
    private int y; //relative to playfield, tile-coord: 0, 1, 2, 3, 4, ...

    public Tetramino_Tile(Texture texture){
        this.texture = texture;
    }

    public void draw(Playfield playfield){ //requires batch.begin and batch.end
        GameHandler.batch.draw(texture, playfield.getX() + x * playfield.TILE_SIZE_PX, playfield.getY() + y * playfield.TILE_SIZE_PX, playfield.TILE_SIZE_PX, playfield.TILE_SIZE_PX);
    }

    public void setX(int x){ this.x = x; }

    public void setY(int y){ this.y = y; }

    public int getX(){ return x; }
    public int getY(){ return y; }
}
