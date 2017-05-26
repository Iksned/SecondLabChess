package player;

import board.BoardUtils;

import java.io.Serializable;

public enum FigureSide implements Serializable{
    WHITE {
        @Override
      public int getDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isUpgradeTile(int pos) {
            return BoardUtils.FIRST_ROW[pos];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
      public  int getDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isUpgradeTile(int pos) {
            return BoardUtils.EIGHT_ROW[pos];
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

   public abstract int getDirection();
   public abstract boolean isWhite();
   public abstract boolean isBlack();

   public abstract boolean isUpgradeTile(int pos);

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
