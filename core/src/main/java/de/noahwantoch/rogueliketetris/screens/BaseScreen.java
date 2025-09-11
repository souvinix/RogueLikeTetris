package de.noahwantoch.rogueliketetris.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.noahwantoch.rogueliketetris.main.GameHandler;
import de.noahwantoch.rogueliketetris.screen_handling.ScreenIdentifier;
import de.noahwantoch.rogueliketetris.ui.ButtonInputProcessor;


//Gamefield: 10 * 18 tiles
//10 * 32 = 320
//18 * 32 = 576
//UI beside/below/above = ...
public abstract class BaseScreen extends ScreenAdapter implements ScreenIdentifier {
    protected static String TAG = "BaseScreen";
    protected static final int vp_width = 540;
    protected static final int vp_height = 960;
    protected final GameHandler game;
    protected final OrthographicCamera cam = new OrthographicCamera();
    protected final FitViewport vp = new FitViewport(vp_width, vp_height, cam);
    protected InputMultiplexer inputMultiplexer;
    BaseScreen(GameHandler game){
        this.game = game;
        cam.position.set(vp_width / 2, vp_height,0);
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        GameHandler.batch.setProjectionMatrix(cam.combined);

        inputMultiplexer = new InputMultiplexer();

        Gdx.input.setInputProcessor(inputMultiplexer);
    }
    public void resize(int w,int h){
        vp.update(w,h,true);
    }

    @Override
    public void show(){
        Gdx.app.debug(TAG, "Showing current ScreenType: " + getType(this));
    }

    @Override
    public void dispose(){
        GameHandler.batch.dispose();
    }

}
