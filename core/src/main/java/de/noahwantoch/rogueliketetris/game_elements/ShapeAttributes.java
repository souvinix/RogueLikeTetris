package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.graphics.Texture;

public class ShapeAttributes {
    public final Texture tileTexture;
    public final int rotationType; //1, 2 or 4

    public ShapeAttributes(Texture tileTexture, int rotationType) {
        this.tileTexture = tileTexture;
        this.rotationType = rotationType;
    }
}
