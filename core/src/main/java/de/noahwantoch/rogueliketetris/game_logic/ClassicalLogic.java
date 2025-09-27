package de.noahwantoch.rogueliketetris.game_logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Formatter;
import java.util.Map;

import de.noahwantoch.rogueliketetris.game_elements.ClassicalLevelAttributes;
import de.noahwantoch.rogueliketetris.game_elements.Playfield;
import de.noahwantoch.rogueliketetris.game_elements.ShapeAttributes;
import de.noahwantoch.rogueliketetris.game_elements.Tetramino;
import de.noahwantoch.rogueliketetris.game_elements.Tetramino_Tile;
import de.noahwantoch.rogueliketetris.game_elements.VariableFont;

public class ClassicalLogic {
    //The logic of the main elements of this game
    //Classical (Tetris) and RogueLikeTetris will extend from here
    //
    //What elements does both modes share? :
    //Falling Blocks, Rotating blocks, fast bottom hit, Score, Level, Lines, GameOver after specific condiftion, Pause, Restart, Save, Load, Playfield (10x18)
    private static final String TAG = "ClassicalLogic";

    private static final float MOVING_SPEED_VERTICAL = 0.03f; //Every ... seconds
    private static final float MOVING_SPEED_HORIZONTAL = 0.04f; //Every ... seconds
    private static final float MOVING_SPEED_HORIZONTAL_DELAY = 0.25f; //time to pass after left/right is pressed to being "held"
    protected Playfield playfield;
    protected Color outerPlayfieldColor;
    protected Tetramino currentTetramino; //current falling tetramino (movable)
    protected Tetramino_Tile[][] frozenTiles; //tiles laying on the ground (not movable)

    protected int currentLevel = 0;
    protected float currentSpeed;
    protected int score = 0;
    protected int lines = 0;
    protected boolean gameOver = false;
    protected boolean paused = false;
    protected boolean started = false;

    private float tickCounter = 0;
    private float speedCounter_vertical = 0;
    private float speedCounter_horizontal = 0;
    private float delayCounter_horizontal = 0;

    private VariableFont lines_font;
    private VariableFont score_font;
    private VariableFont level_font;

    protected Controller controller;

    protected static final Map<Tetramino.Shape, ShapeAttributes> ATTRIBUTES_MAP = new EnumMap<>(Tetramino.Shape.class);;
    static {
        ATTRIBUTES_MAP.put(Tetramino.Shape.L_SHAPED, new ShapeAttributes(new Texture("darkblue_tile.png"), 4));
        ATTRIBUTES_MAP.put(Tetramino.Shape.J_SHAPED, new ShapeAttributes(new Texture("purple_tile.png"), 4));
        ATTRIBUTES_MAP.put(Tetramino.Shape.O_SHAPED, new ShapeAttributes(new Texture("red_tile.png"), 1));
        ATTRIBUTES_MAP.put(Tetramino.Shape.T_SHAPED, new ShapeAttributes(new Texture("yellow_tile.png"), 4));
        ATTRIBUTES_MAP.put(Tetramino.Shape.S_SHAPED, new ShapeAttributes(new Texture("cyan_tile.png"), 2));
        ATTRIBUTES_MAP.put(Tetramino.Shape.Z_SHAPED, new ShapeAttributes(new Texture("green_tile.png"), 2));
        ATTRIBUTES_MAP.put(Tetramino.Shape.I_SHAPED, new ShapeAttributes(new Texture("orange_tile.png"), 2));
    }
    public ClassicalLogic(){}

    public void show(){
        frozenTiles = new Tetramino_Tile[Playfield.HEIGHT_TILES][Playfield.WIDTH_TILES];

        playfield = new Playfield(new Texture("playfield_01.png")); //outer: a689d7
        outerPlayfieldColor = Color.valueOf("a689d7");

        controller = new Controller();
        Gdx.input.setInputProcessor(controller);

        startGame();

        lines_font = new VariableFont("Lines: " + lines, 0, Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/30, 1);
        score_font = new VariableFont("Score: " + score, 0, Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/28, 1);
        level_font = new VariableFont("Level: " + currentLevel, 0, Gdx.graphics.getHeight() - Gdx.graphics.getHeight()/27, 1);
    }

