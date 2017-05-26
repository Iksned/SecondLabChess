package figures;

import board.Board;
import board.Move;
import player.FigureSide;

import java.io.Serializable;
import java.util.Collection;

public abstract class Figure implements Serializable{

    protected final FigureType figureType;
    protected final int position;
    protected final FigureSide side;
    protected boolean firstMove;

    private final int cachedHash;

    Figure(int position, FigureSide side,FigureType figureType,boolean firstMove)
    {
        this.position = position;
        this.side = side;
        this.figureType = figureType;
        //TODO
        this.firstMove = firstMove;
        this.cachedHash = calcHashCode();
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    private int calcHashCode() {
        int result = figureType.hashCode();
        result = 31 * result + side.hashCode();
        result = 31 * result + position;
        result = 31 * result + (isFirstMove() ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object object){
        if (this == object)
        {
            return true;
        }
        if(!(object instanceof Figure))
            return false;
        final Figure otherFigure = (Figure)object;
        return side == otherFigure.getFigureSide() &&
               figureType == otherFigure.getFigureType() &&
               position == otherFigure.getFigurePos() &&
               isFirstMove() == otherFigure.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHash;
    }

    public FigureSide getFigureSide()
    {
        return side;
    }

    public abstract Collection<Move> calcMoves(final Board board);

    public boolean isFirstMove() {
        return firstMove;
    }

    public int getFigurePos() {
        return this.position;
    }

    public FigureType getFigureType() {
        return this.figureType;
    }

    public abstract Figure moveFigure(Move move);

    public enum FigureType {

        PAWN("P") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };
        private String figureName;

        FigureType(final String figureName){
            this.figureName = figureName;
        }

        public String toString()
        {
            return this.figureName;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();
    }
}
