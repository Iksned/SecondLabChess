package server;

import board.Board;

public class ChessParty {
   private Chesser playerThread1;
   private Chesser playerThread2;
   private static Board board;
   private boolean full;

    public ChessParty(Chesser player1) {
        this.playerThread1 = player1;
        board = playerThread1.getBoard();
        System.out.println(board.getCurrentPlayer().getSide());
        full = false;
    }

    public void addSecondPlayer(Chesser player2) {
        this.playerThread2 = player2;
        player2.getBoard().getCurrentPlayer().getSide();
        full = true;
    }

    public Chesser getPlayerThread2() {
        return playerThread2;
    }

    public void setOtherBoard(Board otherBoard) {
        if (playerThread1.getBoard().equals(otherBoard))
        {
            playerThread2.setBoard(otherBoard);
            playerThread2.sendBoard(otherBoard);
        }
        if (playerThread2.getBoard().equals(otherBoard))
        {
            playerThread1.setBoard(otherBoard);
            playerThread1.sendBoard(otherBoard);
        }
    }

    public boolean isFull() {
        return full;
    }
}
