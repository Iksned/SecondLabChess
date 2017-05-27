package view;

import javafx.scene.control.Tab;
import player.FigureSide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OpponentsFrame extends JFrame {

    private final Dimension OPPONENT_FRAME_DIMENSION = new Dimension(200,400);
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private String[] logins;
    private Table table;

    public OpponentsFrame(ObjectInputStream inputStream, ObjectOutputStream outputStream,Table selfTable) throws HeadlessException {
        this.setName("Введите логин");
        this.setPreferredSize(OPPONENT_FRAME_DIMENSION);
        this.objIn = inputStream;
        this.objOut = outputStream;
        this.table = selfTable;
        this.logins = createLogins();
        this.addWindowListener(initWinListener());
        JPanel mainpan = new JPanel();
        mainpan.setLayout(new BorderLayout());
        this.fillPanel(mainpan);
        this.add(mainpan);
        this.setBounds(600,400,500,500);
        validate();
        pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    private String[] createLogins() {
        String[] logs = null;
        try {
            logs = (String[])objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return logs;
    }

    private void fillPanel(JPanel mainpan) {
        final JList<String> opponents = new JList(logins);
        opponents.setLayoutOrientation(JList.VERTICAL);
        opponents.setVisibleRowCount(0);
        opponents.setPreferredSize(OPPONENT_FRAME_DIMENSION);
        JScrollPane scroll = new JScrollPane(opponents);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(100, 100));
        add(scroll,BorderLayout.CENTER);
        JLabel headLabel = new JLabel("Choose opponent");
        JButton okButton = new JButton("Ok");
        JButton waitButton = new JButton("Wait");
        add(okButton,BorderLayout.SOUTH);
       // add(waitButton);
        add(new JLabel("Choose your opponent"),BorderLayout.NORTH);
        pack();
        validate();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String opponent = opponents.getSelectedValue();
                try {
                    if (opponent == null) {
                        objOut.writeObject("Wait");
                        table.setPlayerSide(FigureSide.WHITE);
                        table.setPreparations();
                        setVisible(false);
                    }
                    else {
                        objOut.writeObject(opponent);
                        table.setPlayerSide(FigureSide.BLACK);
                        table.setPreparations();
                        setVisible(false);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        waitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    objOut.writeObject("Wait");
                    table.setPlayerSide(FigureSide.WHITE);
                    table.setPreparations();
                    setVisible(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private WindowListener initWinListener() {
        return new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    objOut.writeObject("Wait");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                table.setPlayerSide(FigureSide.WHITE);
                table.setPreparations();
                setVisible(false);
            }
            @Override
            public void windowClosed(WindowEvent e) {
            }
            @Override
            public void windowIconified(WindowEvent e) {
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            @Override
            public void windowActivated(WindowEvent e) {
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
    }

}
