package de.noahwantoch.rogueliketetris.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import de.noahwantoch.rogueliketetris.main.GameHandler;
import de.noahwantoch.rogueliketetris.screen_handling.ScreenHandler;
import de.noahwantoch.rogueliketetris.screen_handling.ScreenIdentifier;
import de.noahwantoch.rogueliketetris.ui.BackButton;
import de.noahwantoch.rogueliketetris.ui.Button;
import de.noahwantoch.rogueliketetris.ui.ButtonInputProcessor;

public class ModesScreen extends BaseScreen{

    private static final String TAG = "ModesScreen";
    private ButtonInputProcessor buttonInputProcessor;
    private BackButton backButton;
    private Button play_newRun_button;
    private Button play_classical_button;
    public ModesScreen(GameHandler game) {
        super(game);

        int button_width = Gdx.graphics.getWidth() / 5;
        int button_height = Gdx.graphics.getHeight() / 40;

        backButton = new BackButton(1);
        backButton.setAction(() -> {
            game.getScreenHandler().goBack();
        });

        play_newRun_button = new Button(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + button_height * 3, "New Run", new Texture("button.png"), 3, button_width, button_height);
        play_newRun_button.center_X();

        play_classical_button = new Button(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - button_height * 3, "Classical", new Texture("button.png"), 3, button_width, button_height);
        play_classical_button.center_X();
        play_classical_button.setAction(() -> {
            game.getScreenHandler().changeScreen(ScreenType.GAME_NORMAL);
        });

        buttonInputProcessor = new ButtonInputProcessor(backButton, play_newRun_button, play_classical_button);
        inputMultiplexer.addProcessor(buttonInputProcessor);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GameHandler.batch.begin();

        backButton.draw();
        play_newRun_button.draw();
        play_classical_button.draw();

        GameHandler.batch.end();
    }

    @Override
    public void dispose(){
        backButton.dispose();
        play_newRun_button.dispose();
        play_classical_button.dispose();
    }
}
