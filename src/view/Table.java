package view;

import addons.Support;
import board.Board;
import board.BoardUtils;
import board.Move;
import board.Move.MoveFactory;
import board.Tile;
import figures.Figure;
import figures.Pawn;
import player.FigureSide;
import player.MoveTrasition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;

import static addons.Support.makeListFromIterable;
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
    private BoardDirection boardDirection;
    private boolean showPosMoves;
    private FigureSide playerSide;
    private Table selfTable;

    private Socket fromserver = null;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;

    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    private final static Dimension FRAME_DIMENSION = new Dimension(600,600);


    public Table() {
        this.selfTable = this;
        this.gameFrame = new JFrame("SecondLabChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = fillMenuBar();
        this.board = Board.createDefaultBoard();
        this.playerSide = board.getCurrentPlayer().getSide();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.boardDirection = BoardDirection.NORMAL;
        this.showPosMoves = true;
        this.gameFrame.setSize(FRAME_DIMENSION);
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(new LeftPanel(),BorderLayout.WEST);
        this.gameFrame.add(new UpperPanel(),BorderLayout.NORTH);
        this.gameFrame.setResizable(false);

        this.gameFrame.setVisible(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JMenuBar fillMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPrefMenu());
        return tableMenuBar;
        }

    private JMenu createFileMenu() {
        final JMenu netMenu = new JMenu("Net");
        final JMenuItem connect = new JMenuItem("Connect to server");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (fromserver == null)
                    try {
                        System.out.println("Connecting to server...");
                        fromserver = new Socket("localhost",4444);
                       // connect.setLabel("Disconnet");
                    } catch (IOException e1) {
                        System.out.println("Fail to connect");
                    }
               /*if (connect.getLabel().equals("Disconnect"))
                    {
                        try {
                            objOut.close();
                            objIn.close();
                            fromserver.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                      fromserver = null;
                        connect.setLabel("Connect to server");
                    }*/
                if (fromserver != null)
                    try {
                    //TODO полноценый коннект
                        objOut = new ObjectOutputStream(fromserver.getOutputStream());
                        objOut.flush();
                        objIn = new ObjectInputStream(fromserver.getInputStream());
                    new LoginFrame(objOut,objIn,selfTable);

                    System.out.println("Connected");
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
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
        netMenu.addSeparator();
        netMenu.add(exit);
        return netMenu;
    }

    private JMenu createPrefMenu()
    {
        final JMenu preferences = new JMenu("Pref's");
        final JMenuItem flipper = new JMenuItem("Flip Board");
        final JCheckBoxMenuItem showMoves = new JCheckBoxMenuItem("Show possible moves");
        final JMenuItem chooseSide = new JMenuItem("White");
        flipper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.redrawBoard(board);
                boardDirection = boardDirection.opposite();

            }
        });
        showMoves.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPosMoves = !showPosMoves;
            }
        });
        chooseSide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chooseSide.getLabel().equals("White") && fromserver == null)
                {
                    playerSide = FigureSide.BLACK;
                    chooseSide.setLabel("Black");
                }
                else
                {
                    if (fromserver == null) {
                        playerSide = FigureSide.WHITE;
                        chooseSide.setLabel("White");
                    }
                }
            }
        });
        showMoves.setState(true);
        preferences.add(flipper);
        preferences.addSeparator();
        preferences.add(showMoves);
        preferences.addSeparator();
        preferences.add(chooseSide);
        return preferences;

    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> flipper(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return REVERSED;
            }
        },REVERSED {
            @Override
            List<TilePanel> flipper (List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return boardTiles;
            }
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<TilePanel> flipper(List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
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
            for (TilePanel tilePanel: boardDirection.flipper(boardTiles))
            {
                tilePanel.drawTile(board);
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
                        boardPanel.redrawBoard(board);
                    }
                    else if (isLeftMouseButton(e) && playerSide == board.getCurrentPlayer().getSide()) {
                        if (currentTile == null)
                        {
                            currentTile = board.getTile(tileId);
                            movedFigure = currentTile.getFigure();
                            System.out.println("Clicked");

                            if (movedFigure == null)
                            {
                                currentTile = null;}

                            boardPanel.redrawBoard(board);
                        }
                        else {
                            targetTile = board.getTile(tileId);
                            if (targetTile != currentTile) {

                                if (movedFigure instanceof Pawn &&
                                   ((Pawn) movedFigure).getReplacedFigure() == null &&
                                   ((BoardUtils.EIGHT_ROW[targetTile.gettCoordinate()] && movedFigure.getFigureSide().isBlack() && BoardUtils.SEVEN_ROW[currentTile.gettCoordinate()]) ||
                                    (BoardUtils.FIRST_ROW[targetTile.gettCoordinate()] && movedFigure.getFigureSide().isWhite() && BoardUtils.SECOND_ROW[currentTile.gettCoordinate()]))) {
                                         new ReplaceFrame((Pawn) movedFigure);
                                }
                                else {
                                    final Move move = MoveFactory.createMove(board, currentTile.gettCoordinate(), targetTile.gettCoordinate());
                                    final MoveTrasition trasition = board.getCurrentPlayer().makeMove(move);
                                    if (trasition.getMoveStatus().isDone()) {
                                        board = trasition.getBoard();
                                    }//TODO логирование
                                    currentTile = null;
                                    targetTile = null;
                                    movedFigure = null;
                                    boardPanel.redrawBoard(board);
                                    if (objOut != null && trasition.getMoveStatus().isDone())
                                    try {
                                        objOut.writeObject(board);
                                        setBoard();
                                    } catch (IOException e1) {
                                        System.out.println("No connection");
                                    }
                                }
                            }

                        }
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

        public void highLight (Board board)
        {if(showPosMoves)
            for (Move move:getFigureMovesOnBoard(board))
                if(this.tileId == move.getTargetCoordinate())
                {
                    try {
                        final BufferedImage image = ImageIO.read(new File(imagePath  + "pdot" + ".png"));
                        add(new JLabel(new ImageIcon(image)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

        public Collection<Move> getFigureMovesOnBoard (Board board)
        {
            if (currentTile != null && movedFigure.getFigureSide() == board.getCurrentPlayer().getSide() && board.getCurrentPlayer().getPlayerKing() == movedFigure)
                return makeListFromIterable(Support.concat(movedFigure.calcMoves(board),board.getCurrentPlayer().getKingMoves()));
            if(currentTile != null && movedFigure.getFigureSide() == board.getCurrentPlayer().getSide())
                return movedFigure.calcMoves(board);

            return Collections.emptyList();

        }


        public void drawTile(Board board)
        {
            assignTileColor();
            assignTexture(board);
            highLight(board);
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

        private void assignTileColor() {
                final int tilePositionX = this.tileId / 8;
                final int tilePositionY = this.tileId % 8;
                setBackground((tilePositionX + tilePositionY) % 2 == 0 ? lightTileColor : darkTileColor);
        }
    }

    private void setBoard ()
    {
        final Thread setThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (board.getCurrentPlayer().getSide() != playerSide)
                {
                    try {
                        if (objIn != null)
                            board = (Board)objIn.readObject();
                        boardPanel.redrawBoard(board);
                        System.out.println("Stil running");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        setThread.start();
    }

    public void invokeOpponentsFrame()
    {
        new OpponentsFrame(objIn,objOut,selfTable);

    }

    public void setPreparations()
    {
        System.out.println(playerSide);
        if (playerSide == FigureSide.BLACK)
        {
            boardDirection = boardDirection.opposite();
            boardPanel.redrawBoard(board);
            boardDirection = boardDirection.opposite();
        }
        try {
            objOut.writeObject(board);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBoard();
    }

    public void setPlayerSide(FigureSide playerSide) {
        this.playerSide = playerSide;
    }
}
