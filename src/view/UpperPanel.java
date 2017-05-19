package view;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class UpperPanel extends JPanel {

    private static Dimension LEFT_PANE_DIM = new Dimension(80, 40);
    private final Dimension NUBERS_DIMENSION = new Dimension(10, 10);
    // private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public UpperPanel() {
        super(new BorderLayout());
        JPanel cornerAdd = new JPanel();
        cornerAdd.setPreferredSize(new Dimension(new Dimension(40,40)));
        cornerAdd.setBackground(Color.GRAY);

        this.add(cornerAdd,BorderLayout.WEST);
        this.add(new UpperAdd(),BorderLayout.CENTER);
        repaint();
        validate();
    }

    private class UpperAdd extends JPanel
    {
        List<String> lettrs = new ArrayList<>(Arrays.asList("a", "b", "c","d","e","f","g","h"));
        public UpperAdd() {
            super(new GridLayout(1, 8));

            setPreferredSize(LEFT_PANE_DIM);
            for (int i = 0; i < 8; i++) {
                this.add(new Letters(lettrs.get(i)));
            }
            repaint();
            validate();
        }
    }
    private class Letters extends JPanel {
        public Letters(String text) {
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
