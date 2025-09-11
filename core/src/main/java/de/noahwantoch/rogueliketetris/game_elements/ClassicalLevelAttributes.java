package de.noahwantoch.rogueliketetris.game_elements;

public class ClassicalLevelAttributes{
    public static final float[] BASE_POINTS_FOR_ROWS = {
        0,
        40,  //* (Level + 1)
        100, //* (Level + 1)
        300, //* (Level + 1)
        1200 //* (Level + 1)
    };
    public static final float[] LEVEL_SPEEDS = {
        0.800f, // Level 0 (Doesn't exist)
        0.717f, // Level 1
        0.633f, // Level 2
        0.550f, // Level 3
        0.467f, // Level 4
        0.383f, // Level 5
        0.300f, // Level 6
        0.217f, // Level 7
        0.133f, // Level 8
        0.100f, // Level 9
        0.083f, // Level 10
        0.083f, // Level 11
        0.083f, // Level 12
        0.067f, // Level 13
        0.067f, // Level 14
        0.067f, // Level 15
        0.050f, // Level 16
        0.050f, // Level 17
        0.050f, // Level 18
        0.033f, // Level 19
        0.025f  // Level 20 (Bonus)
    };
}
