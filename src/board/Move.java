package board;


import board.Board.Builder;
import figures.*;
import player.FigureSide;
import view.ReplaceFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

/* Класс котороый соверщает ход, создавая при этом новую доску и проверяя корректен ли он.
Основной метод execute();
 */

public abstract class Move {

    final Board board;
    final Figure movedFigure;
    final int targetCoordinate;

    public static final Move EMPTY_MOVE = new EmptyMove();

    private Move(Board board, Figure movedFigure, int targetCoordinate) {
        this.board = board;
        this.movedFigure = movedFigure;
        this.targetCoordinate = targetCoordinate;

    }

    private Move(Board board, int targetCoordinate) {
        this.board = board;
        this.movedFigure = null;
        this.targetCoordinate = targetCoordinate;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.targetCoordinate;
        result = 31 * result + this.movedFigure.hashCode();
        result = 31 * result + this.movedFigure.getFigurePos();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
        {
            return true;
        }
        if(!(obj instanceof Move))
        {
            return false;
        }
        final Move otherMove = (Move) obj;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getTargetCoordinate() == otherMove.getTargetCoordinate() &&
                getMovedFigure().equals(otherMove.getMovedFigure()) &&
                getCurrentCoordinate() == otherMove.getCurrentCoordinate();
    }

    public Figure getMovedFigure()
    {
        return this.movedFigure;
    }

    public boolean isAttack()
    {
        return false;
    }

    public boolean isCastlingMove()
    {
        return false;
    }

    public Figure getAttackedFigure()
    {
        return null;
    }

    private int getCurrentCoordinate() {
        if (this.getMovedFigure() != null)
       return this.getMovedFigure().getFigurePos();
        else
            return -1;
    }

    public int getTargetCoordinate() {
        return this.targetCoordinate;
    }

