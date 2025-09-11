package de.noahwantoch.rogueliketetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class ButtonInputProcessor implements InputProcessor {

    private static final String TAG = "InputHandler";
    private Button[] buttons;

    private Button currentButtonFocused = null;

    public ButtonInputProcessor(Button... buttons){
        this.buttons = buttons;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY; //transformed y
        for(int i = 0; i < buttons.length; i++){
            if(screenX >= buttons[i].getX() && screenX <= buttons[i].getX() + buttons[i].getVisualButtonWidth() && screenY >= buttons[i].getY() && screenY <= buttons[i].getY() + buttons[i].getVisualButtonHeight()){
                Gdx.app.debug(TAG, "Button pressed: " + buttons[i].getText());
                buttons[i].setCurrentState(1);
                currentButtonFocused = buttons[i];
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(currentButtonFocused != null){
            //Funktion des Buttons mit delay
            Gdx.app.debug(TAG, "Button released: " + currentButtonFocused.getText());
            currentButtonFocused.setCurrentState(0);
            currentButtonFocused.executeAction();
            currentButtonFocused = null;
            return true;
        }
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
