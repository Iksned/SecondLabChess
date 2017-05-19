package figures;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import player.FigureSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Figure {

    private final static int[] POSSIBLE_MOVES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int position, FigureSide side) {
        super(position, side,FigureType.KING,true);
    }
    public King(int position, FigureSide side,boolean firstMove) {
        super(position, side,FigureType.KING,firstMove);
    }
    //TODO битые поля
    @Override
    public Collection<Move> calcMoves(Board board) {

        final List<Move> passMoves = new ArrayList<>();

        for (int POSSIBLE_MOVE : POSSIBLE_MOVES) {

            final int moveCoordinate = this.position + POSSIBLE_MOVE;

            if (isFirstColumn(this.position,POSSIBLE_MOVE) ||isEightColumn(this.position,POSSIBLE_MOVE)) {
                continue;
            }

            if (BoardUtils.isMoveValid(moveCoordinate)) {
                final Tile moveCoordinateTile = board.getTile(moveCoordinate);

                if (!moveCoordinateTile.isOccupied()) {
                    passMoves.add(new Move.MajorMove(board, this, moveCoordinate));
                } else {
                    final Figure figureOnTile = moveCoordinateTile.getFigure();
                    final FigureSide figureSide = figureOnTile.getFigureSide();

                    if (this.side != figureSide) {
                        passMoves.add(new Move.AttackMove(board, this, moveCoordinate, figureOnTile));
                    }
                }
            }

        }

        return passMoves;
    }

    @Override
    public King moveFigure(Move move) {
        return new King(move.getTargetCoordinate(),move.getMovedFigure().getFigureSide(),false);
    }

    public String toString()
    {
        return FigureType.KING.toString();
    }

    private static boolean isFirstColumn(int currentPosition, int targetPosition) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((targetPosition == -9)
                || (targetPosition == -1)
                || (targetPosition == 7));
    }

    private static boolean isEightColumn(int currentPosition, int targetPosition) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((targetPosition == -7)
                || (targetPosition == 1)
                || (targetPosition == 9));
    }

}