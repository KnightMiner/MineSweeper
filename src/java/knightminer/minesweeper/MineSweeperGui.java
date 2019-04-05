package knightminer.minesweeper;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 * Main GUI object for Minesweeper games. Called when gameplay is ready
 * 
 * @author  KnightMiner
 */
public class MineSweeperGui extends JFrame implements ActionListener {
    // data
    private MineSweeperBoard board;

    // states
    private boolean isCheating;

    // buttons and labels
    private JLabel mines;
    private MineButton[][] buttons;

    private JButton buttonNewGame;
    private JButton buttonCheat;

    // menus
    private JMenuBar bar;
    private JMenu menuMineSweeper;

    private JMenu menuNewGame;
    private JMenuItem buttonRestart;
    private JMenuItem buttonSave;
    private JMenuItem buttonLoad;
    private JMenuItem buttonExit;

    private JMenu menuHelp;
    private JMenuItem buttonRules;
    private JMenuItem buttonMenu;
    private JMenuItem buttonDifficulty;

    /**
     * Makes a new minesweaper window
     * @param board  Input board, the display will take the size and pieces on the board
     */
    public MineSweeperGui(MineSweeperBoard board) {
        this.setTitle("MineSweeper");
        this.board = board;

        // construct the top menu
        bar = new JMenuBar();
        setJMenuBar(bar);

        // miscelanous actions
        menuMineSweeper = new JMenu("MineSweeper");
        bar.add(menuMineSweeper);

        // new game menu
        menuNewGame = new JMenu("New Game");
        menuMineSweeper.add(menuNewGame);

        // add all difficulties
        for(MineSweeper.Difficulty difficulty : MineSweeper.Difficulty.values()) {
            DifficultyButton button = new DifficultyButton(difficulty);
            menuNewGame.add(button);
        }


        // restarts the current game
        buttonRestart = new JMenuItem("Restart");
        buttonRestart.addActionListener(this);
        menuMineSweeper.add(buttonRestart);

        // saves the current game
        buttonSave = new JMenuItem("Save game");
        buttonSave.addActionListener(this);
        menuMineSweeper.add(buttonSave);

        // loads a new game from a file
        buttonLoad = new JMenuItem("Load game");
        buttonLoad.addActionListener(this);
        menuMineSweeper.add(buttonLoad);

        // exits the game
        menuMineSweeper.addSeparator();
        buttonExit = new JMenuItem("Exit");
        buttonExit.addActionListener(this);
        menuMineSweeper.add(buttonExit);

        // help menu
        menuHelp = new JMenu("Help");
        bar.add(menuHelp);

        // shows the rules
        buttonRules = new JMenuItem("Rules");
        buttonRules.addActionListener(this);
        menuHelp.add(buttonRules);

        // shows help about the menu
        buttonMenu = new JMenuItem("Menus");
        buttonMenu.addActionListener(this);
        menuHelp.add(buttonMenu);

        // shows available difficulties
        buttonDifficulty = new JMenuItem("Difficulties");
        buttonDifficulty.addActionListener(this);
        menuHelp.add(buttonDifficulty);


        // GUI Layout

        // gives use four regions and a center
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());


        // new game with same dimensions
        buttonNewGame = new JButton("New game");
        buttonNewGame.addActionListener(this);

        // cheating
        buttonCheat = new JButton();
        updateCheat();
        buttonCheat.addActionListener(this);

        // remaining mines display
        mines = new JLabel("Mines", SwingConstants.CENTER);
        updateMines();

        // top row of the board
        Container top = new Container();
        top.setLayout(new GridLayout(1, 3));
        top.add(mines);
        top.add(buttonNewGame);
        top.add(buttonCheat);
        top.setPreferredSize(new Dimension(20, top.getFontMetrics(buttonCheat.getFont()).getHeight() * 2));

        pane.add(top, BorderLayout.NORTH);


        // main button grid
        Container minesContainer = new Container();
        int width2 = board.getWidth();
        int height2 = board.getHeight();
        minesContainer.setLayout(new GridLayout(height2, width2));
        buttons = new MineButton[height2][width2];

        // and add them all
        for(int y = 0; y < height2; y++) {
            for(int x = 0; x < width2; x++) {
                // add buttons, and store them
                buttons[y][x] = new MineButton(this, board, new Space(x, y));
                minesContainer.add(buttons[y][x]);
            }
        }

        pane.add(minesContainer, BorderLayout.CENTER);


        // sizes, we need them for the top bar
        int width = board.getWidth() * 40;
        int height = board.getHeight() * 40 + 100;

        setSize(width, height);
        setVisible(true);

        // eventually I will replace this with an event that fires a prompt
        // basically, allow the user to save their game before quitting
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Gets a MineButton at the specified location
     * @param space  Location of the button
     * @return  the button at the location
     */
    public MineButton getButton(Space space) {
        if(!board.isValid(space)) {
            return null;
        }
        return buttons[space.getY()][space.getX()];
    }

