package player;

import addons.Support;
import board.Board;
import board.Move;
import figures.Figure;
import figures.King;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static addons.Support.makeListFromIterable;

public abstract class Player implements Serializable{
    protected final Board board;
    protected final King pKing;
    protected final Collection<Move> passMoves;
    private  final boolean isInCheck;
    private final Collection<Move> kingMoves;

    Player(Board board,Collection<Move> passMoves,
                        Collection<Move> opponentMoves)
    {
        this.board = board;
        this.pKing = setKing();
        this.passMoves = makeListFromIterable(Support.concat(passMoves,calcKingCastles(passMoves,opponentMoves)));
        this.isInCheck = !Player.calcAttackOnTile(this.pKing.getFigurePos(),opponentMoves).isEmpty();
        this.kingMoves = calcKingCastles(passMoves,opponentMoves);
    }

    protected static Collection<Move> calcAttackOnTile(int figurePos, Collection<Move> opponentMoves) {
        final List<Move> checkMoves = new ArrayList<>();
        for (Move move: opponentMoves)
        {
            if (figurePos == move.getTargetCoordinate())
                checkMoves.add(move);
        }
        return checkMoves;
    }

    private King setKing() {
        for (Figure figure:getAliveFigures())
        {
            if (figure.getFigureType().isKing())
            {
                return (King)figure;
            }
        }
        throw new RuntimeException("No King");
    }

    public Collection<Move> getKingMoves() {
        return kingMoves;
    }

    public boolean isMoveLegal(Move move)
    {
        return this.passMoves.contains(move);
    }

    public boolean isCheck()
    {
        return this.isInCheck;
    }

    public boolean isMate()
    {
        return this.isCheck() && !hasEscapeMoves();
    }

    public boolean isPat()
    {
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        for (Move move:this.passMoves)
        {
           final MoveTrasition trasition = makeMove(move);
           if(trasition.getMoveStatus().isDone())
           {
               return true;
           }
        }
        return false;
    }

    public boolean isCastled()
    {
        return false;
    }

    public MoveTrasition makeMove(Move move)
    {
        if (!isMoveLegal(move))
        {
            return new MoveTrasition(this.board,move,MoveStatus.ILLEGAL_MOVE);
        }

        final Board transBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calcAttackOnTile(transBoard.getCurrentPlayer().getOpponent().getPlayerKing().getFigurePos(),
                transBoard.getCurrentPlayer().getPassMoves());
        if (!kingAttacks.isEmpty())
        {
            return new MoveTrasition(this.board,move,MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTrasition(transBoard,move,MoveStatus.DONE);
    }

    public abstract Collection<Figure> getAliveFigures();

    public abstract FigureSide getSide();

    public abstract Player getOpponent();

    protected abstract Collection<Move> calcKingCastles(Collection<Move> playerMoves,
                                                        Collection<Move> opponentMoves);

    public Figure getPlayerKing() {
        return this.pKing;
    }

    public Collection<Move> getPassMoves() {
        return passMoves;
    }
}
