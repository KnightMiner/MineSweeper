package knightminer.minesweeper;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * Core panel for the custom game menu
 * <br>
 * Generally only one instance exists
 * 
 * @author  KnightMiner
 */
public class CustomMenu extends JPanel implements FocusListener {
    // data
    private JSlider width, height;
    private JTextField fieldMines;

    /**
     * Default constructor
     */
    public CustomMenu() {
        // start with an empty spot
        setLayout(new GridLayout(5, 1));

        // next, create the sliders
        width = new Slider();
        height = new Slider();

        // then add them with the labels
        add(new JLabel("Width", JLabel.CENTER));
        add(width);
        add(new JLabel("Height", JLabel.CENTER));
        add(height);

        Container c = new Container();
        c.setLayout(new GridLayout(1, 2));
        c.add(new JLabel("Mines:"));

        fieldMines = new JTextField("10");
        fieldMines.setHorizontalAlignment(JTextField.RIGHT);
        fieldMines.addFocusListener(this);
        c.add(fieldMines);
        add(c);
    }

    /**
     * Gets the color result
     * @return  the color result
     */
    public MineSweeperBoard getBoard() {
        int mines = Integer.parseInt(fieldMines.getText());
        return new MineSweeperBoard(width.getValue(), height.getValue(), mines);
    }

    // unused
    @Override
    public void focusGained(FocusEvent e) {}

    /**
     * Called when focus on the text field is lost
     */
    @Override
    public void focusLost(FocusEvent e) {
        // make sure the mine count is valid, too large and we cannot generate
        try {
            int mines = Integer.parseInt(fieldMines.getText());
            int max = width.getValue() * height.getValue() - 9;
            if(mines > max) {
                fieldMines.setText(max + "");
            }
        }
        catch(NumberFormatException ex) {
            fieldMines.setText("10");
        }
    }

    /**
     * Sets data used by both sliders
     */
    private class Slider extends JSlider {
        /**
         * General constructor
         */
        public Slider() {
            super(JSlider.HORIZONTAL, 5, 50, 10);

            this.setMajorTickSpacing(5);
            this.setMinorTickSpacing(1);
            this.setPaintTicks(true);
            this.setPaintLabels(true);
        }
    }
}