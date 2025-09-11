package de.noahwantoch.rogueliketetris.main;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.noahwantoch.rogueliketetris.screen_handling.ScreenHandler;
import de.noahwantoch.rogueliketetris.screen_handling.ScreenIdentifier;
import de.noahwantoch.rogueliketetris.screens.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameHandler extends Game {
    private static final String TAG = "Main";
    public static SpriteBatch batch;
    public ScreenHandler screenHandler;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        batch = new SpriteBatch();

        //setScreen(new MenuScreen(this));

        screenHandler = new ScreenHandler(this);
        screenHandler.changeScreen(ScreenIdentifier.ScreenType.MENU);
        Gdx.app.debug(TAG, "GameHandler was created");

        Gdx.app.debug("GAME", "Game was created");
    }

    @Override
    public void render(){
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public ScreenHandler getScreenHandler(){
        return screenHandler;
    }
}
