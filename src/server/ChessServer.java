package server;

import board.Board;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Пока создано 3 проверочных игрока с логинами Player,Player2,Player3
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
           G: while (true) {
                while (login.equals("")) {
                    String loginIn = (String) objIn.readObject();
                    if (loginIn.equals("Reset"))
                        break G;
                    for (ChessPlayer player : ChessServer.getPlayers()) {
                        if (loginIn.equals(player.getLogin()) && !player.isOnline()) {
                            login = loginIn;
                            objOut.writeObject("Accept");
                            currentPlayer = player;
                            currentPlayer.setOnline(true);
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
                        if (player.isVisible() && !player.getLogin().equals(login))
                        {
                            opponents[counter] = player.getLogin();
                            counter++;
                        }
                    }
                    if(opponents.length>0)
                        objOut.writeObject(opponents);

                    String opp = (String) objIn.readObject();
                    for (ChessPlayer player:ChessServer.getPlayers()) {
                        if (player.getLogin().equals(opp))
                        {
                           opponent = player;
                           currentPlayer.setVisible(true);
                        }
                    }
                    if (opp.equals("Wait") || opponent == null) {
                        waiting = true;
                        opponent = new ChessPlayer("Bot",0.0);
                        currentPlayer.setVisible(true);
                    }
                }
                if (board == null) {
                    board = (Board) objIn.readObject();
                    if (party == null && opponent.getLogin().equals("Bot")) {
                        party = new ChessParty(this);
                        ChessServer.addChessParty(login,party);
                    }
                    else {
                        if (!ChessServer.getChessParty(opponent.getLogin()).isFull()) {
                            party = ChessServer.getChessParty(opponent.getLogin());
                            party.addSecondPlayer(this);
                        }
                    }
                }
                else {
                    Object obj = objIn.readObject();
                    if (obj.equals("Choose"))
                    {
                        this.opponent = null;
                        this.party = null;
                        this.board = null;
                    } else {
                        if (obj instanceof Board)
                            board = (Board) obj;
                        // board = (Board)objIn.readObject();
                        if (board.getCurrentPlayer().isMate()) {
                            if (party != null) {
                                currentPlayer.setScore(currentPlayer.getScore() + opponent.getScore() * 0.2);
                                opponent.setScore(opponent.getScore() - currentPlayer.getScore() * 0.2);
                                party.setOtherBoard(board);
                                party.setPartyNull(this);
                                ChessServer.removeParty(party.getPartyName());
                            }
                            this.opponent = null;
                            this.party = null;
                            this.board = null;

                        } else {
                            party.setOtherBoard(board);
                            currentPlayer.setVisible(false);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("IO Exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (currentPlayer != null) {
                    currentPlayer.setVisible(false);
                    currentPlayer.setOnline(false);
                }
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

    public void setParty(ChessParty party) {
        this.party = party;
    }

    public ChessPlayer getCurrentPlayer() {
        return currentPlayer;
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

    static ChessParty getChessParty(String name)
    {
        return parties.get(name);
    }

    static void addChessParty(String name, ChessParty party)
    {
        parties.put(name,party);
    }

    static List<ChessPlayer> getPlayers() {
        return players;
    }

    static void removeParty(String login)
    {
        parties.remove(login);
    }
}