    /**
     * Updates all spaces pending update to display their current value
     */
    public void updateSpaces() {
        Queue<Space> queue = board.getUpdates();
        while(!queue.isEmpty()) {
            Space space = queue.poll();
            MineButton update = getButton(space);
            if(update != null) {
                update.updatePiece();
            }
        }

        updateMines();
    }

    /**
     * Updates the mines display
     */
    public void updateMines() {
        mines.setText(String.format("Mines: %d", board.getRemainingMines()));
    }

    /**
     * Sets the cheat status
     * @param cheat  Whether the user is in cheat mode
     */
    private void setCheating(boolean cheat) {
        // and change the button text
        if(cheat) {
            buttonCheat.setText("Cancel cheat");
            isCheating = true;
        }
        // otherwise, run general cheating code
        else {
            updateCheat();
        }
    }

    /**
     * Sets the cheat button to used
     */
    public void updateCheat() {
        // if we can cheat, set the label
        if(board.canCheat()) {
            buttonCheat.setText("Cheats: " + board.getCheats());
            buttonCheat.setEnabled(true);
        }
        // otherwise, set the out of cheats label
        else {
            buttonCheat.setText("Out of cheats");
            buttonCheat.setEnabled(false);
        }
        isCheating = false;
    }

    /**
     * Sets the cheat button to used
     */
    public void resetButtons() {
        buttonNewGame.setText("New game");

        updateCheat();
        updateSpaces();
    }

    /**
     * Sets the winning text
     * @param victory  Whether the player won
     */
    public void gameOver(boolean victory) {
        if(victory) {
            buttonNewGame.setText("You Win!");
        }
        else {
            buttonNewGame.setText("You Lose");
        }
    }

    /**
     * Checks if the cheat button is active
     * @return  true if the cheat button is active
     */
    public boolean isCheating() {
        return isCheating;
    }

    /**
     * Called when the 'X' button is pressed on the window
     * @param e  Event the button is called with
     */
    public void windowClosing(WindowEvent e) {
        // simply run the menu
        //this.menu();
    }

    /**
     * Handles all single instance buttons
     * @param e  Calling event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();

        // restart the current game
        if(button == buttonNewGame) {
            // make the board new
            board.newGame();

            // and clear game data
            resetButtons();
            return;
        }

        // restart the current game
        if(button == buttonRestart) {
            // make the board new
            board.restart();

            // and clear game data
            resetButtons();
            return;
        }

        // saves the game
        if(button == buttonSave) {
            String filename = JOptionPane.showInputDialog(this,
                    "Enter filename to save game",
                    "MineSweeper - Save game",
                    JOptionPane.QUESTION_MESSAGE
                    );

            // user canceled
            if(filename == null || filename.equals("")) {
                return;
            }

            // if it exists, prompt to continue
            if(new File(filename + ".bin").exists()) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Save game " + filename + " already exists, overwrite?",
                        "MineSweeper - Save game",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                        );

                if(result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            board.saveGame(filename);
        }

        // loads the game
        if(button == buttonLoad) {
            String filename = JOptionPane.showInputDialog(this,
                    "Enter filename to load game",
                    "MineSweeper - Load game",
                    JOptionPane.QUESTION_MESSAGE
                    );

            // user canceled
            if(filename == null || filename.equals("")) {
                return;
            }

            // if it exists, prompt to continue
            if(new File(filename + ".bin").exists()) {
                MineSweeperBoard board = MineSweeperBoard.loadGame(filename);
                if(board != null) {
                    MineSweeper.startGame(board);
                }
                // did not load right
                else {
                    JOptionPane.showMessageDialog(this,
                            "Error reading save game " + filename,
                            "MineSweeper - Load game",
                            JOptionPane.ERROR_MESSAGE
                            );
                }
            }
            else {
                // file does not exist
                JOptionPane.showMessageDialog(this,
                        "Saved game " + filename + " does not exist",
                        "MineSweeper - Load game",
                        JOptionPane.ERROR_MESSAGE
                        );
            }
        }

        // cheat button
        if(button == buttonCheat) {
            // no cheating too many times
            if(!board.canCheat()) {
                return;
            }

            // toggle cheating
            setCheating(!isCheating);
            return;
        }

        // cheat button
        if(button == buttonExit) {
            this.dispose();
            return;
        }


        /* Help */

        // rules
        if(button == buttonRules) {
            Help.showRules(this);
        }

        // menus
        if(button == buttonMenu) {
            Help.menuHelp(this);
        }

        // rules
        if(button == buttonDifficulty) {
            Help.difficultyHelp(this);
        }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @author  Larry Vail
     * @param  path  Icon path
     * @param  description  Basic description of the icon
     * @return  Returns the icon at the path, or null if the path is invalid
     */
    protected ImageIcon createImageIcon(String path, String description) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Common code for difficulty buttons
     */
    private class DifficultyButton extends JMenuItem implements ActionListener {
        // data
        private MineSweeper.Difficulty difficulty;

        public DifficultyButton(MineSweeper.Difficulty difficulty) {
            super(difficulty.getLabel());

            this.difficulty = difficulty;
            this.addActionListener(this);
        }

        /**
         * Called when the button is clicked
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            MineSweeper.newGame(difficulty);
        }
    }
}