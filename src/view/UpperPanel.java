package view;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class UpperPanel extends JPanel {

    private static Dimension LEFT_PANE_DIM = new Dimension(80, 40);
    private final Dimension NUBERS_DIMENSION = new Dimension(10, 10);
    // private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    
    public UpperPanel() {
        super(new GridLayout(1, 8));

        setPreferredSize(LEFT_PANE_DIM);
            for (int i = 8; i > 0; i--) {
            this.add(new Numbers(Integer.toString(i)));
        }
        repaint();
        validate();
    }

    private class Numbers extends JPanel {
        public Numbers(String text) {
            super(new GridBagLayout());
            this.setPreferredSize(NUBERS_DIMENSION);
            JLabel numLabel = new JLabel(text);
            numLabel.setForeground(Color.WHITE);
            this.add(numLabel);
            this.setBackground(Color.GRAY);
            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            repaint();
            validate();
        }

    }
}
