package view;

import board.BoardUtils;
import board.Move;
import figures.*;
import player.FigureSide;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static javax.swing.SwingUtilities.isLeftMouseButton;

public class ReplaceFrame extends JFrame{
    private final Color lightTileColor = Color.decode("#f2f2f2");
    private final Color darkTileColor = Color.decode("#635844");


    private FigureSide side;
    private int fCoordinate;
    private final Pawn replacedPawn;
    private static String imagePath = "images/";

    private final static Dimension REPLACE_FRAME_DIMENSION = new Dimension(200,100);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    public ReplaceFrame(Pawn pawn) {
        this.setName("Choose Figure");
        this.setLayout(new BorderLayout());
        this.setSize(REPLACE_FRAME_DIMENSION);
        this.replacedPawn = pawn;
        this.side = pawn.getFigureSide();
       // this.fCoordinate = pawn.getFigurePos();
        this.add(new ReplacePanel(side));
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


    }

    private class ReplacePanel extends JPanel
    {
        final java.util.List<ReplaceTile> boardTiles;

        ReplacePanel(FigureSide side)
        {
            super(new GridLayout(1,4));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i< 4;i++)
            {
                final ReplaceTile tilePanel = new ReplaceTile(side,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(REPLACE_FRAME_DIMENSION);
            validate();
            repaint();
        }
    }

    protected void setVisibleNow()
    {
        this.setVisible(false);
    }

    private class ReplaceTile extends JPanel
    {
        private final int tileId;
        private final FigureSide side;

        ReplaceTile(final FigureSide side, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            this.side = side;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTexture(tileId);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isLeftMouseButton(e)) {
                            Figure figure = chooseFigure(replacedPawn.getFigurePos(),replacedPawn.getFigureSide());
                            replacedPawn.setReplacedFigure(figure);
                            setVisibleNow();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }

        private void assignTexture(int tileId)
        {
            Map<Integer,String> figures = new HashMap<>();
            figures.put(0,"q");
            figures.put(1,"b");
            figures.put(2,"n");
            figures.put(3,"r");
            this.removeAll();
            try {
                final BufferedImage image = ImageIO.read(new File(imagePath  + side.toString().substring(0,1)+
                        figures.get(tileId) + ".png"));
                add(new JLabel(new ImageIcon(image)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void assignTileColor() {
            final int tilePositionX = this.tileId / 8;
            final int tilePositionY = this.tileId % 8;
            setBackground((tilePositionX + tilePositionY) % 2 == 0 ? lightTileColor : darkTileColor);
        }

       private Figure chooseFigure(int pos, FigureSide side) {
            if(this.tileId == 0) {
                this.setVisible(false);
                return new Queen(pos, side);}
            else if (this.tileId == 1){
                this.setVisible(false);
                return new Bishop(pos,side);}
            else if (this.tileId == 2) {
                this.setVisible(false);
                return new Knight(pos,side);}
            else if (this.tileId == 3) {
                this.setVisible(false);
                return new Rook(pos, side);}
            else return new Pawn(pos,side);

        }

    }

}