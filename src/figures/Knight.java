package figures;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import player.FigureSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;


public class Knight extends Figure {

    private final static int[] POSSIBLE_MOVES = {-17,-15,-10,-6, 6, 10, 15, 17};

    public Knight(int position, FigureSide side) {
        super(position, side,FigureType.KNIGHT);
    }

    @Override
    public Collection<Move> calcMoves(Board board) {

        final List<Move> passMoves = new ArrayList<>();
        for (int POSSIBLE_MOVE : POSSIBLE_MOVES) {

           final int moveCoordinate = this.position + POSSIBLE_MOVE;

            if(BoardUtils.isMoveValid(moveCoordinate))
            {
                if (isFirstColumn(this.position,POSSIBLE_MOVE)
                        ||isSecondColumn(this.position,POSSIBLE_MOVE)
                        ||isSevenColumn(this.position,POSSIBLE_MOVE)
                        ||isEightColumn(this.position,POSSIBLE_MOVE))
                {
                    continue;
                }
                final Tile moveCoordinateTile = board.getTile(moveCoordinate);

                if (!moveCoordinateTile.isOccupied())
                {
                    passMoves.add(new MajorMove(board,this, moveCoordinate));
                } else {
                    final Figure figureOnTile = moveCoordinateTile.getFigure();
                    final FigureSide figureSide = figureOnTile.getFigureSide();

                    if (this.side != figureSide)
                    {
                        passMoves.add(new AttackMove(board,this,moveCoordinate,figureOnTile));
                    }
                }
            }
        }
        return Collections.unmodifiableList(passMoves);
    }

    @Override
    public Knight moveFigure(Move move) {
        return new Knight(move.getTargetCoordinate(),move.getMovedFigure().getFigureSide());
    }

    public String toString()
    {
        return FigureType.KNIGHT.toString();
    }

    private static boolean isFirstColumn(int currentPosition,int targetPosition)
    {
       return BoardUtils.FIRST_COLUMN[currentPosition] && ((targetPosition == -17)
                                                    || (targetPosition == -10)
                                                    || (targetPosition == 6)
                                                    || (targetPosition == 15));
    }
    private static boolean isSecondColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((targetPosition == -10)
                || (targetPosition == 6));
    }

    private static boolean isSevenColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.SEVEN_COLUMN[currentPosition] && ((targetPosition == -6)
                || (targetPosition == 10));
    }
    private static boolean isEightColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((targetPosition == 17)
                                                         || (targetPosition == 10)
                                                         || (targetPosition == -6)
                                                         || (targetPosition == -15));
    }



}
