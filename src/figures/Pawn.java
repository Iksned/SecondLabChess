package figures;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import player.FigureSide;
import view.ReplaceFrame;
import view.Replacer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static board.Move.*;

public class Pawn extends Figure {
    private Figure replacedFigure;

    private final static int[] POSSIBLE_MOVES = {7,8,9,16};

    public Pawn(int position, FigureSide side) {
        super(position, side,FigureType.PAWN,true);
    }
    public Pawn(int position, FigureSide side,boolean firstMove) {
        super(position, side,FigureType.PAWN,firstMove);
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
            if(POSSIBLE_MOVE == 8 && !board.getTile(moveCoordinate).isOccupied()) {
                if (this.getFigureSide().isUpgradeTile(moveCoordinate)) {
                    passMoves.add(new ReplaceMove(new MinorMove(board,this,moveCoordinate)));
                } else {
                    passMoves.add(new MinorMove(board, this, moveCoordinate));
                }
            }
            if(POSSIBLE_MOVE == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.position] && this.getFigureSide().isBlack())
                    || (BoardUtils.SEVEN_ROW[this.position] && this.getFigureSide().isWhite())))
            {
                final int betweenJumpCoordinate = this.position + (this.getFigureSide().getDirection()*8);
                if(!board.getTile(betweenJumpCoordinate).isOccupied()
                       && !board.getTile(moveCoordinate).isOccupied())
                {
                    passMoves.add(new PawnJumpMove(board,this,moveCoordinate));
                }
            }
            if(POSSIBLE_MOVE == 7 && !((BoardUtils.EIGHT_COLUMN[this.position] && this.side.isWhite())
                                            || (BoardUtils.FIRST_COLUMN[this.position] && this.side.isBlack())))
            {
                if(board.getTile(moveCoordinate).isOccupied())
                {
                    final Figure targetFigure = board.getTile(moveCoordinate).getFigure();
                    if (targetFigure.getFigureSide() != this.side)
                    {
                        if (this.getFigureSide().isUpgradeTile(moveCoordinate)) {
                            passMoves.add(new ReplaceMove(new MinorAttackMove(board,this,moveCoordinate,targetFigure)));
                        } else {
                            passMoves.add(new MinorAttackMove(board, this, moveCoordinate, targetFigure));
                        }
                    }
                } else {
                      final Tile onPassTile = board.getTile(this.position - this.getFigureSide().getDirection());
                      if (onPassTile.isOccupied()) {
                          Figure onPassFigure = onPassTile.getFigure();
                          if ((onPassFigure.equals(board.getOnPassPawn())) && (board.getOnPassPawn().getFigureSide() != this.getFigureSide())) {
                              passMoves.add(new OnPassAttackMove(board, this, moveCoordinate, board.getOnPassPawn()));
                          }
                      }
                }
            }
            if (POSSIBLE_MOVE == 9 && !((BoardUtils.FIRST_COLUMN[this.position] && this.side.isWhite())
                    || (BoardUtils.EIGHT_COLUMN[this.position] && this.side.isBlack())))
            {
                if(board.getTile(moveCoordinate).isOccupied()) {
                    final Figure targetFigure = board.getTile(moveCoordinate).getFigure();
                    if (targetFigure.getFigureSide() != this.side) {
                        if (this.getFigureSide().isUpgradeTile(moveCoordinate)) {
                            passMoves.add(new ReplaceMove(new MinorAttackMove(board,this,moveCoordinate,targetFigure)));
                        } else {
                            passMoves.add(new MinorAttackMove(board, this, moveCoordinate, targetFigure));
                        }
                }
            }
                else {
                    final Tile onPassTile = board.getTile(this.position + this.getFigureSide().getDirection());
                    if (onPassTile.isOccupied()) {
                        Figure onPassFigure = onPassTile.getFigure();
                        if ((onPassFigure.equals(board.getOnPassPawn())) && (board.getOnPassPawn().getFigureSide() != this.getFigureSide())) {
                            passMoves.add(new OnPassAttackMove(board, this, moveCoordinate, board.getOnPassPawn()));
                        }
                    }
                }
            }

        }
        return passMoves;
    }

    @Override
    public Pawn moveFigure(Move move) {
        return new Pawn(move.getTargetCoordinate(),move.getMovedFigure().getFigureSide(),false);
    }

    public String toString()
    {
        return FigureType.PAWN.toString();
    }

    public void setReplacedFigure(Figure replacedFigure) {
        this.replacedFigure = replacedFigure;
    }

    public Figure getReplacedFigure() {
        return replacedFigure;
    }

    public Figure getUpgradedFigure() {
        return replacedFigure;
        //return new Queen(this.position,this.side,false);
    }
}


