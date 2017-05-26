package board;



import addons.Support;
import figures.*;
import player.BlackPlayer;
import player.FigureSide;
import player.Player;
import player.WhitePlayer;

import java.io.Serializable;
import java.util.*;

public class Board implements Serializable{

    private final List<Tile> chessboard;
    private final Collection<Figure> whiteFigures;
    private final Collection<Figure> blackFigures;

    private final Pawn onPassPawn;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private Board(Builder builder)
    {
        this.chessboard = createChessBoard(builder);
        this.whiteFigures = calcActiveFigures(this.chessboard,FigureSide.WHITE);
        this.blackFigures = calcActiveFigures(this.chessboard,FigureSide.BLACK);
        this.onPassPawn = builder.onPassPawn;

        final Collection<Move> whiteMovesSum = calculateMoves(this.whiteFigures);
        final Collection<Move> blackMovesSum = calculateMoves(this.blackFigures);

        this.whitePlayer = new WhitePlayer(this,whiteMovesSum,blackMovesSum);
        this.blackPlayer = new BlackPlayer(this,whiteMovesSum,blackMovesSum);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer,this.blackPlayer);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES;i++)
        {
            final String tileText = this.chessboard.get(i).toString();
            builder.append(String.format("%3s",tileText));
            if ((i + 1)% BoardUtils.NUM_TILES_ON_ROW == 0)
            {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private Collection<Move> calculateMoves(Collection<Figure> FIGURES) {

        final List<Move> moveList = new ArrayList<>();

        for(Figure figure:FIGURES)
        {
            moveList.addAll(figure.calcMoves(this));
        }

        return moveList;

    }


    private static Collection<Figure> calcActiveFigures(List<Tile> chessboard, FigureSide side) {
        final List<Figure> activeFigures = new ArrayList<>();
        for (Tile tile:chessboard)
        {
            if(tile.isOccupied()){
                Figure figure = tile.getFigure();
                if(figure.getFigureSide() == side)
                    activeFigures.add(figure);
            }
        }
        return activeFigures;
    }

    public Tile getTile(int tCoordinate)
    {
        return chessboard.get(tCoordinate);
    }

    public Pawn getOnPassPawn() {
        return onPassPawn;
    }

    private static List<Tile> createChessBoard(Builder builder)
    {
        //tiles[] vs List<Tile>
            final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                tiles[i] = Tile.createTile(i, builder.boardconfig.get(i));
            }


        return new ArrayList<>(Arrays.asList(tiles));
    }

    public static Board createDefaultBoard()
    {
        final Builder builder = new Builder();
        //Black
        builder.setFigure(new Rook(0,FigureSide.BLACK));
        builder.setFigure(new Knight(1,FigureSide.BLACK));
        builder.setFigure(new Bishop(2,FigureSide.BLACK));
        builder.setFigure(new Queen(3,FigureSide.BLACK));
        builder.setFigure(new King(4,FigureSide.BLACK));
        builder.setFigure(new Bishop(5,FigureSide.BLACK));
        builder.setFigure(new Knight(6,FigureSide.BLACK));
        builder.setFigure(new Rook(7,FigureSide.BLACK));
        builder.setFigure(new Pawn(8,FigureSide.BLACK));
        builder.setFigure(new Pawn(9,FigureSide.BLACK));
        builder.setFigure(new Pawn(10,FigureSide.BLACK));
        builder.setFigure(new Pawn(11,FigureSide.BLACK));
        builder.setFigure(new Pawn(12,FigureSide.BLACK));
        builder.setFigure(new Pawn(13,FigureSide.BLACK));
        builder.setFigure(new Pawn(14,FigureSide.BLACK));
        builder.setFigure(new Pawn(15,FigureSide.BLACK));
        //White
        builder.setFigure(new Pawn(48,FigureSide.WHITE));
        builder.setFigure(new Pawn(49,FigureSide.WHITE));
        builder.setFigure(new Pawn(50,FigureSide.WHITE));
        builder.setFigure(new Pawn(51,FigureSide.WHITE));
        builder.setFigure(new Pawn(52,FigureSide.WHITE));
        builder.setFigure(new Pawn(53,FigureSide.WHITE));
        builder.setFigure(new Pawn(54,FigureSide.WHITE));
        builder.setFigure(new Pawn(55,FigureSide.WHITE));
        builder.setFigure(new Rook(56,FigureSide.WHITE));
        builder.setFigure(new Knight(57,FigureSide.WHITE));
        builder.setFigure(new Bishop(58,FigureSide.WHITE));
        builder.setFigure(new Queen(59,FigureSide.WHITE));
        builder.setFigure(new King(60,FigureSide.WHITE));
        builder.setFigure(new Bishop(61,FigureSide.WHITE));
        builder.setFigure(new Knight(62,FigureSide.WHITE));
        builder.setFigure(new Rook(63,FigureSide.WHITE));


        builder.setMoveMaker(FigureSide.WHITE);

        return builder.build();

    }

    public Collection<Figure> getWhiteFigures() {
        return this.whiteFigures;
    }

    public Collection<Figure> getBlackFigures() {
        return this.blackFigures;
    }

    public Player WhitePlayer() {
        return this.whitePlayer;
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player getCurrentPlayer()
    {
        return this.currentPlayer;
    }


    //Кастомный метод для объединения коллекций, варианты.
    public Iterable<Move> getAllMoves() {
        return Support.concat(this.whitePlayer.getPassMoves(), this.blackPlayer.getPassMoves());
    }

    public static class Builder{

        Map<Integer, Figure> boardconfig;
        FigureSide nextMoveMaker;
        Pawn onPassPawn;

        public Builder(){
            this.boardconfig = new HashMap<>();
        }

        public Builder setFigure(Figure figure)
        {
            this.boardconfig.put(figure.getFigurePos(),figure);
            return this;
        }

        public Builder setMoveMaker(FigureSide side)
        {
            this.nextMoveMaker = side;
            return this;
        }

        public Board build()
        {
            return new Board(this);
        }

        public void setOnPassPawn(Pawn onPassPawn) {
            this.onPassPawn = onPassPawn;
        }
    }
}
