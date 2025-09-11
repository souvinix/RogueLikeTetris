package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import de.noahwantoch.rogueliketetris.main.GameHandler;

public class Playfield {
    //a689d7 outer color of playfield
    private static final String TAG = "Playfield";
    private static final int OFFSET_TO_SIDES = Gdx.graphics.getWidth() / 20;
    public static final int WIDTH_TILES = 10;
    public static final int HEIGHT_TILES = 18;
    public static final int WIDTH_PX = Gdx.graphics.getWidth() - 2 * OFFSET_TO_SIDES;
    public static final int HEIGHT_PX = (int) (WIDTH_PX * 1.8f);

    public static final int TILE_SIZE_PX = WIDTH_PX / WIDTH_TILES;

    private Texture borderTexture;

    public Playfield(Texture borderTexture){
        this.borderTexture = borderTexture;
    }

    public void draw(){ //requires batch.begin and batch.end
        GameHandler.batch.draw(borderTexture, OFFSET_TO_SIDES, OFFSET_TO_SIDES, WIDTH_PX, HEIGHT_PX);
    }

    public int getX(){
        return OFFSET_TO_SIDES;
    }
    public int getY(){
        return OFFSET_TO_SIDES;
    }
}
