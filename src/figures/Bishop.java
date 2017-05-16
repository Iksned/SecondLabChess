package figures;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Move.AttackMove;
import board.Move.MajorMove;
import board.Tile;
import player.FigureSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishop extends Figure {

    private final static int[] POSSIBLE_MOVES_VECTORS = {-9,-7,7,9};

    public Bishop(int position, FigureSide side) {
        super(position, side,FigureType.BISHOP);
    }

    @Override
    public Collection<Move> calcMoves(Board board) {
        final List<Move> passMoves = new ArrayList<>();

        for (int POSSIBLE_MOVES_VECTOR : POSSIBLE_MOVES_VECTORS) {
            int moveCoordinate = this.position;

            while (BoardUtils.isMoveValid(moveCoordinate) && !isEightColumn(moveCoordinate,POSSIBLE_MOVES_VECTOR) && !isFirstColumn(moveCoordinate,POSSIBLE_MOVES_VECTOR))
            {
                if (isFirstColumn(this.position,POSSIBLE_MOVES_VECTOR)
                   ||isEightColumn(this.position,POSSIBLE_MOVES_VECTOR))
                {
                    break;
                }
                moveCoordinate += POSSIBLE_MOVES_VECTOR;

                if (BoardUtils.isMoveValid(moveCoordinate)) {
                    final Tile moveCoordinateTile = board.getTile(moveCoordinate);

                    if (!moveCoordinateTile.isOccupied()) {
                        passMoves.add(new MajorMove(board, this, moveCoordinate));
                    } else {
                        final Figure figureOnTile = moveCoordinateTile.getFigure();
                        final FigureSide figureSide = figureOnTile.getFigureSide();

                        if (this.side != figureSide) {
                            passMoves.add(new AttackMove(board, this, moveCoordinate, figureOnTile));
                        }
                        break;
                    }

                }

            }
        }
        return passMoves;
    }

    @Override
    public Bishop moveFigure(Move move) {
        return new Bishop(move.getTargetCoordinate(),move.getMovedFigure().getFigureSide());
    }

    public String toString()
    {
        return FigureType.BISHOP.toString();
    }

    private static boolean isFirstColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((targetPosition == -9)
                || (targetPosition == 7));
    }

    private static boolean isEightColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((targetPosition == -7)
                || (targetPosition == 9));
    }
}
