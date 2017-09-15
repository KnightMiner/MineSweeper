package knightminer.minesweeper;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Main launcher class for MineSweeper, containing all the menus and general
 * prompts
 * 
 * @author  KnightMiner
 */
public class MineSweeper {
    // currently running instance of the game
    private static MineSweeperGui gui;

    /**
     * Primary way to boot the game
     * @param args  Default args (unused)
     */
    public static void main(String[] args) {
        // High DPI stuff
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // load the help panels
        // this has a side effect of loading the icons for the pieces
        Help.load();

        // start the game on easy
        newGame(Difficulty.EASY);
    }

    /**
     * Creates a new MineSweeper game with one of three default sizes or custom
     * @param difficulty Game difficulty
     */
    public static void newGame(Difficulty difficulty) {
        // difficulty switch
        MineSweeperBoard board = null;
        switch(difficulty) {
            case EASY:
                board = new MineSweeperBoard(9, 9, 10);
                break;
            case NORMAL:
                board = new MineSweeperBoard(16, 16, 40);
                break;
            case HARD:
                board = new MineSweeperBoard(30, 16, 99);
                break;
            case CUSTOM:
                board = customGame();
                break;
        }

        startGame(board);
    }

    /**
     * Creates a new game with the custom size
     * @return  A board of the custom size
     */
    public static MineSweeperBoard customGame() {
        CustomMenu menu = new CustomMenu();

        int result = JOptionPane.showConfirmDialog(null, menu,
                "Custom", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // if they pressed OK
        if(result == JOptionPane.OK_OPTION) {
            return menu.getBoard();
        }
        else {
            return null;
        }
    }

    /**
     * Starts a new game with the specified board
     * @param board  Game board to start
     */
    public static void startGame(MineSweeperBoard board) {
        // must be a valid board
        if(board != null) {
            // remove the current gui and define the new one
            if(gui != null) {
                gui.dispose();
            }

            // create a new instance
            gui = new MineSweeperGui(board);
        }
    }

    /**
     * List of buttons for the GUI
     */
    public enum Difficulty {
        /** Easy game */
        EASY("Easy"),
        /** Normal game */
        NORMAL("Normal"),
        /** Hard game */
        HARD("Expert"),
        /** Custom size and mines from the GUI */
        CUSTOM("Custom");

        // data
        private String label;

        /**
         * Default constructor
         * @param  Button label
         */
        Difficulty(String label) {
            this.label = label;
        }

        /**
         * Gets the label for the difficulty
         * @return  Difficulty name
         */
        public String getLabel() {
            return label;
        }
    }
}