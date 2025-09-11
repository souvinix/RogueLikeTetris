package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Tetramino {

    public enum Shape {
        L_SHAPED,
        J_SHAPED,
        S_SHAPED,
        Z_SHAPED,
        T_SHAPED,
        I_SHAPED,
        O_SHAPED
    }

    private Tetramino_Tile[][] tiles;
    private int rotationType;
    private int currentRotation = 0;
    private int tileSize;

    //position of the down-left tile, default is 0,0
    private int x = 0; //tile coord: 0, 1, 2, 3, ...
    private int y = 0; //tile coord: 0, 1, 2, 3, ...

    private Texture texture;

    private Tetramino_Tile[][][] rotationStates;

    public Tetramino(Texture texture, Shape shape, int rotationType) {
        tiles = new Tetramino_Tile[4][4];
        this.texture = texture;
        this.rotationType = rotationType;
        this.rotationStates = new Tetramino_Tile[rotationType][4][4];

        if (shape == Shape.L_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), new Tetramino_Tile(texture), null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else if (shape == Shape.J_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else if (shape == Shape.S_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), new Tetramino_Tile(texture), null},
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, null, null, null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else if (shape == Shape.Z_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), new Tetramino_Tile(texture), null},
                new Tetramino_Tile[]{null, null, null, null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else if (shape == Shape.T_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{null, new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), new Tetramino_Tile(texture), null},
                new Tetramino_Tile[]{null, null, null, null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else if (shape == Shape.I_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{null, null, null, null},
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), new Tetramino_Tile(texture), new Tetramino_Tile(texture)},
                new Tetramino_Tile[]{null, null, null, null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else if (shape == Shape.O_SHAPED) {
            setTiles(
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{new Tetramino_Tile(texture), new Tetramino_Tile(texture), null, null},
                new Tetramino_Tile[]{null, null, null, null},
                new Tetramino_Tile[]{null, null, null, null}
            );
        } else {
            Gdx.app.debug("Tetramino", "Tetramino doesn't have the correct shape");
        }

        generateRotationStates();
    }

    public void rotate() {
        Gdx.app.debug("Tetramino", "Rotating");
        if(currentRotation < rotationType - 1){
            currentRotation++;
        }else{
            currentRotation = 0;
        }
    }

    //(1,1) is the origin of rotation
    private void generateRotationStates(){
        rotationStates[0] = tiles;

        for(int x = 1; x < rotationType; x++){
            Tetramino_Tile[][] newTiles = new Tetramino_Tile[4][4];
            rotationStates[x] = newTiles;
            for(int i = 0; i < rotationStates[x - 1][0].length; i++){
                for(int j = 1; j < rotationStates[x - 1].length; j++){
                    rotationStates[x][i][j - 1] = rotationStates[x - 1][3 - j][i];
                }
            }
        }
    }

    private void setTiles(Tetramino_Tile[]... tiles){
        if(tiles.length == 4){
            this.tiles = tiles;
        }else{
            Gdx.app.debug("Tetramino", "Tiles don't have the correct length");
        }
    }

    public void draw(Playfield playfield){
        for(int i = 0; i < rotationStates[currentRotation].length; i++){
            for(int j = 0; j < rotationStates[currentRotation][i].length; j++){
                if(rotationStates[currentRotation][i][j] != null){
                    rotationStates[currentRotation][i][j].setX(x + j);
                    rotationStates[currentRotation][i][j].setY(y + (3 - i));
                    rotationStates[currentRotation][i][j].draw(playfield);
                }
            }
        }
    }

    public Tetramino_Tile getMostLeftTile(){
        Tetramino_Tile tile = null;
        for(int i = 0; i < tiles.length; i++){
            for(int j = 0; j < tiles[i].length; j++){
                if(tiles[i][j] != null){
                    if(tile == null || tiles[i][j].getX() < tile.getX()){
                        tile = tiles[i][j];
                    }
                }
            }
        }
        return tile;
    }

    public Tetramino_Tile getMostRightTile(){
        for(int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[3 - i][j] != null) {
                    return tiles[3 - i][j];
                }
            }
        }
        return null;
    }

    public Tetramino_Tile getMostBottomTile(){
        for(int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][3 - j] != null) {
                    return tiles[i][3 - j];
                }
            }
        }
        return null;
    }

    public void setX(int x){ this.x = x; }
    public void setY(int y){ this.y = y; }
    public int getX(){ return x; }
    public int getY(){ return y; }

    public void setPosition(int x, int y){ setX(x); setY(y); }

    public Tetramino_Tile[][] getTiles(){
        return rotationStates[currentRotation];
    }

    public void moveLeft(){
        setX(x - 1);
    }

    public void moveRight(){
        setX(x + 1);
    }

    public void moveDown(){
        setY(y - 1);
    }
}
