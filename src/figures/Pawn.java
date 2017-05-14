package figures;

import board.Board;
import board.BoardUtils;
import board.Move;
import player.FigureSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static board.Move.*;

public class Pawn extends Figure {

    private final static int[] POSSIBLE_MOVES = {7,8,9,16};

    public Pawn(int position, FigureSide side) {
        super(position, side,FigureType.PAWN);
    }

    @Override
    public Collection<Move> calcMoves(Board board) {

        final List<Move> passMoves = new ArrayList<>();

        for (int POSSIBLE_MOVE : POSSIBLE_MOVES) {
            final int moveCoordinate = this.position + (this.getFigureSide().getDirection())*(POSSIBLE_MOVE);

            if(!BoardUtils.isMoveValid(moveCoordinate))
            {
                continue;
            }
            if(POSSIBLE_MOVE == 8 || POSSIBLE_MOVE == 16 && !board.getTile(moveCoordinate).isOccupied())
            {
                //TODO
                passMoves.add(new MajorMove(board,this,moveCoordinate));
            }
            else if(POSSIBLE_MOVE == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.position] && this.getFigureSide().isBlack())
                    || (BoardUtils.SEVEN_ROW[this.position] && this.getFigureSide().isWhite())))
            {
                final int betweenJumpCoordinate = this.position + (this.getFigureSide().getDirection()*8);
                if(!board.getTile(betweenJumpCoordinate).isOccupied()
                       && !board.getTile(moveCoordinate).isOccupied())
                {
                    passMoves.add(new PawnJumpMove(board,this,moveCoordinate));
                }
            } else if(POSSIBLE_MOVE == 7 && !((BoardUtils.EIGHT_COLUMN[this.position] && this.side.isWhite())
                                            || (BoardUtils.FIRST_COLUMN[this.position] && this.side.isBlack())))
            {
                if(board.getTile(moveCoordinate).isOccupied())
                {
                    final Figure targetFigure = board.getTile(moveCoordinate).getFigure();
                    if (targetFigure.getFigureSide() != this.side)
                    {
                        //TODO
                        passMoves.add(new AttackMove(board,this,moveCoordinate,targetFigure));
                    }
                }
            }
            else if (POSSIBLE_MOVE == 9 && !((BoardUtils.FIRST_COLUMN[this.position] && this.side.isWhite())
                    || (BoardUtils.EIGHT_COLUMN[this.position] && this.side.isBlack())))
            {
                if(board.getTile(moveCoordinate).isOccupied()) {
                    final Figure targetFigure = board.getTile(moveCoordinate).getFigure();
                    if (targetFigure.getFigureSide() != this.side) {
                        //TODO
                        passMoves.add(new AttackMove(board, this, moveCoordinate, targetFigure));
                }
            }
            }

        }
        return passMoves;
    }

    @Override
    public Pawn moveFigure(Move move) {
        return new Pawn(move.getTargetCoordinate(),move.getMovedFigure().getFigureSide());
    }

    public String toString()
    {
        return FigureType.PAWN.toString();
    }
}
