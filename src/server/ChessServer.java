package server;

import board.Board;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Chesser extends Thread {
    private Socket socket;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private Board board;
    private String login = "";
    private ChessParty party;
    private ChessPlayer opponent;
    private ChessPlayer currentPlayer;
    private boolean waiting = false;


    public Chesser(Socket s) throws IOException {
        socket = s;
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objIn = new ObjectInputStream(socket.getInputStream());

        start();
    }

    public void run() {
        try {
            while (true) {
                while (login.equals("")) {
                    String loginIn = (String) objIn.readObject();
                    if (loginIn.equals(""))
                        break;
                    for (ChessPlayer player : ChessServer.getPlayers()) {
                        if (loginIn.equals(player.getLogin())) {
                            login = loginIn;
                            player.setPlayerState(true);
                            objOut.writeObject("Accept");
                            currentPlayer = player;
                        }
                    }
                    if (login.equals(""))
                        objOut.writeObject("Not Accepted");
                }
                while (opponent == null) {
                    String[] opponents = new String[ChessServer.getPlayers().size()];
                    int counter = 0;
                    for (ChessPlayer player:ChessServer.getPlayers())
                    {
                        if (player.isPlayerState() && !player.getLogin().equals(login))
                        {
                            opponents[counter] = player.getLogin();
                            counter++;
                        }
                    }
                    if(opponents.length>0)
                        objOut.writeObject(opponents);

                    String opp = (String) objIn.readObject();
                    for (ChessPlayer player:ChessServer.getPlayers())
                    {
                        if (player.getLogin().equals(opp)) {
                           opponent = player;
                        }
                    }
                    if (opp.equals("Wait") || opponent == null)
                    {
                        waiting = true;
                        opponent = new ChessPlayer("Bot",0.0);
                    }
                }
                //TODO победа/поражение
                if (board == null) {
                    opponent.getLogin();
                    board = (Board) objIn.readObject();
                    if (board.getCurrentPlayer().isMate())
                        break;
                    if (party == null && opponent.getLogin().equals("Bot"))
                    {
                        party = new ChessParty(this);
                        ChessServer.addChessParty(login,party);
                    }
                    else
                    {
                        party = ChessServer.getChessParty(opponent.getLogin());
                        party.addSecondPlayer(this);
                    }
                }
                else
                {
                    board = (Board)objIn.readObject();
                    party.setOtherBoard(board);
                    currentPlayer.setPlayerState(false);
                }
            }
        }
        catch (IOException e) {
            System.err.println("IO Exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                currentPlayer.setPlayerState(false);
                objIn.close();
                objOut.close();
                socket.close();
            }
            catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

    public Board getBoard() {
        if (board != null)
             return board;
        else
             return null;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void sendBoard(Board board)
    {
        try {
            objOut.writeObject(board);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ChessServer {
   private static final int PORT = 4444;
   private static List<Thread> clients = new ArrayList<>();
   private static Map<String,ChessParty> parties = new HashMap<>();
   private static List<ChessPlayer> players = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        players.add(new ChessPlayer("Player",140.2));
        players.add(new ChessPlayer("Player2",200.5));
        players.add(new ChessPlayer("Player3",10.3));
        ChessPlayer oppon = new ChessPlayer("Fake",500.5);
        oppon.setPlayerState(true);
       // players.add(oppon);
        System.out.println("Server Started");
        try (ServerSocket s = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = s.accept();
                try {
                    Chesser th = new Chesser(socket);
                    clients.add(th);
                    System.out.println("Connected");
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }

    public static ChessParty getChessParty(String name)
    {
        return parties.get(name);
    }

    public static void addChessParty(String name,ChessParty party)
    {
        parties.put(name,party);
    }

    public static List<ChessPlayer> getPlayers() {
        return players;
    }
}
