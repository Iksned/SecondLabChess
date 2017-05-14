package view;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Move.MoveFactory;
import board.Tile;
import figures.Figure;
import player.MoveTrasition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final Color lightTileColor = Color.decode("#f2f2f2");
    private final Color darkTileColor = Color.decode("#635844");

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board board;
    private static String imagePath = "images/";

    private Tile currentTile;
    private Tile targetTile;
    private Figure movedFigure;

    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    private final static Dimension FRAME_DIMENSION = new Dimension(600,600);

    public Table()
    {
        this.gameFrame = new JFrame("SecondLabChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = fillMenuBar();
        this.board = Board.createDefaultBoard();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(FRAME_DIMENSION);
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JMenuBar fillMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
        }

        //TODO добавить соединение с сервером
    private JMenu createFileMenu() {

        final JMenu netMenu = new JMenu("Net");
        final JMenuItem connect = new JMenuItem("Connect to server");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Connecting to server... Пока не реализовано");
            }
        });
        netMenu.add(connect);
        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        netMenu.add(exit);
        return netMenu;
    }

    private class BoardPanel extends JPanel
    {
        final List<TilePanel> boardTiles;

        BoardPanel()
        {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i< BoardUtils.NUM_TILES;i++)
            {
                final TilePanel tilePanel = new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void redrawBoard(Board board) {
            removeAll();
            for (TilePanel tilePanel: boardTiles)
            {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

        public void redrawBoardMax(Board board,Figure figure) {
            removeAll();
            for (TilePanel tilePanel: boardTiles)
            {
                tilePanel.drawTileMax(board,figure);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel{

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId)
        {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTexture(board);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if(isRightMouseButton(e)) {
                        currentTile = null;
                        targetTile = null;
                        movedFigure = null;
                    }
                    else if (isLeftMouseButton(e)) {
                        if (currentTile == null)
                        {//TODO подсветка ходов
                            currentTile = board.getTile(tileId);
                            movedFigure = currentTile.getFigure();
                            System.out.println("Clicked");

                            if (movedFigure == null)
                            {
                                currentTile = null;}
                            boardPanel.redrawBoardMax(board,movedFigure);
                        }
                        else {
                            targetTile = board.getTile(tileId);
                            final Move move = MoveFactory.createMove(board, currentTile.gettCoordinate(),targetTile.gettCoordinate());
                            final MoveTrasition trasition = board.getCurrentPlayer().makeMove(move);
                            if (trasition.getMoveStatus().isDone())
                            {
                                board = trasition.getBoard();
                            }//TODO логирование
                            currentTile = null;
                            targetTile = null;
                            movedFigure = null;
                            boardPanel.redrawBoard(board);

                        }
                        //TODO посмотреть преимущества
                       /* SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.redrawBoard(board);
                            }
                        });*/
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

            validate();
        }


        public void drawTile(Board board)
        {
            assignTileColor();
            assignTexture(board);
            validate();
            repaint();
        }

        public void drawTileMax(Board board,Figure figure)
        {
            assignTileColor();
            assignTextureMax(board,figure);
            validate();
            repaint();
        }

        private void assignTexture(Board board)
        {
            this.removeAll();
            if(board.getTile(this.tileId).isOccupied())
            {
                try {
                    final BufferedImage image = ImageIO.read(new File(imagePath  + board.getTile(this.tileId).getFigure().getFigureSide().toString().substring(0,1)+
                            board.getTile(this.tileId).getFigure().toString().toLowerCase() + ".png"));
                    add(new JLabel(new ImageIcon(image)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTextureMax(Board board,Figure figure)
        {
            this.removeAll();
            if(board.getTile(this.tileId).isOccupied())
            {
                try {
                    final BufferedImage image = ImageIO.read(new File(imagePath  + board.getTile(this.tileId).getFigure().getFigureSide().toString().substring(0,1)+
                            board.getTile(this.tileId).getFigure().toString().toLowerCase() + ".png"));
                    add(new JLabel(new ImageIcon(image)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {Collection<Move> moves = figure.calcMoves(board);
                for (Move move:moves)
                    if(board.getTile(tileId).gettCoordinate() == move.getTargetCoordinate())
                {
                    try {
                        final BufferedImage image = ImageIO.read(new File(imagePath  + "pborder" + ".png"));
                        add(new JLabel(new ImageIcon(image)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void assignTileColor() {
                final int tilePositionX = this.tileId / 8;
                final int tilePositionY = this.tileId % 8;
                setBackground((tilePositionX + tilePositionY) % 2 == 0 ? lightTileColor : darkTileColor);
        }
    }

}
