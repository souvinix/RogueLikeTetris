package de.noahwantoch.rogueliketetris.game_logic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class Controller implements InputProcessor {
    private static final String TAG = "Controller";

    private boolean rotateTriggered;
    private boolean rotateHeld;
    private boolean isMovingDown;
    private boolean isMovingLeftHeld;
    private boolean isMovingLeftTriggered;
    private boolean isMovingRightHeld;
    private boolean isMovingRightTriggered;

    private boolean hardDrop;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP && !rotateHeld) {
            rotateTriggered = true; //Rotation
            rotateHeld = true;
        }
        if(keycode == Input.Keys.DOWN){
            isMovingDown = true;
        }
        if(keycode == Input.Keys.LEFT && !isMovingLeftHeld){
            isMovingLeftTriggered = true;
            isMovingLeftHeld = true;
        }
        if(keycode == Input.Keys.RIGHT && !isMovingRightHeld){
            isMovingRightTriggered = true;
            isMovingRightHeld = true;
        }
        if(keycode == Input.Keys.SPACE){
            hardDrop = true;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP){ //rotating
            rotateHeld = false;
        }
        if(keycode == Input.Keys.DOWN){
            isMovingDown = false;
        }
        if(keycode == Input.Keys.LEFT){
            isMovingLeftHeld = false;
        }
        if(keycode == Input.Keys.RIGHT){
            isMovingRightHeld = false;
        }
        if(keycode == Input.Keys.SPACE){
            hardDrop = false;
        }
        return true;
    }

    public boolean isRotationTriggered(){
        return rotateTriggered;
    }

    public void resetRotationTriggered(){
        rotateTriggered = false;
    }
    public void resetLeftTriggered(){
        isMovingLeftTriggered = false;
    }
    public void resetRightTriggered() {
        isMovingRightTriggered = false;
    }


    public boolean isMovingLeftTriggered(){ return isMovingLeftTriggered; }
    public boolean isMovingRightTriggered(){ return isMovingRightTriggered; }

    public boolean isMovingDown(){
        return isMovingDown;
    }
    public boolean isMovingLeftHeld(){
        return isMovingLeftHeld;
    }
    public boolean isMovingRightHeld(){
        return isMovingRightHeld;
    }
    public boolean hardDrop(){
        return hardDrop;
    }









    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
