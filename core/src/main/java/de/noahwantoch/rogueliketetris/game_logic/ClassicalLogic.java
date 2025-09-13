package de.noahwantoch.rogueliketetris.game_logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

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

    private static final float MOVING_SPEED = 0.06f; //Every ... seconds
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
    private float collisionCounter = 0;


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
    public ClassicalLogic(){

    }

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

        checkCollision(dt);

        if(tickCounter >= ClassicalLevelAttributes.LEVEL_SPEEDS[currentLevel]){
            tick();
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
    public enum Direction{
        LEFT,
        RIGHT,
        DOWN,
        ROTATING;
    }
    private boolean isCollidingWithTiles(Direction direction){
        if(currentTetramino == null){ return false; }

        if(direction == Direction.ROTATING){
            Tetramino_Tile[][] nextRotationState = currentTetramino.getNextRotationState();
            for(int i = 0; i < nextRotationState.length; i++){
                for(int j = 0; j < nextRotationState[i].length; j++){
                    if(nextRotationState[i][j] != null){
                        if(frozenTiles[nextRotationState[i][j].getX()][nextRotationState[i][j].getY()] != null){
                            return true;
                        }
                    }
                }
            }
        }

        for(int i = 0; i < currentTetramino.getTiles().length; i++){
            for(int j = 0; j < currentTetramino.getTiles()[i].length; j++){
                Tetramino_Tile tetraminoTile = currentTetramino.getTiles()[i][j];
                if(tetraminoTile != null){
                    if(tetraminoTile.getY() >= Playfield.HEIGHT_TILES){ break; }
                    if(direction == Direction.LEFT){
                        if(tetraminoTile.getX() == 0) continue; //There is no tile to the left -> other function is handling collision with border
                        if(tetraminoTile.getX() == Playfield.WIDTH_TILES - 1) continue; //There is no tile to the right -> other function is handling collision with border
                        if(tetraminoTile.getY() == 0) continue; //There is no tile below -> other function is handling collision with border

                        if(frozenTiles[tetraminoTile.getX() - 1][tetraminoTile.getY()] != null){
                            return true;
                        }
                    }else if(direction == Direction.RIGHT){
                        if(frozenTiles[tetraminoTile.getX() + 1][tetraminoTile.getY()] != null){
                            return true;
                        }
                    }else if(direction == Direction.DOWN){
                        if(frozenTiles[tetraminoTile.getX()][tetraminoTile.getY() - 1] != null){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean isColliding(Direction direction){
        if(currentTetramino == null){ return false; }

        Gdx.app.debug(TAG, currentTetramino.tetraminoToString());

        if(direction == Direction.LEFT){
            if(currentTetramino.getMostLeftTile().getX() - 1 < 0 || isCollidingWithTiles(direction)){ //Left Border
                return true;
            }
        }
        else if(direction == Direction.RIGHT){
            if(currentTetramino.getMostRightTile().getX() + 1 > Playfield.WIDTH_TILES - 1 || isCollidingWithTiles(direction)){ //Right Border
                return true;
            }
        }
        else if(direction == Direction.DOWN){
            if(currentTetramino.getMostBottomTile().getY() - 1 < 0 || isCollidingWithTiles(direction)) { //Bottom Border){
                return true;
            }
        }else if(direction == Direction.ROTATING){
            Tetramino_Tile[][] nextState_tiles = currentTetramino.getNextRotationState();
            for(Tetramino_Tile[] rows : nextState_tiles){
                for(Tetramino_Tile tile : rows){
                    if(tile != null){
                        Gdx.app.debug("Rotation check", "current Y: " + currentTetramino.getY() + ", local Y: " + tile.getLocalY());
                        if(currentTetramino.getX() + tile.getLocalX() < 0 || currentTetramino.getX() + tile.getLocalX() + 1 > Playfield.WIDTH_TILES || currentTetramino.getY() + tile.getLocalY() < 0){
                            return true;
                        }
                    }
                }
            }

            if(isCollidingWithTiles(direction)){ return true; }
        }
        return false;
    }

    public void checkCollision(float dt){
        if(currentTetramino == null || controller == null){
            return;
        }

        collisionCounter += dt;

        if(collisionCounter >= MOVING_SPEED){
            collisionCounter = 0;
            if(controller.isMovingLeft() && !isColliding(Direction.LEFT)){
                currentTetramino.moveLeft();
            }
            if(controller.isMovingRight() && !isColliding(Direction.RIGHT)){
                currentTetramino.moveRight();
            }
            if(controller.isMovingDown() && !isColliding(Direction.DOWN)){
                currentTetramino.moveDown();
            }
        }
        if(controller.isRotationTriggered() && !isColliding(Direction.ROTATING)){
            currentTetramino.rotate();
            controller.resetRotationTriggered();
        }
        if(controller.hardDrop()){
            Gdx.app.debug(TAG, "Hard drop");
        }
    }
    public void tick(){ //currentTile is going 1 down
        if(currentTetramino != null){
            if(!isColliding(Direction.DOWN)){
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
