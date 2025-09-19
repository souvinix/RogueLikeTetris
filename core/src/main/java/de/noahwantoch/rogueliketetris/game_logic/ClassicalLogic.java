package de.noahwantoch.rogueliketetris.game_logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.EnumMap;
import java.util.Map;

import de.noahwantoch.rogueliketetris.game_elements.ClassicalLevelAttributes;
import de.noahwantoch.rogueliketetris.game_elements.Playfield;
import de.noahwantoch.rogueliketetris.game_elements.ShapeAttributes;
import de.noahwantoch.rogueliketetris.game_elements.Tetramino;
import de.noahwantoch.rogueliketetris.game_elements.Tetramino_Tile;

public class ClassicalLogic {
    //The logic of the main elements of this game
    //Classical (Tetris) and RogueLikeTetris will extend from here
    //
    //What elements does both modes share? :
    //Falling Blocks, Rotating blocks, fast bottom hit, Score, Level, Lines, GameOver after specific condiftion, Pause, Restart, Save, Load, Playfield (10x18)
    private static final String TAG = "ClassicalLogic";

    private static final float MOVING_SPEED_VERTICAL = 0.04f; //Every ... seconds
    private static final float MOVING_SPEED_HORIZONTAL = 0.05f; //Every ... seconds
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
        frozenTiles = new Tetramino_Tile[10][18];

        playfield = new Playfield(new Texture("playfield_01.png")); //outer: a689d7
        outerPlayfieldColor = Color.valueOf("a689d7");

        controller = new Controller();
        Gdx.input.setInputProcessor(controller);

        startGame();
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
    private Vector2 isCollidingWithTiles(Vector2 finalMovement){
        for(int i = 0; i < currentTetramino.getTiles().length; i++){
            for(int j = 0; j < currentTetramino.getTiles()[i].length; j++){
                Tetramino_Tile tetraminoTile = currentTetramino.getTiles()[i][j];
                if(tetraminoTile != null){
                    if(frozenTiles[tetraminoTile.getX() + (int) finalMovement.x][tetraminoTile.getY()] != null){
                        finalMovement.x = 0;
                    }
                    if(frozenTiles[tetraminoTile.getX()][tetraminoTile.getY() + (int) finalMovement.y] != null){
                        finalMovement.y = 0;
                    }
                    if(frozenTiles[tetraminoTile.getX() + (int) finalMovement.x][tetraminoTile.getY() + (int) finalMovement.y] != null){
                        finalMovement.y = 0;
                        finalMovement.x = 0;
                    }
                }
            }
        }
        return finalMovement;
    }

    private boolean isRotationCollidingWithTiles(){
        Tetramino_Tile[][] nextRotationState = currentTetramino.getTiles();
        for(int i = 0; i < nextRotationState.length; i++){
            for(int j = 0; j < nextRotationState[i].length; j++){
                if(nextRotationState[i][j] != null){
                    if(frozenTiles[currentTetramino.getX() + i][currentTetramino.getY() + (3 - j)] != null){
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

    //returns the final movement after collision-detection
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

        speedCounter_vertical += dt;
        speedCounter_horizontal += dt;

        Vector2 finalMovement;
        int vertical = 0; //no input yet
        int horizontal = 0; //no input yet

        if(speedCounter_vertical >= MOVING_SPEED_VERTICAL){
            speedCounter_vertical = 0;

            if(controller.isMovingDown()){
                vertical--;
            }
        }

        if(controller.isMovingLeftTriggered()){
            horizontal--;
            delayCounter_horizontal = 0;
        }
        if(controller.isMovingRightTriggered()){
            horizontal++;
            delayCounter_horizontal = 0;
        }

        if((controller.isMovingLeftHeld() || controller.isMovingRightHeld()) &&
            (!controller.isMovingLeftTriggered() || !controller.isMovingRightTriggered())){
            delayCounter_horizontal += dt;
        }


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

        controller.resetLeftTriggered();
        controller.resetRightTriggered();

        finalMovement = isColliding(horizontal, vertical);

        if(finalMovement.x == -1){
            currentTetramino.moveLeft();
        }else if(finalMovement.x == 1){
            currentTetramino.moveRight();
        }

        if(finalMovement.y == -1){
            currentTetramino.moveDown();
        }

        if(controller.isRotationTriggered() && !isRotationColliding()){
            currentTetramino.rotate();
            controller.resetRotationTriggered();
        }
        if(controller.hardDrop()){
            Gdx.app.debug(TAG, "Hard drop");
        }
    }
    public void tick(){ //currentTile is going 1 down
        Vector2 finalMovement = isColliding(0, -1);

        if(currentTetramino != null){
            if(finalMovement.y == -1){
                currentTetramino.moveDown();
            }else{
                freezeCurrentTile();
                spawnRandomTetramino();
            }
        }
        tickCounter = 0;
    }

    public void freezeCurrentTile(){
        if(currentTetramino == null){ return; }

        for(int i = 0; i < currentTetramino.getTiles().length; i++){
            for(int j = 0; j < currentTetramino.getTiles()[i].length; j++){
                if(currentTetramino.getTiles()[i][j] != null) {
                    int x = currentTetramino.getTiles()[i][j].getX();
                    int y = currentTetramino.getTiles()[i][j].getY();

                    frozenTiles[x][y] = currentTetramino.getTiles()[i][j];
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
