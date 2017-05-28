package server;

import board.Board;

public class ChessParty {
   private Chesser playerThread1;
   private Chesser playerThread2;
   private Board board;
   private boolean full;
   private String partyName;

    public ChessParty(Chesser player1) {
        this.playerThread1 = player1;
        board = playerThread1.getBoard();
        partyName = playerThread1.getCurrentPlayer().getLogin();
        System.out.println(board.getCurrentPlayer().getSide());
        full = false;
    }

    public void addSecondPlayer(Chesser player2) {
        this.playerThread2 = player2;
        player2.getBoard().getCurrentPlayer().getSide();
        this.full = true;
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

    public String getPartyName() {
        return partyName;
    }

    public boolean isFull() {
        return full;
    }

    public void setPartyNull(Chesser chesser) {
        if (chesser.equals(playerThread1))
            playerThread2.setParty(null);
        if (chesser.equals(playerThread2))
            playerThread1.setParty(null);
    }
}
