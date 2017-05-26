package server;

import board.Board;
import player.FigureSide;

public class ChessPlayer {
    private String login;
    private Double score;
    private boolean playerState;
    private FigureSide side;
    private Board board;

    public ChessPlayer(String login, Double score) {
        this.login = login;
        this.score = score;
        this.playerState = false;
        this.side = null;
    }

    public String getLogin() {
        return login;
    }

    public Double getScore() {
        return score;
    }

    public void setPlayerState(boolean playerState) {
        this.playerState = playerState;
    }

    public void setSide(FigureSide side) {
        this.side = side;
    }

    public FigureSide getSide() {
        return side;
    }

    public boolean isPlayerState() {
        return playerState;
    }
}