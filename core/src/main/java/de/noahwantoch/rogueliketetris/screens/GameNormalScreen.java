package de.noahwantoch.rogueliketetris.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import de.noahwantoch.rogueliketetris.game_logic.ClassicalLogic;
import de.noahwantoch.rogueliketetris.main.GameHandler;

public class GameNormalScreen extends BaseScreen {

    private ClassicalLogic mainGameLogic;

    public GameNormalScreen(GameHandler game) {
        super(game);

        mainGameLogic = new ClassicalLogic();
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(mainGameLogic.getOuterPlayfieldColor().r, mainGameLogic.getOuterPlayfieldColor().g, mainGameLogic.getOuterPlayfieldColor().b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GameHandler.batch.begin();

        mainGameLogic.render(dt);

        GameHandler.batch.end();
    }

    @Override
    public void show() {
        mainGameLogic.show();
    }
}
