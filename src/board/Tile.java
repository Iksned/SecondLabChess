package board;

import figures.Figure;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile implements Serializable{

    protected final int tCoordinate;

   private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

    private static Map<Integer,EmptyTile> createAllEmptyTiles() {
        final Map<Integer,EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i< BoardUtils.NUM_TILES; i++)
        {
            emptyTileMap.put(i, new EmptyTile(i));
        }

        return Collections.unmodifiableMap(emptyTileMap);
    }

    private Tile(int tCoordinate) {
        this.tCoordinate = tCoordinate;
    }

    public int gettCoordinate() {
        return tCoordinate;
    }

    public static Tile createTile(int tCoordinate, Figure figure)
    {
        if (figure != null)
            return new OccupiedTile(tCoordinate,figure);
        else
            return EMPTY_TILES_CACHE.get(tCoordinate);
    }

    public abstract boolean isOccupied();

    public abstract Figure getFigure();

    public static final class EmptyTile extends Tile
    {
       private EmptyTile(int coordinate)
        {
            super(coordinate);
        }

        public String toString()
        {
            return "-";
        }

        public boolean isOccupied()
        {
            return false;
        }

        public Figure getFigure()
        {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile
    {
     private final Figure figureOnTile;

     private OccupiedTile(int coordinate,Figure figureOnTile)
     {
         super(coordinate);
         this.figureOnTile = figureOnTile;
     }

        public String toString()
        {
            return getFigure().getFigureSide().isBlack() ? getFigure().toString().toLowerCase() :
                    getFigure().toString();
        }

     public boolean isOccupied()
     {
         return true;
     }
     public Figure getFigure()
        {
            return figureOnTile;
        }
    }
}