    public Board execute()
    {
        final Builder builder = new Builder();

        for(Figure figure : this.board.getCurrentPlayer().getAliveFigures())
        {
            if(!this.movedFigure.equals(figure))
            {
                builder.setFigure(figure);
            }
        }
        for(Figure figure : this.board.getCurrentPlayer().getOpponent().getAliveFigures())
        {
            builder.setFigure(figure);
        }
        //Двигаем фигуру после выставления доски.
        builder.setFigure(this.movedFigure.moveFigure(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getSide());
        return builder.build();
    }

    public static final class MajorMove extends Move
    {

        public MajorMove(Board board,
                         Figure movedFigure,
                         int targetCoordinate) {
            super(board, movedFigure, targetCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof MajorMove && super.equals(obj);
        }
        @Override
        public Board execute() {
            return super.execute();
        }

    }

    public static class AttackMove extends Move
    {
        final Figure attakedFigure;

       public AttackMove(Board board,
                         Figure movedFigure,
                         int targetCoordinate,
                         Figure atkdFigure) {
            super(board, movedFigure, targetCoordinate);
            this.attakedFigure = atkdFigure;
        }
        //TODO
        @Override
        public Board execute() {
            final Builder builder = new Builder();

            for(Figure figure : this.board.getCurrentPlayer().getAliveFigures())
            {
                if(!this.movedFigure.equals(figure))
                {
                    builder.setFigure(figure);
                }
            }
            for(Figure figure : this.board.getCurrentPlayer().getOpponent().getAliveFigures())
            {
                if(!this.attakedFigure.equals(figure))
                builder.setFigure(figure);
            }
            //Двигаем фигуру после выставления доски.
            builder.setFigure(this.movedFigure.moveFigure(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getSide());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Figure getAttackedFigure() {
            return this.attakedFigure;
        }

        @Override
        public int hashCode() {
            return this.attakedFigure.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
           if (this == obj)
               return true;
           if (!(obj instanceof AttackMove))
               return false;
               AttackMove otherAttackMove = (AttackMove)obj;
            return super.equals(otherAttackMove)
                    && getAttackedFigure().equals(otherAttackMove.getAttackedFigure());
        }
    }
    //TODO
    public static final class MinorMove extends Move
    {
        public MinorMove(Board board, Figure movedFigure, int targetCoordinate) {
            super(board, movedFigure, targetCoordinate);
        }

        @Override
        public boolean equals(Object obj)
        {
            return this == obj || obj instanceof MinorMove && super.equals(obj);
        }

        public Board execute() {
            final Builder builder = new Builder();

            for(Figure figure : this.board.getCurrentPlayer().getAliveFigures())
            {
                if(!this.movedFigure.equals(figure))
                {
                    builder.setFigure(figure);
                }
            }
            for(Figure figure : this.board.getCurrentPlayer().getOpponent().getAliveFigures())
            {
                builder.setFigure(figure);
            }
            if (!BoardUtils.EIGHT_ROW[this.targetCoordinate] && !BoardUtils.FIRST_ROW[this.targetCoordinate])
            {
                builder.setFigure(this.movedFigure.moveFigure(this));
            }
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getSide());
            return builder.build();
        }
    }
    //TODO
    public static class MinorAttackMove extends AttackMove
    {
        public MinorAttackMove(Board board,
                               Figure movedFigure,
                               int targetCoordinate,
                               Figure atkdFigure) {
            super(board, movedFigure, targetCoordinate, atkdFigure);
        }

        @Override
        public boolean equals(Object obj)
        {
            return this == obj || obj instanceof MinorAttackMove && super.equals(obj);
        }
    }
    //TODO
    public static final class OnPassAttackMove extends MinorAttackMove
    {
        public OnPassAttackMove (Board board,
                                Figure movedFigure,
                                int targetCoordinate,
                                Figure atkdFigure) {
            super(board, movedFigure, targetCoordinate, atkdFigure);
        }

        @Override
        public boolean equals(Object obj)
        {
            return this == obj || obj instanceof OnPassAttackMove && super.equals(obj);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(Figure figure:this.board.getCurrentPlayer().getAliveFigures())
            {
                if(!this.movedFigure.equals(figure))
                {
                    builder.setFigure(figure);
                }

            }

            for(Figure figure:this.board.getCurrentPlayer().getOpponent().getAliveFigures())
            {
                if(!this.getAttackedFigure().equals(figure))
                builder.setFigure(figure);
            }
            builder.setFigure(this.movedFigure.moveFigure(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getSide());
            return builder.build();

        }
    }

    public static class ReplaceMove extends Move
    {
        final Move wrappedMove;
        final Pawn upgradedPawn;

        public ReplaceMove(Move wrappedMove) {
            super(wrappedMove.board,wrappedMove.getMovedFigure(),wrappedMove.getTargetCoordinate());
            this.wrappedMove = wrappedMove;
            this.upgradedPawn = (Pawn) wrappedMove.getMovedFigure();
        }

        @Override
        public Board execute() {
            final Board wrappedBoard = this.wrappedMove.execute();
            final Builder builder = new Builder();
            for(Figure figure:wrappedBoard.getCurrentPlayer().getAliveFigures()) {
                if (!this.movedFigure.equals(figure)) {
                    builder.setFigure(figure);
                }
            }
            for(Figure figure:wrappedBoard.getCurrentPlayer().getOpponent().getAliveFigures()) {
                    builder.setFigure(figure);
            }
            builder.setFigure(this.upgradedPawn.getUpgradedFigure().moveFigure(this));
            builder.setMoveMaker(wrappedBoard.getCurrentPlayer().getSide());
            return builder.build();


            }

        @Override
        public boolean isAttack() {
            return this.wrappedMove.isAttack();
        }

        @Override
        public Figure getAttackedFigure() {
            return this.wrappedMove.getAttackedFigure();
        }

        @Override
        public Figure getMovedFigure() {
            return this.wrappedMove.getMovedFigure();
        }
    }

    public static final class PawnJumpMove extends Move
    {

        public PawnJumpMove(Board board, Figure movedFigure, int targetCoordinate) {
            super(board, movedFigure, targetCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(Figure figure:this.board.getCurrentPlayer().getAliveFigures())
            {
                if(!this.movedFigure.equals(figure))
                {
                    builder.setFigure(figure);
                }
            }
            for(Figure figure:this.board.getCurrentPlayer().getOpponent().getAliveFigures())
            {
                builder.setFigure(figure);
            }
            final Pawn movedPawn = (Pawn)this.movedFigure.moveFigure(this);
            builder.setFigure(movedPawn);
            builder.setOnPassPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getSide());
            return builder.build();
        }
    }

    public static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookTarget;

        public CastleMove(Board board,
                          Figure movedFigure,
                          int targetCoordinate,
                          Rook castleRook,
                          int castleRookStart,
                          int castleRookTarget) {
            super(board, movedFigure, targetCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookTarget = castleRookTarget;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }
        //TODO
        public boolean isCastleMove()
        {
            return true;
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            for(Figure figure:this.board.getCurrentPlayer().getAliveFigures())
            {
                if(!this.movedFigure.equals(figure) && !this.castleRook.equals(figure))
                {
                    builder.setFigure(figure);
                }
            }
            for(Figure figure:this.board.getCurrentPlayer().getOpponent().getAliveFigures())
            {
                builder.setFigure(figure);
            }
            // выставляем короля и ладью в соответсвующие позиции.
            builder.setFigure(this.movedFigure.moveFigure(this));
            builder.setFigure(new Rook( this.castleRookTarget, this.castleRook.getFigureSide()));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getSide());
            movedFigure.setFirstMove(false);
            return builder.build();
        }
    }

    public static final class QueenCastleMove extends CastleMove {

        public QueenCastleMove(Board board,
                               Figure movedFigure,
                               int targetCoordinate,
                               Rook castleRook,
                               int castleRookStart,
                               int castleRookTarget) {
            super(board, movedFigure, targetCoordinate,
                    castleRook,castleRookStart,castleRookTarget);

        }
        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static final class KingCastleMove extends CastleMove {

        public KingCastleMove(Board board,
                              Figure movedFigure,
                              int targetCoordinate,
                              Rook castleRook,
                              int castleRookStart,
                              int castleRookTarget) {
            super(board, movedFigure, targetCoordinate,
                    castleRook,castleRookStart,castleRookTarget);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class EmptyMove extends Move {

        public EmptyMove() {
            super(null, null, -1);
        }
        @Override
        public Board execute()
        {
            throw new RuntimeException("Empty move");
        }

    }

    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("It's a Factory");
        }
        public static Move createMove(Board board,
                                      int currentCoordinate,
                                      int targetCoordinate)
        {
            for (Move move: board.getAllMoves())
            {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getTargetCoordinate() == targetCoordinate)
                {
                    return move;
                }
            }
            return EMPTY_MOVE;
        }
    }


}
