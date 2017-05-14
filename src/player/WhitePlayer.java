package player;


import board.Board;
import board.Move;
import board.Tile;
import figures.Figure;
import figures.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player{

    public WhitePlayer(Board board, Collection<Move> whiteMovesSum, Collection<Move> blackMovesSum) {
        super(board,whiteMovesSum,blackMovesSum);
    }

    @Override
    public Collection<Figure> getAliveFigures() {
        return this.board.getWhiteFigures();
    }

    @Override
    public FigureSide getSide() {
        return FigureSide.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calcKingCastles(Collection<Move> playerMoves,
                                               Collection<Move> opponentMoves) {
        final List<Move> kingCastless = new ArrayList<>();

        if(this.pKing.isFirstMove() && !this.isCheck()) {
            if (!this.board.getTile(61).isOccupied() &&
                    !this.board.getTile(62).isOccupied()) {
                final Tile rookTile = this.board.getTile(63);
                if (rookTile.isOccupied() && rookTile.getFigure().isFirstMove()) {
                    if(Player.calcAttackOnTile(61,opponentMoves).isEmpty() &&
                            Player.calcAttackOnTile(62,opponentMoves).isEmpty() &&
                            rookTile.getFigure().getFigureType().isRook())
                    {
                        kingCastless.add(new Move.KingCastleMove(this.board,this.pKing,62,
                                         (Rook)rookTile.getFigure(),rookTile.gettCoordinate(),61));
                    }
                }
            }
            if (!this.board.getTile(59).isOccupied() &&
                    !this.board.getTile(58).isOccupied()&&
                    !this.board.getTile(57).isOccupied())
            {
                final Tile rookTile = this.board.getTile(56);
                if(rookTile.isOccupied() && rookTile.getFigure().isFirstMove()) {
                    if(Player.calcAttackOnTile(59,opponentMoves).isEmpty() &&
                            Player.calcAttackOnTile(58,opponentMoves).isEmpty() &&
                            rookTile.getFigure().getFigureType().isRook())
                    {
                        kingCastless.add(new Move.QueenCastleMove(this.board,this.pKing,58,
                                (Rook)rookTile.getFigure(),rookTile.gettCoordinate(),59));
                    }
                }
            }
        }

        return kingCastless;
    }
}
