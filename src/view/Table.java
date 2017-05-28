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
import static javax.swing.SwingConstants.CENTER;
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
    private String title;
    private String login;

    private Socket fromserver = null;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;

    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    private final static Dimension FRAME_DIMENSION = new Dimension(600,600);


    public Table() {
        this.selfTable = this;
        this.title = "SecondLabChess";
        this.gameFrame = new JFrame(title);
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
        final JMenuItem chooseOpponent = new JMenuItem("Choose Opponent");
        final JMenuItem exit = new JMenuItem("Exit");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (fromserver == null)
                    try {
                        System.out.println("Connecting to server...");
                        fromserver = new Socket("localhost",4444);
                        connect.setLabel("Disconnet");
                        chooseOpponent.setEnabled(true);
                    } catch (IOException e1) {
                        System.out.println("Fail to connect");
                    }
                if (connect.getLabel().equals("Disconnect") && objOut != null) {
                    closeConnection();
                }
                if (fromserver != null)
                    try {
                        objOut = new ObjectOutputStream(fromserver.getOutputStream());
                        objOut.flush();
                        objIn = new ObjectInputStream(fromserver.getInputStream());
                        board = Board.createDefaultBoard();
                        boardPanel.redrawBoard(board);
                    new LoginFrame(objOut,objIn,selfTable);

                    System.out.println("Connected");
                }
                catch (IOException e1) {
                    connect.setLabel("Connect to server");
                    chooseOpponent.setEnabled(false);
                    gameFrame.setTitle(title);
                    closeConnection();
                    System.out.println("Disconnected");
                }
            }
        });
        netMenu.add(connect);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        chooseOpponent.setEnabled(false);
        chooseOpponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    objOut.writeObject("Choose");
                    objOut.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                invokeOpponentsFrame(login);
            }
        });
        netMenu.addSeparator();
        netMenu.add(chooseOpponent);
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
            for(int i = 0; i< BoardUtils.NUM_TILES;i++) {
                final TilePanel tilePanel = new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void redrawBoard(Board board) {
            removeAll();
            for (TilePanel tilePanel: boardDirection.flipper(boardTiles)) {
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
                    else if (isLeftMouseButton(e) && (playerSide == board.getCurrentPlayer().getSide() || fromserver == null)) {
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
                                        objOut.flush();
                                        if (board.getCurrentPlayer().isMate()) {
                                            JFrame endFrame = new JFrame("You WIN");
                                            endFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                                            endFrame.setPreferredSize(new Dimension(200,100));
                                            endFrame.setBounds(400,600,200,100);
                                            endFrame.add(new JLabel("You WIN"),CENTER);
                                            endFrame.validate();
                                            endFrame.setVisible(true);
                                            invokeOpponentsFrame(login);
                                        }
                                        else
                                           setBoard();
                                    } catch (IOException e1) {
                                        System.out.println("No connection");
                                        closeConnection();
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

        public void highLight (Board board) {
            if(showPosMoves)
            for (Move move:getFigureMovesOnBoard(board))
                if(this.tileId == move.getTargetCoordinate()) {
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
                            board = (Board)objIn.readObject();  // считывание из другого потока
                        boardPanel.redrawBoard(board);
                        if (board.getCurrentPlayer().isMate()) {
                            objOut.writeObject(board);          // отправка в свой поток
                            JFrame endFrame = new JFrame("You LOSE");
                            endFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                            endFrame.setPreferredSize(new Dimension(200,100));
                            endFrame.setBounds(400,600,200,100);
                            endFrame.add(new JLabel("You LOSE"),CENTER);
                            endFrame.validate();
                            endFrame.setVisible(true);
                            invokeOpponentsFrame(login);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        closeConnection();
                    }
                }
            }
        });
        setThread.start();
    }

    public void invokeOpponentsFrame(String login)
    {
        this.login = login;
        this.gameFrame.setTitle(title + " - " + login);
        new OpponentsFrame(objIn,objOut,selfTable);
    }

    public void setPreparations()
    {
        this.board = Board.createDefaultBoard();
        boardPanel.redrawBoard(board);
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

    void closeConnection()
    {
        if (this.fromserver != null) {
            try {
                this.objOut.close();
                this.objIn.close();
                this.fromserver.close();
                this.fromserver = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayerSide(FigureSide playerSide) {
        this.playerSide = playerSide;
    }
}
