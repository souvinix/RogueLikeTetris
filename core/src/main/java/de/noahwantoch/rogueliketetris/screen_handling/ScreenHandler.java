package de.noahwantoch.rogueliketetris.screen_handling;

import com.badlogic.gdx.Gdx;

import java.util.Deque;
import java.util.LinkedList;

import de.noahwantoch.rogueliketetris.main.GameHandler;
import de.noahwantoch.rogueliketetris.screens.MenuScreen;

public class ScreenHandler {
    private Deque<ScreenIdentifier.ScreenType> history;
    private GameHandler game;
    public ScreenHandler(GameHandler game){
        this.game = game;

        history = new LinkedList<ScreenIdentifier.ScreenType>();
        history.push(ScreenIdentifier.ScreenType.MENU); //begins with the menu
    }
    public void changeScreen(ScreenIdentifier.ScreenType screenType){
        game.setScreen(ScreenIdentifier.getScreen(screenType, game));
        history.push(screenType);
    }

    public void setGame(GameHandler game){
        this.game = game;
    }

    public void goBack(){
        Gdx.app.debug("ScreenHandler", "Going back");

        if(history.size() > 1){
            history.pop();
            changeScreen(history.getFirst());
            Gdx.app.debug("ScreenHandler", "history was popped");
        }
    }
}
