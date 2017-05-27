package view;

import figures.Figure;
import player.FigureSide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;

public class LoginFrame extends JFrame{

    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private Table table;

    private final Dimension LOGIN_FRAME_DIMENSION = new Dimension(200,100);

    public LoginFrame(ObjectOutputStream out1, ObjectInputStream in1, Table mainTable) throws HeadlessException {
        //TODO clean
        this.setName("Введите логин");
        this.setPreferredSize(LOGIN_FRAME_DIMENSION);
        JPanel mainpan = new JPanel();
        this.objOut = out1;
        this.objIn = in1;
        this.table = mainTable;
        this.addWindowListener(initWinListener());
        mainpan.setLayout(new BorderLayout());
        this.fillPanel(mainpan);
        this.add(mainpan);
        this.setBounds(500,500,500,500);
        validate();
        pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    private void fillPanel(JPanel mainpan) {
        final JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(100,10));
        JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(50,20));
        JLabel label = new JLabel("Введите логин");
        textField.validate();
        okButton.validate();
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loggin(textField);
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               loggin(textField);
            }
        });
        mainpan.add(textField,BorderLayout.CENTER);
        mainpan.add(okButton,BorderLayout.SOUTH);
        mainpan.add(label,BorderLayout.NORTH);
    }

    private void loggin(JTextField textField)
    {
        String login = textField.getText();
        try {
            objOut.writeObject(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String in1 = (String) objIn.readObject();
            System.out.println(in1);
            if (in1.equals("Accept")) {
                table.invokeOpponentsFrame(login);
                setVisible(false);
            }
            else {
                textField.setText("");
            }
        } catch (IOException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    private WindowListener initWinListener() {
        return new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    objOut.writeObject("Reset");
                    table.closeConnection();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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
