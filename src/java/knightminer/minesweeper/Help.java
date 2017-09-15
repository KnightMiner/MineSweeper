package knightminer.minesweeper;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Contains methods to load various help panels
 * 
 * @author  KnightMiner
 */
public class Help {
    // prebuilt panels
    private static RulesPanel rulesPanel;
    private static DifficultyPanel difficultyPanel;
    private static MenuPanel menuPanel;

    /**
     * Called by MineSweeper to create all the help panels
     */
    public static void load() {
        // create panels
        rulesPanel = new RulesPanel();
        difficultyPanel = new DifficultyPanel();
        menuPanel = new MenuPanel();
    }

    /**
     * Show the main rules help panel
     * @param parent  Currently open GUI
     */
    public static void showRules(JFrame parent) {
        JOptionPane.showMessageDialog(
                parent,
                rulesPanel,
                "MineSweeper - Rules",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Show the GUI help panel
     * @param parent  Currently open GUI
     */
    public static void difficultyHelp(JFrame parent) {
        JOptionPane.showMessageDialog(
                parent,
                difficultyPanel,
                "MineSweeper - Difficulty",
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Show the GUI help panel
     * @param parent  Currently open GUI
     */
    public static void menuHelp(JFrame parent) {
        JOptionPane.showMessageDialog(
                parent,
                menuPanel,
                "MineSweeper - Menu help",
                JOptionPane.PLAIN_MESSAGE);

    }

    /**
     * Panel describing the game
     */
    private static class RulesPanel extends JPanel {
        public RulesPanel() {
            // 10 panels
            setLayout(new GridLayout(5, 2));

            // empty
            add(new IconDescription(null, new String[] {
                    "Empty spaces can be clicked to reveal mines and numbers. The",
                    "first click of a game is safe, but after that it can be either",
                    "safe or a mine. If the space contains a mine, the game is",
                    "immeditely lost, meanwhile a number will help you determine the",
                    "locations of mines around the space."
            }));
            add(new IconDescription(Piece.N0, new String[] {
                    "If a space is not a mine and has no mines around it, it will",
                    "appear empty but clicked and will automatically click all",
                    "surrounding spaces, essentially acting as a number 0."
            }));

            // mines
            add(new IconDescription(Piece.MINE, new String[] {
                    "The object of the game is to avoid clicking mines, so you will",
                    "not see this icon in an ideal game. Upon losing all spaces not",
                    "marked with flags which contain mines will show this icon"
            }));
            add(new IconDescription(Piece.MINE_RED, new String[] {
                    "The red mine is simply the mine that was clicked, resulting in",
                    "the loss."
            }));
            add(new IconDescription(Piece.MINE_GREEN, new String[] {
                    "Upon clicking the last safe space, the locations of all mines",
                    "that are not marked by a flag are shown in green, denoting they",
                    "were defused.",
                    "This icon is additionally seen when using the cheat button.",
                    "Clicking the cheat button then clicking a space will show it as",
                    "a green mine if it is a mine, or click it if it is safe. Other",
                    "than appearnace, green mines are just like empty spaces, so they",
                    "may still have a flag placed on them or be clicked.",
            }));

            // numbers
            add(new IconDescription(Piece.N5, new String[] {
                    "When a space is safe, clicking it will reveal a number denoting",
                    "the amount of mines surrounding the space. Numbers can be",
                    "adjectent to the mine in any direction, including diagonally.",
                    "If a number has the same number of flags around it as its",
                    "number, it can be clicked to automatically click all the",
                    "surrounding spaces, as they are now known to be safe."
            }));

            // flags
            add(new IconDescription(Piece.FLAG, new String[] {
                    "Flags can be placed by right clicking on a space and are used to",
                    "mark the locations of known mines. If a flag is clicked, nothing",
                    "will happen to prevent accidental losing of the game.",
                    "Right clicking a flag will replace it with a mark, which can be",
                    "right clicked again to remove it."
            }));
            add(new IconDescription(Piece.FLAG_NOT, new String[] {
                    "Upon losing the game, all flags which are not over mines are",
                    "replaced with the broken flag, denoting the error."
            }));

            // marks
            add(new IconDescription(Piece.MARK_RED, new String[] {
                    "Marks are placed by right clicking a flag, or holding shift and",
                    "right clicking and empty space. They are used to mark possible",
                    "locations of mines. Unlike flags, they can still be clicked and",
                    "will result in either a safe space or a bomb",
                    "Right clicking a mark will remove it, and shift-right clicking",
                    "it will change its color, allowing distingusihing different",
                    "possible mines in a more challenging game."
            }));

            // goal
            add(new IconDescription(null, new String[] {
                    "The object of the game is to click all safe spaces without",
                    "clicking any mines. Doing so will reveal all mines."
            }));
        }
    }

    /**
     * Panel about navigating the menus
     */
    private static class DifficultyPanel extends JPanel {
        public DifficultyPanel() {
            // 4 panels
            setLayout(new GridLayout(4, 1));

            // easy
            add(new IconDescription(Piece.FLAG, new String[] {
                    "In an easy game, the board is 9x9 and there are only 10 mines.",
                    "This size is recommended for beginners and can be completed",
                    "quickly as skill progresses."
            }));

            // normal
            add(new IconDescription(Piece.N4, new String[] {
                    "In a normal game, the board is 16x16 and has 40 mines. This size",
                    "is recommended for a player hoping to advance their skills, or",
                    "as a more relaxing game after many trys in expert."
            }));

            // hard
            add(new IconDescription(Piece.MINE, new String[] {
                    "In an expert game, the board is 30x16 and has 99 mines. This size",
                    "is a challenge even for the best of players, and can take up to",
                    "an hour to complete a single game."
            }));

            // custom
            add(new IconDescription(Piece.MARK_BLUE, new String[] {
                    "Custom games allow the player to choose both the size from 5x5",
                    "to 50x50, as well as the mine count ranging from an easy game to",
                    "impossible. Note that larger boards with more mines may take",
                    "longer to render and load the mines"
            }));
        }
    }

    private static class MenuPanel extends JPanel {
        public MenuPanel() {
            add(new IconDescription(null, new String[] {
                    "The menu contains various useful items. Firstly, it contains the",
                    "new game menu, which allows games to be started in three",
                    "different difficulties, along with a customizer",
                    "",
                    "Restart will restart the current game from the beginning,",
                    "removing all revealed numbers and placed flags and marks,",
                    "allowing you to try again after losing. Note that when",
                    "restarting the first click is no longer gaurenteed to be safe.",
                    "",
                    "Save game and Load game allow you to save a game to the disc and",
                    "resume at a later time, or if you feel cheaty you can save",
                    "making a risky click",
                    "",
                    "Mine denotes how many mines are left unmarked. Note that this",
                    "will increment even if there is no mine under the flag, so it",
                    "can become negative if too many flags are placed.",
                    "",
                    "New game will simply start a new game at the current size. This",
                    "button will also change its label to denote winning and losing.",
                    "",
                    "Cheat allows you to safely click any space, as due to the",
                    "randomness of MineSweeper there can often be points in the game",
                    "where the remaining mines cannot be determined between multiple",
                    "points. Upon clicking the button, you can cancel, or click a",
                    "space to reveal it. If safe, it acts like any other click. If a",
                    "mine, it will be revealed in green and the game will not be",
                    "lost. Each game has limited cheats, so use sparingly."
            }));
        }
    }

    /**
     * Helper class to add multiline text with images
     */
    private static class IconDescription extends JLabel {
        public IconDescription(Piece piece, String[] text) {
            // add newlines to the text array
            setText("<html>" + String.join("<br>", text));

            // if a piece is set, use that as the icon
            if(piece != null && piece.getIcon() != null) {
                Image image = piece.getIcon()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(image));
            }
        }
    }
}