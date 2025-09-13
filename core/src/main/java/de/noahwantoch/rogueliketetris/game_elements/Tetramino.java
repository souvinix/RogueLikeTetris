package de.noahwantoch.rogueliketetris.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Formatter;

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

    private Shape shape;
    private Tetramino_Tile[][][] rotationStates;


    public Tetramino(Texture texture, Shape shape, int rotationType) {
        tiles = new Tetramino_Tile[4][4];
        this.rotationType = rotationType;
        this.rotationStates = new Tetramino_Tile[rotationType][4][4];
        this.shape = shape;

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
        rotationStates[0] = tiles; //start-state and first element in rotation-states

        //works fine too
//        for(int x = 1; x < rotationType; x++){ //starts at 1, bc one element already in list
//            Tetramino_Tile[][] newTiles = new Tetramino_Tile[4][4];
//            rotationStates[x] = newTiles;
//            for(int i = 0; i < rotationStates[x - 1][0].length; i++){
//                for(int j = 1; j < rotationStates[x - 1].length; j++){
//                    rotationStates[x][i][j - 1] = rotationStates[x - 1][3 - j][i];
//                }
//            }
//        }

        for(int x = 1; x < rotationType; x++){ //starts at 1, bc one element already in list
            Tetramino_Tile[][] newTiles = new Tetramino_Tile[4][4];
            rotationStates[x] = newTiles;

            //reminder: rotation-states -> 3d-list [][][]
            //starting at i = 1 because every element with i = 0 is null anyways after transforming -> shift of 1 to left
            for(int i = 0; i < rotationStates[x - 1][0].length; i++){
                for(int j = 1; j < rotationStates[x - 1].length; j++){
                    //i - 1 is the shift to left
                    //swap i and j after '=' bc, we make our old rows to the new columns
                    //3 - i is because the upper element in rotationState is rotationStates[...][...][0] x: left-right, y: top-bottom
                    //but this.getY() == 0 is also the most bottom element: bottom-top -> transforming/mirroring

                    rotationStates[x][i][3 - j] = rotationStates[x - 1][j - 1][i];
                }
            }
        }

        setLocalPositions();
    }


    private void setLocalPositions() {
        for (int x = 0; x < rotationType; x++) {
            for (int i = 0; i < rotationStates[currentRotation][x].length; i++) {
                for (int j = 0; j < rotationStates[currentRotation].length; j++) {
                    if(rotationStates[x][i][j] == null){ continue; }

                    rotationStates[x][i][j].setLocalX(i);
                    rotationStates[x][i][j].setLocalY(3 - j);
                }
            }
        }
    }


    public String tetraminoToString() {
        Formatter formatter = new Formatter();

        formatter.format("X: %s, Y: %s, Rotation-state: %d\n", x, y, currentRotation);
        formatter.format("Rotations states for %s: {\n", shape.toString()); // Startklammer für die Zeile
        for (int i = 0; i < rotationStates[currentRotation][0].length; i++) {
            for(int j = 0; j < rotationStates[currentRotation].length; j++){
                if(rotationStates[currentRotation][i][j] != null){
                    formatter.format("X ");
                }else {
                    formatter.format("- ");
                }
            }
            formatter.format("\n");
        }
        formatter.format(" }\n"); // Endklammer und Zeilenumbruch

        return formatter.toString();
    }

    public Tetramino_Tile[][] getNextRotationState(){
        if(currentRotation < rotationType - 1){
            return rotationStates[currentRotation + 1];
        }else{
            return rotationStates[0];
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
        //Drawing current tile in its current rotation state
        for(int i = 0; i < rotationStates[currentRotation].length; i++){
            for(int j = 0; j < rotationStates[currentRotation][i].length; j++){
                if(rotationStates[currentRotation][i][j] != null){
                    //works fine
//                    rotationStates[currentRotation][i][j].setX(x + j);
//                    rotationStates[currentRotation][i][j].setY(y + (3 - i));

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
        Tetramino_Tile tile = null;
        for(int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] != null){
                    if(tile == null || tiles[i][j].getX() > tile.getX()){
                        tile = tiles[i][j];
                    }
                }
            }
        }
        return tile;
    }

    public Tetramino_Tile getMostBottomTile(){
        Tetramino_Tile tile = null;
        for(int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] != null){
                    if(tile == null || tiles[i][j].getY() < tile.getY()){
                        tile = tiles[i][j];
                    }
                }
            }
        }
        return tile;
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
