package player;

import board.Board;
import board.Move;
import board.Tile;
import figures.Figure;
import figures.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {
    public BlackPlayer(Board board, Collection<Move> whiteMovesSum, Collection<Move> blackMovesSum) {
        super(board, blackMovesSum,whiteMovesSum);
    }

    @Override
    public Collection<Figure> getAliveFigures() {
        return this.board.getBlackFigures();
    }

    @Override
    public FigureSide getSide() {
        return FigureSide.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.WhitePlayer();
    }

    @Override
    protected Collection<Move> calcKingCastles(Collection<Move> playerMoves,
                                               Collection<Move> opponentMoves) {
        final List<Move> kingCastless = new ArrayList<>();

        if(this.pKing.isFirstMove() && !this.isCheck()) {
            if (!this.board.getTile(5).isOccupied() &&
                    !this.board.getTile(6).isOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                if (rookTile.isOccupied() && rookTile.getFigure().isFirstMove()) {
                    if(Player.calcAttackOnTile(5,opponentMoves).isEmpty() &&
                            Player.calcAttackOnTile(6,opponentMoves).isEmpty() &&
                            rookTile.getFigure().getFigureType().isRook())
                    {kingCastless.add(new Move.KingCastleMove(this.board,this.pKing,6,
                                (Rook)rookTile.getFigure(),rookTile.gettCoordinate(),5));
                    }
                }
            }
            if (!this.board.getTile(1).isOccupied() &&
                    !this.board.getTile(2).isOccupied()&&
                    !this.board.getTile(3).isOccupied())
            {final Tile rookTile = this.board.getTile(0);
                if(rookTile.isOccupied() && rookTile.getFigure().isFirstMove()) {
                    if(Player.calcAttackOnTile(2,opponentMoves).isEmpty() &&
                            Player.calcAttackOnTile(3,opponentMoves).isEmpty() &&
                            rookTile.getFigure().getFigureType().isRook())
                    {
                        kingCastless.add(new Move.QueenCastleMove(this.board,this.pKing,2,
                                (Rook)rookTile.getFigure(),rookTile.gettCoordinate(),3));
                    }
                }
            }
        }

        return kingCastless;
    }
}
