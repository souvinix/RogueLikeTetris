package de.noahwantoch.rogueliketetris.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import de.noahwantoch.rogueliketetris.main.GameHandler;
import de.noahwantoch.rogueliketetris.screen_handling.ScreenHandler;
import de.noahwantoch.rogueliketetris.screen_handling.ScreenIdentifier;
import de.noahwantoch.rogueliketetris.ui.Button;
import de.noahwantoch.rogueliketetris.ui.ButtonInputProcessor;

public class MenuScreen extends BaseScreen{

    private static final String TAG = "MenuScreen";

    private Button modes_button; //--> new Run (Roguelike), normal-mode (normales Tetris)
    private Button options_button;
    private Button shop_button;
    private Button leaderboards_button;
    private Button exit_button;
    private Button[] buttons;
    private ButtonInputProcessor buttonInputProcessor;

    private int button_range = Gdx.graphics.getHeight() / 10;
    public MenuScreen(GameHandler game){
        super(game);
    }

    @Override
    public void show(){
        super.show();

        int button_width = Gdx.graphics.getWidth() / 5;
        int button_height = Gdx.graphics.getHeight() / 40;

        modes_button = new Button(Gdx.graphics.getWidth() / 2, button_range * 5, "Modes", new Texture("button.png"), 3, button_width, button_height);
        modes_button.center_X();
        modes_button.setAction(() -> {
            game.getScreenHandler().changeScreen(ScreenIdentifier.ScreenType.MODES);
        });

        options_button = new Button(Gdx.graphics.getWidth() / 2, button_range * 4, "Options", new Texture("button.png"), 3, button_width, button_height);
        options_button.center_X();
        shop_button = new Button(Gdx.graphics.getWidth() / 2, button_range * 3, "Shop", new Texture("button.png"), 3, button_width, button_height);
        shop_button.center_X();
        leaderboards_button = new Button(Gdx.graphics.getWidth() / 2, button_range * 2, "Leaderboard", new Texture("button.png"), 3, button_width, button_height);
        leaderboards_button.center_X();
        exit_button = new Button(Gdx.graphics.getWidth() / 2, button_range * 1, "Exit", new Texture("button.png"), 3, button_width, button_height);
        exit_button.center_X();
        exit_button.setAction(() -> {
            Gdx.app.debug(TAG, "Exit button pressed");
            Gdx.app.exit();
        });

        buttons = new Button[]{modes_button, options_button, shop_button, leaderboards_button, exit_button};

        buttonInputProcessor = new ButtonInputProcessor(buttons);
        inputMultiplexer.addProcessor(buttonInputProcessor);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GameHandler.batch.begin();

        for(Button button : buttons){
            button.draw();
        }

        GameHandler.batch.end();
    }

    @Override
    public void dispose(){
        for(Button button : buttons){
            button.dispose();
        }
    }
}
