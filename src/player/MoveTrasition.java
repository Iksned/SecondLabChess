package player;

import board.Board;
import board.Move;

public class MoveTrasition {

    private final Board transitBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTrasition(Board transitBoard,
                         Move move,
                         MoveStatus moveStatus) {
        this.transitBoard = transitBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public Board getBoard() {
        return transitBoard;
    }
}
