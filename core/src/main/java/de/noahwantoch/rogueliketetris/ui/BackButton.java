package de.noahwantoch.rogueliketetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.noahwantoch.rogueliketetris.main.GameHandler;

public class BackButton extends Button{

    private static final String TAG = "BackButton";
    private static final int OFFSET_XY = 10;
    private static final int SIZE_PX = 40;

    public BackButton(float size) {
        this(OFFSET_XY, Gdx.graphics.getHeight() - OFFSET_XY - (SIZE_PX * size), SIZE_PX, SIZE_PX, size);
    }

    public BackButton(float x, float y, int width, int height, float size) {
        super(x, y, "", new Texture("backbutton.png"), size, SIZE_PX, SIZE_PX);
    }


}
