package board;


import java.io.Serializable;

public class BoardUtils implements Serializable {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVEN_COLUMN = initColumn(6);
    public static final boolean[] EIGHT_COLUMN = initColumn(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] THIRD_ROW = initRow(16);
    public static final boolean[] FOURTH_ROW = initRow(24);
    public static final boolean[] FIFTH_ROW = initRow(32);
    public static final boolean[] SIXTH_ROW = initRow(40);
    public static final boolean[] SEVEN_ROW = initRow(48);
    public static final boolean[] EIGHT_ROW = initRow(56);

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        do {
            row[rowNumber] = true;
            rowNumber++;
        }while (rowNumber % NUM_TILES_ON_ROW != 0);
        return row;
    }

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_ON_ROW = 8;

    private BoardUtils()
    {throw new RuntimeException("Addon class");}

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_TILES];
        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_ON_ROW;
        }while (columnNumber < NUM_TILES);
        return column;
    }




    public static boolean isMoveValid(int moveCoordinate) {
        return moveCoordinate>=0 && moveCoordinate < NUM_TILES;
    }


}
