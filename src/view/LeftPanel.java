package view;

import board.Move;
import figures.Figure;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LeftPanel extends JPanel {

    private static Dimension LEFT_PANE_DIM = new Dimension(40,80);
    private final Dimension NUBERS_DIMENSION = new Dimension(10,10);
   // private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public LeftPanel() {
        super(new GridLayout(8,1));
        setBorder(new EtchedBorder(EtchedBorder.RAISED));
        setPreferredSize(LEFT_PANE_DIM);
        for (int i = 8;i>0;i--)
        {
            this.add(new Numbers(Integer.toString(i)));
        }
        repaint();
        validate();
    }

    private class Numbers extends JPanel
    {
        public Numbers(String text) {
            super(new GridBagLayout());
            this.setPreferredSize(NUBERS_DIMENSION);
            JLabel numLabel = new JLabel(text);
            numLabel.setForeground(Color.WHITE);
            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            this.add(numLabel);
            this.setBackground(Color.GRAY);
            repaint();
            validate();
        }

    }
}
