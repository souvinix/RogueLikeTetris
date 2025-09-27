package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import de.noahwantoch.rogueliketetris.main.GameHandler;

public class VariableFont {
    private String text;
    private BitmapFont font;
    private GlyphLayout glyphLayout = new GlyphLayout();

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    private int x;
    private int y;

    private final int size;

    public VariableFont(String text, int x, int y, int size){
        this.text = text;
        this.x = x;
        this.y = y;
        this.size = size;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = new Color(1, 1, 1, 1);

        font = generator.generateFont(parameter);
        font.getData().setScale(1f);

        //Für bessere Skalierungsqualität
        if (font != null) {
            font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        glyphLayout.setText(font, text);
    }

    public void draw(){ //batch.begin is required
       font.draw(GameHandler.batch, text, x, y);
    }

    public void dispose(){

    }

    public void setText(){

    }
}
