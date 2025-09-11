package de.noahwantoch.rogueliketetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import de.noahwantoch.rogueliketetris.main.GameHandler;

public class Button {
    private float x;
    private float y;
    public int width;
    public int height;
    private String text;
    private Texture texture;
    private TextureRegion[] textureRegions;
    private float size;
    private int currentState = 0; //0 = not pressed, 1 = pressed
    private boolean isDisposed = false;

    private BitmapFont font;
    private static GlyphLayout glyphLayout = new GlyphLayout();
    private float textPressedOffset = 1.5f; //pixel (* size) the text travels down when pressed
    private float textOffset = 2f; //General downward offset (because of the shadow below)
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private ButtonAction action;
    public Button(float x, float y, String text, Texture texture, float size, int width, int height){
        this.x = x;
        this.y = y;
        this.text = text;
        this.texture = texture;
        this.size = size;
        this.width = width;
        this.height = height;
        action = null;

        textureRegions = new TextureRegion[2];
        textureRegions[0] = new TextureRegion(texture, 0, 0, texture.getWidth() / 2, texture.getHeight());
        textureRegions[1] = new TextureRegion(texture, texture.getWidth() / 2, 0, texture.getWidth() / 2, texture.getHeight());

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (1f / 3f * getVisualButtonHeight());
        parameter.color = new Color(0, 0, 0, 1);

        font = generator.generateFont(parameter);
        font.getData().setScale(1f);

        //Für bessere Skalierungsqualität
        if (font != null) {
            font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    public void draw(){
        if(!isDisposed){
            GameHandler.batch.draw(textureRegions[currentState], x, y, 0, 0, width, height, size, size, 0);

            glyphLayout.setText(font, text); //needs to be called everytime before font is drawn
            if(currentState == 0){
                font.draw(GameHandler.batch, text, x + getVisualButtonWidth() / 2 - glyphLayout.width / 2, y + getVisualButtonHeight() / 2 + glyphLayout.height - textOffset * size);
            }else{
                font.draw(GameHandler.batch, text, x + getVisualButtonWidth() / 2 - glyphLayout.width / 2, y + getVisualButtonHeight() / 2 + glyphLayout.height - textPressedOffset * size - textOffset * size);
            }
        }
    }

    public void center_X(){
        x = x - getVisualButtonWidth() / 2;
    }

    @FunctionalInterface
    public interface ButtonAction{
        void execute();
    }

    public void setAction(ButtonAction action){ this.action = action; }
    public void executeAction(){
        if(action != null){
            action.execute();
        }
    }

    public void dispose(){
        isDisposed = true;
        texture.dispose();
        font.dispose();
        generator.dispose();
    }

    public float getY(){ return y; }
    public float getX(){ return x; }

    public int getWidth(){ return width; }
    public int getHeight(){ return height; }
    public int getVisualButtonWidth(){ return (int) (width * size); }
    public int getVisualButtonHeight(){ return (int) (height * size); }

    public String getText(){ return text; }

    public void setCurrentState(int state){
        if(state == 0 || state == 1){
            currentState = state;
        }
    }
}

