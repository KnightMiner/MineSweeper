package knightminer.minesweeper;
import java.awt.Image;
import java.util.Locale;
import java.io.Serializable;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Enum of all piece types
 * 
 * @author  KnightMiner
 */
public enum Piece implements Serializable {
    // basic numbers
    /** No mines around */
    N0,
    /** 1 mines around */
    N1,
    /** 2 mines around */
    N2,
    /** 3 mines around */
    N3,
    /** 4 mines around */
    N4,
    /** 5 mines around */
    N5,
    /** 6 mines around */
    N6,
    /** 7 mines around */
    N7,
    /** 8 mines around */
    N8,

    // flags
    /** Space contains a flag */
    FLAG(true, false, Type.FLAG),
    /** Space contains a flag, but the game ended and there was no mine */
    FLAG_NOT(true, false, Type.FLAG),

    // mines
    /** Space contains a mine, used at the end of the game */
    MINE(true, true, Type.MINE),
    /** Mine that was clicked when losing the game */
    MINE_RED(true, true, Type.MINE),
    /** Mines remaining after winning or revealed by a cheat */
    MINE_GREEN(true, true, Type.MINE),

    // marks
    /** Generic mark */
    MARK_RED(true, true, Type.MARK),
    /** Alternate mark 1 */
    MARK_GREEN(true, true, Type.MARK),
    /** Alternate mark 2 */
    MARK_BLUE(true, true, Type.MARK);

    // storage
    private int number;
    private boolean enabled;
    private boolean replaceable;
    private Image icon;
    private Type type;

    /**
     * Standard constructor for non-numbers
     * @param enabled      Determines if the space appears "clicked"
     * @param replaceable  Determines if the piece can be replaced by clicking it
     */
    Piece(boolean enabled, boolean replaceable, Type type) {
        this.number = -1;
        this.enabled = enabled;
        this.replaceable = replaceable;
        this.type = type;

        setIcon();
    }

    /**
     * Constructor for number values, automatically sets clickable to false
     * and the character to the diget
     */
    Piece() {
        // character is just the oridinal as a character
        this.number = this.ordinal();
        this.enabled = false;
        this.replaceable = false;
        this.type = Type.NUMBER;

        setIcon();
    }

    /**
     * Gets the icon for the button
     * @return the icon
     */
    public Image getIcon() {
        return icon;
    }

    /**
     * Determines if a space appeared "enabled", or pressed up
     * @return true if a space appeared "enabled", or pressed up
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Determines if a space can be clicked despite containing something
     * @return true if a space can be clicked despite containing something
     */
    public boolean isReplaceable() {
        return replaceable;
    }

    /**
     * Determines the space's number
     * @return space's number
     */
    public boolean isNumber() {
        return type == Type.NUMBER;
    }

    /**
     * Determines if the space is a mark
     * @return space's number
     */
    public boolean isMark() {
        return type == Type.MARK;
    }

    /**
     * Determines if a space is a number
     * @return true if a space is a number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets a constant from the specified integer
     * Only goes up to 8 since EMPTY and FLAG shouldn't care about number
     * @param num  Number cooresponding to the piece
     * @return  Piece coorseponding to the number
     */
    public static Piece fromNumber(int num) {
        // if the number is too big or small, return null
        if(num < 0 || num > 8) {
            return null;
        }

        // loop through until we find the equal value
        return values()[num];
    }


    /* Internal use */

    /**
     * Gets the name for a piece
     * @return  The pieces name
     */
    private String getName() {
        if(number > -1) {
            return number + "";
        }

        // US lowercasing just in case
        return this.toString().toLowerCase(Locale.US);
    }

    /**
     * Sets the pieces icon
     */
    private void setIcon() {
        // get the icon for this button
        String pathString = "assets/" + getName() + ".png";
        URL path = getClass().getClassLoader()
                .getResource(pathString);

        // should exist
        if(path != null) {
            // images are so annoying to pull from a path, so imageicon
            icon = new ImageIcon(path).getImage();
        }
        else {
            System.err.println("Error: cannot find icon at " + pathString);
            icon = null;
        }
    }

    private enum Type {
        NUMBER,
        MINE,
        FLAG,
        MARK;
    }
}