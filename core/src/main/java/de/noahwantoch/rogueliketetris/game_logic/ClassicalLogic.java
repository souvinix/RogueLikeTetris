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
    private static final String TAG = "MainGameLogic";

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
        playfield = new Playfield(new Texture("playfield_01.png")); //outer: a689d7
        outerPlayfieldColor = Color.valueOf("a689d7");

        frozenTiles = new Tetramino_Tile[10][18];

        controller = new Controller();
        Gdx.input.setInputProcessor(controller);
    }

    public void show(){
        spawnRandomTetramino();
    }

    public void render(float dt) { //requires batch.begin and batch.end
        tickCounter += dt;
        playfield.draw();
        currentTetramino.draw(playfield);

        checkCollision(dt);

        if(tickCounter >= ClassicalLevelAttributes.LEVEL_SPEEDS[currentLevel]){
            tick();
        }
    }

    public void checkCollision(float dt){
        if(currentTetramino == null || controller == null){
            return;
        }

        collisionCounter += dt;

        if(collisionCounter >= MOVING_SPEED){
            collisionCounter = 0;
            if(controller.isMovingLeft()){
                if(currentTetramino.getMostLeftTile().getX() - 1 > 0){ //Left Border
                    currentTetramino.moveLeft();
                }else{
                    Gdx.app.debug(TAG, "Left border hit");
                }
            }
            if(controller.isMovingRight()){
                if(currentTetramino.getMostRightTile().getX() + 1 < Playfield.WIDTH_TILES - 1){ //Right Border
                    currentTetramino.moveRight();
                }else{
                    Gdx.app.debug(TAG, "Right border hit");
                }
            }
            if(controller.isMovingDown()){
                if(currentTetramino.getMostBottomTile().getY() - 1 > 0) { //Bottom Border
                    currentTetramino.moveDown();
                }else{
                    Gdx.app.debug(TAG, "Bottom border has been hit");
                }
            }
        }
        if(controller.isRotationTriggered()){
            currentTetramino.rotate();
            controller.resetRotationTriggered();
        }
        if(controller.hardDrop()){
            Gdx.app.debug(TAG, "Hard drop");
        }
    }
    public void tick(){ //currentTile is going 1 down
        if(currentTetramino != null){
            currentTetramino.moveDown();
        }
        tickCounter = 0;
    }

    public void freezeCurrentTile(){
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

        tetramino.setPosition(Playfield.WIDTH_TILES / 2 - 2, Playfield.HEIGHT_TILES - 2);

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
    }

    protected void increaseLevel(){
        currentLevel++;
        currentSpeed = ClassicalLevelAttributes.LEVEL_SPEEDS[currentLevel];
    }
}
