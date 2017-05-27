package server;

import board.Board;
import player.FigureSide;

import java.io.Serializable;

public class ChessPlayer implements Serializable{
    private String login;
    private Double score;
    private boolean visible;
    private boolean online;
    private FigureSide side;
    private Board board;

    public ChessPlayer(String login, Double score) {
        this.login = login;
        this.score = score;
        this.visible = false;
        this.online = false;
        this.side = null;
        this.board = null;
    }

    public String getLogin() {
        return login;
    }

    public Double getScore() {
        return score;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSide(FigureSide side) {
        this.side = side;
    }

    public FigureSide getSide() {
        return side;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}