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
        JOptionPane.showMessageDialog(parent, rulesPanel, "MineSweeper - Rules", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Show the GUI help panel
     * @param parent  Currently open GUI
     */
    public static void difficultyHelp(JFrame parent) {
        JOptionPane.showMessageDialog(parent, difficultyPanel, "MineSweeper - Difficulty", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Show the GUI help panel
     * @param parent  Currently open GUI
     */
    public static void menuHelp(JFrame parent) {
        JOptionPane.showMessageDialog(parent, menuPanel, "MineSweeper - Menu help", JOptionPane.PLAIN_MESSAGE);

    }

    /**
     * Panel describing the game
     */
    private static class RulesPanel extends JPanel {
        public RulesPanel() {

            setLayout(new GridLayout(3, 2));

            // empty
            add(new IconDescription(Piece.N0, "Empty spaces can be clicked to reveal mines and numbers. "
                    + "The first click of a game is safe, but after that it may be a mine."));

            // goal
            add(new IconDescription(Piece.MARK_GREEN,
                    "The goal of the game is to click all safe spaces without clicking any mines."));

            // mines
            add(new IconDescription(Piece.MINE,
                    "If you click a space with a mine, you lose and all other mines are revealed."));

            // numbers
            add(new IconDescription(Piece.N6,
                    "When a space is safe, clicking it will reveal a number which tells tells how many mines are adjectent to the number. "
                            + "They could be horizontally, vertically, or diagonally adjectent.",
                    "If a number has the same number of flags around it, it can be clicked to click all open spaces around it."));

            // flags
            add(new IconDescription(Piece.FLAG,
                    "Right clicking a space will place a flag, to mark the location of a mine. "
                            + "Flags prevents you from accidently clicking a mine, and decreases the remaining mine count.",
                    "Right clicking a flag will remove it, replacing it with a mark."));

            // marks
            add(new IconDescription(Piece.MARK_RED,
                    "Marks are placed by right clicking a flag, or holding shift and right clicking and empty space. "
                            + "They are used just to mark possible mines. Unlike flags, they can still be clicked.",
                    "Right clicking a mark will remove it."));
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
            add(new IconDescription(Piece.FLAG, "In an easy game, the board is 9x9 and there are only 10 mines.",
                    "This size is recommended for beginners and can be completed quickly as skill progresses."));

            // normal
            add(new IconDescription(Piece.N4, "In a normal game, the board is 16x16 and has 40 mines.",
                    "This size is recommended for a player hoping to advance their skills, "
                            + "or as a more relaxing game after many trys in expert."));

            // hard
            add(new IconDescription(Piece.MINE, "In an expert game, the board is 30x16 and has 99 mines. "
                    + "This size is a challenge even for the best of players, games can easily take 30 minutes."));

            // custom
            add(new IconDescription(Piece.MARK_BLUE,
                    "Custom games allow the player to choose the size from 5x5 to 50x50, "
                            + "as well as the mine count. This allows making a vareity of difficult from easy to impossible."));
        }
    }

    private static class MenuPanel extends JPanel {
        public MenuPanel() {
            add(new IconDescription(null, "The big new game ubtton will simply start a new game at the current size.",
                    "This button will also change its label to denote winning and losing.", "",
                    "The new game menu allows starting the game in three different difficulties, along with a customizer.",
                    "",
                    "Restart will restart the current game from the beginning, removing all revealed numbers and "
                            + "placed flags and marks without changing the minefield.",
                    "Note that when restarting the first click is no longer gaurenteed to be safe.", "",
                    "Save game and load game allow you to save a game to the disc and resume at a later time.", "",
                    "The menu bar shows the number of mines left unmarked, which decreases whenever you place a flag.",
                    "Note that this will increment even if there is no mine under the flag, too many flags will make it negative.",
                    "",
                    "Clicking the cheat button will allow you to safely click a space. "
                            + "If the space is a mine, it will mark it with a green mine, but you won't lose. "
                            + "If it is safe, it will act like clicking a normal space.",
                    "Each game has limited cheats, so use sparingly."));
        }
    }

    /**
     * Helper class to add multiline text with images
     */
    private static class IconDescription extends JLabel {
        public IconDescription(Piece piece, String... text) {
            // add newlines to the text array
            setText("<html><body style='width:400px;'>" + String.join("<br>", text) + "</body></html>");

            // if a piece is set, use that as the icon
            if (piece != null && piece.getIcon() != null) {
                Image image = piece.getIcon().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(image));
            }
        }
    }
}