    public void render(float dt) { //requires batch.begin and batch.end
        tickCounter += dt;
        playfield.draw();
        currentTetramino.draw(playfield);

        if(tickCounter >= ClassicalLevelAttributes.LEVEL_SPEEDS[currentLevel]){
            tick();
        }else{
            checkCollision(dt); //+ input-handling
        }

        drawFrozenTiles();

        lines_font.draw();
        score_font.draw();
        level_font.draw();
    }

    private void drawFrozenTiles(){
        for(int i = 0; i < frozenTiles.length; i++){
            for(int j = 0; j < frozenTiles[i].length; j++){
                if(frozenTiles[i][j] != null){
                    frozenTiles[i][j].draw(playfield);
                }
            }
        }
    }


    private boolean isRotationCollidingWithTiles(){
        Tetramino_Tile[][] nextRotationState = currentTetramino.getTiles();
        for(int i = 0; i < nextRotationState.length; i++){
            for(int j = 0; j < nextRotationState[i].length; j++){
                if(nextRotationState[i][j] != null){
                    if(frozenTiles[currentTetramino.getY() + (3 - j)][currentTetramino.getX() + i] != null){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isRotationColliding(){
        Tetramino_Tile[][] nextState_tiles = currentTetramino.getNextRotationState();
        for(int i = 0; i < nextState_tiles.length; i++){
            for(int j = 0; j < nextState_tiles[i].length; j++){
                Tetramino_Tile tile = nextState_tiles[i][j];
                if(tile != null){
                    if(currentTetramino.getX() + j < 0 || currentTetramino.getX() + j > Playfield.WIDTH_TILES - 1 || currentTetramino.getY() + (3 - i) < 0){
                        return true;
                    }
                }
            }
        }
        return isRotationCollidingWithTiles();
    }

    //checking for collision with other tiles ("frozenTiles")
    //returns the final verified movement
    private Vector2 isCollidingWithTiles(Vector2 finalMovement){
        for(int i = 0; i < currentTetramino.getTiles().length; i++){
            for(int j = 0; j < currentTetramino.getTiles()[i].length; j++){
                Tetramino_Tile tetraminoTile = currentTetramino.getTiles()[i][j];
                if(tetraminoTile != null){
                    if(frozenTiles[tetraminoTile.getY()][tetraminoTile.getX() + (int) finalMovement.x] != null){
                        finalMovement.x = 0;
                    }
                    if(frozenTiles[tetraminoTile.getY() + (int) finalMovement.y][tetraminoTile.getX()] != null){
                        finalMovement.y = 0;
                    }
                    if(frozenTiles[tetraminoTile.getY() + (int) finalMovement.y][tetraminoTile.getX() + (int) finalMovement.x] != null){
                        finalMovement.y = 0;
                        finalMovement.x = 0;
                    }
                }
            }
        }
        return finalMovement;
    }

    //returns the final movement after collision-detection
    //begins with the collision of the borders
    //if that is verified, it returns the collision-detection with tiles
    private Vector2 isColliding(int horizontal, int vertical){
        Vector2 finalMovement = new Vector2(horizontal, vertical);

        if(currentTetramino == null){ return finalMovement; }

        if(finalMovement.x == -1){
            if(currentTetramino.getMostLeftTile().getX() - 1 < 0){ //Left Border
                finalMovement.x = 0;
            }
        }
        else if(finalMovement.x == 1){
            if(currentTetramino.getMostRightTile().getX() + 1 > Playfield.WIDTH_TILES - 1){ //Right Border
                finalMovement.x = 0;
            }
        }
        if(vertical == -1){
            if(currentTetramino.getMostBottomTile().getY() - 1 < 0) { //Bottom Border){
                finalMovement.y = 0;
            }
        }
        return isCollidingWithTiles(finalMovement);
    }

    public void checkCollision(float dt){
        if(currentTetramino == null || controller == null){
            return;
        }

        //speedCounter controlls the speed
        speedCounter_vertical += dt;
        speedCounter_horizontal += dt;

        Vector2 finalMovement; //finalMovement is the allowed/verified movement after collision-detection
        int vertical = 0; //no input yet
        int horizontal = 0; //no input yet

        //if speedCounter allows movement in that split-second and vertical movement is pressed: move down
        if(speedCounter_vertical >= MOVING_SPEED_VERTICAL && controller.isMovingDown()){
            speedCounter_vertical = 0;
            vertical--; //for the final-movement -> y = -1 -> validation -> could be 0 after if collision would happen
        }

        //horizontal movement is handled a bit different: you need to have the possibility to tap but also hold it properly
        if(controller.isMovingLeftTriggered()){
            horizontal--; //for the final-movement -> x = -1 -> validation -> could be 0 after if collision would happen
            delayCounter_horizontal = 0; //delayCounter is the time to pass after left/right is pressed to being "held"
            //it is set to 0, bc user is not holding and just tapping
        }
        if(controller.isMovingRightTriggered()){
            horizontal++; //for the final-movement -> x = x + 1 -> validation -> could be 0 after if collision would happen or left-movement is triggered simultaneously
            delayCounter_horizontal = 0; //delayCounter is the time to pass after left/right is pressed to being "held"
            //it is set to 0, bc user is not holding and just tapping
        }

        //if left/right was triggered some split-seconds ago and the user is still holding
        if((controller.isMovingLeftHeld() || controller.isMovingRightHeld()) &&
            (!controller.isMovingLeftTriggered() || !controller.isMovingRightTriggered())){
            delayCounter_horizontal += dt; //delayCounter is the time to pass after left/right is pressed to being "held"
        }

        //if it is time to move and user is HOLDING left/right
        if(speedCounter_horizontal >= MOVING_SPEED_HORIZONTAL &&
            delayCounter_horizontal >= MOVING_SPEED_HORIZONTAL_DELAY){

            speedCounter_horizontal = 0;

            if(controller.isMovingLeftHeld()){
                horizontal--;
            }
            if(controller.isMovingRightHeld()){
                horizontal++;
            }
        }

        //resetting trigger-variables, which will set to true again if the user released the key and pressed it again
        controller.resetLeftTriggered();
        controller.resetRightTriggered();

        finalMovement = isColliding(horizontal, vertical); //the "raw" movement is "sent" to the collision-detection -> final (verified) movement is returned

        //making actions based on the verified (and safe) movement
        if(finalMovement.x == -1){
            currentTetramino.moveLeft(); //going left
        }else if(finalMovement.x == 1){
            currentTetramino.moveRight(); //going right
        }

        if(finalMovement.y == -1){
            currentTetramino.moveDown(); //going down
        }

        //rotation is a different story: you can only trigger/tap it + has its own collision-detection
        if(controller.isRotationTriggered() && !isRotationColliding()){
            currentTetramino.rotate();
            controller.resetRotationTriggered(); //making sure that 'isRotationTriggered' is only true again, if the user released the input
        }

        //Hard drop to the ground with using the space-key -> can only be triggered/tapped
        if(controller.isHardDropTriggered()){
            hardDrop();
            controller.resetHardDropTriggered();
        }
    }

    private void hardDrop(){
        while(isColliding(0, -1).y == -1){
            currentTetramino.moveDown();
        }
    }

    public void tick(){ //currentTile is going 1 down
        Vector2 finalMovement = isColliding(0, -1);

        if(currentTetramino != null){
            if(finalMovement.y == -1){
                currentTetramino.moveDown();
            }else{
                freezeCurrentTile();
                checkTetris();
                spawnRandomTetramino();
            }
        }
        tickCounter = 0;
    }

    private void printFrozenTilesLines(){
        Formatter formatter = new Formatter();
        formatter.format("\n");
        for(int i = frozenTiles.length - 1; i > -1; i--){
            for(int j = 0; j < frozenTiles[i].length; j++){
                if(frozenTiles[i][j] != null){
                    formatter.format("X ");
                }else{
                    formatter.format("o ");
                }
            }
            formatter.format("\n");
        }

        Gdx.app.debug(TAG, formatter.toString());
    }

    private boolean isRowEmpty(int row){
        if(row >= Playfield.HEIGHT_TILES) return true;

        for(int i = 0; i < frozenTiles[row].length; i++){
            if(frozenTiles[row][i] != null){
                return false;
            }
        }
        return true;
    }

    private void checkTetris(){ //checking for full lines (10 blocks in the same row) and deletes them
        printFrozenTilesLines();

        //Checking for full rows and flagging them (= null)
        int deletedRows = 0;
        for(int i = 0; i < frozenTiles.length; i++){
            int counter = 0;
            for(int j = 0; j < frozenTiles[i].length; j++){
                if(frozenTiles[i][j] != null){
                    counter++;
                }
            }
            if(counter == 10){ //full line
                frozenTiles[i] = null;
                deletedRows++;
            }
        }

        if(deletedRows == 0){ return; } //no full lines

        //For every flagged row: shift every not-empty row one down
        // + set last row to a new fresh row, bc otherwise it would be the same as the row below
        for(int i = Playfield.HEIGHT_TILES - 1; i >= 0; i--){
            if(frozenTiles[i] != null) continue;

            for(int j = i; j < frozenTiles.length; j++){
                if(!isRowEmpty(j + 1)){
                    frozenTiles[j] = frozenTiles[j + 1];
                }else{
                    frozenTiles[j] = new Tetramino_Tile[Playfield.WIDTH_TILES];
                }
            }
        }

        //Update tile-positions
        for(int i = 0; i < frozenTiles.length; i++){
            for(int j = 0; j < frozenTiles[i].length; j++){
                if(frozenTiles[i][j] != null){
                    frozenTiles[i][j].setPosition(j, i);
                }
            }
        }
    }

    public void freezeCurrentTile(){
        if(currentTetramino == null){ return; }

        for(int i = 0; i < currentTetramino.getTiles().length; i++){
            for(int j = 0; j < currentTetramino.getTiles()[i].length; j++){
                if(currentTetramino.getTiles()[i][j] != null) {
                    int x = currentTetramino.getTiles()[i][j].getX();
                    int y = currentTetramino.getTiles()[i][j].getY();

                    frozenTiles[y][x] = currentTetramino.getTiles()[i][j];
                }
            }
        }
        currentTetramino = null;
    }

    public void clearFrozenTiles(){
        for(int i = 0; i < frozenTiles.length; i++){
            for(int j = 0; j < frozenTiles[i].length; j++){
                frozenTiles[i][j] = null;
            }
        }
    }

    public void spawnRandomTetramino(){
        Tetramino.Shape shape = Tetramino.Shape.values()[(int)(Math.random() * Tetramino.Shape.values().length)];
        Tetramino tetramino = new Tetramino(ATTRIBUTES_MAP.get(shape).tileTexture, shape, ATTRIBUTES_MAP.get(shape).rotationType);

        tetramino.setPosition(Playfield.WIDTH_TILES / 2 - 2, Playfield.HEIGHT_TILES - 4);

        setCurrentTetramino(tetramino);
    }

    public Color getOuterPlayfieldColor(){
        return outerPlayfieldColor;
    }

    protected void setCurrentTetramino(Tetramino tetramino){
        currentTetramino = tetramino;
    }

    protected void startGame(){
        started = true;
        paused = false;
        gameOver = false;

        currentLevel = 0;

        increaseLevel();
        clearFrozenTiles();

        spawnRandomTetramino();
    }

    protected void increaseLevel(){
        currentLevel++;
        currentSpeed = ClassicalLevelAttributes.LEVEL_SPEEDS[currentLevel];
    }
}
