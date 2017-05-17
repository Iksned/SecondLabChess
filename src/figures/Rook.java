package figures;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import player.FigureSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends Figure{

    private final static int[] POSSIBLE_MOVES_VECTORS = {-8,-1,1,8};

    public Rook(int position, FigureSide side) {
        super(position, side,FigureType.ROOK,true);
    }

    public Rook(int position, FigureSide side,boolean firstMove) {
        super(position, side,FigureType.ROOK,firstMove);
    }

    @Override
    public Collection<Move> calcMoves(Board board) {
        final List<Move> passMoves = new ArrayList<>();

        for (int POSSIBLE_MOVES_VECTOR : POSSIBLE_MOVES_VECTORS)
        {
            int moveCoordinate = this.position;
            while (BoardUtils.isMoveValid(moveCoordinate))
            {
                if (isFirstColumn(this.position,POSSIBLE_MOVES_VECTOR) // Заменть на moveCoordinate?!
                  ||isEightColumn(this.position,POSSIBLE_MOVES_VECTOR))
                {
                    break;
                }
                moveCoordinate +=POSSIBLE_MOVES_VECTOR;

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
                        break;
                    }

                }
            }
        }
        return passMoves;
    }

    @Override
    public Rook moveFigure(Move move) {
        return new Rook(move.getTargetCoordinate(),move.getMovedFigure().getFigureSide());
    }

    public String toString()
    {
        return FigureType.ROOK.toString();
    }

    private static boolean isFirstColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((targetPosition == -1));
    }

    private static boolean isEightColumn(int currentPosition,int targetPosition)
    {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((targetPosition == 1));
    }
}
