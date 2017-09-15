package knightminer.minesweeper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Contains all the visible pieces in the MineSweeper game
 * 
 * @author  KnightMiner
 */
public class MineSweeperBoard implements Serializable {

    /**
     * Random number generator used to create game seeds
     * Minefields use a sepparate object created using a specific seed
     */
    public static final Random RANDOM = new Random();

    // resizable
    private ArrayList<Space> update;

    // board dimensions
    private int width, height;
    private int mineCount;
    private int flagCount;

    // board data
    private Piece[][] pieces;
    private boolean[][] mines;
    private boolean gameOver;
    private boolean victory;

    // determines if the first click happened yet
    private boolean firstClick;

    // determines if we have used our one cheat
    private int cheatsAllowed;
    private int cheats = 0;

    // board seed
    private long seed;

    /**
     * Creates a new minesweaper board with the specified dimensions
     * @param width      Width of the board
     * @param height     Height of the board
     * @param mineCount  Total mines
     * @param cheats     Cheats allowed
     */
    public MineSweeperBoard(int width, int height, int mineCount, int cheats) {
        this.width = width;
        this.height = height;

        this.pieces = new Piece[height][width];
        this.mines = new boolean[height][width];

        // sanity check in case the GUI fails to correct the number
        this.mineCount = Math.min(mineCount, width * height - 9);
        this.cheatsAllowed = this.cheats = cheats;

        update = new ArrayList<Space>();
    }

    /**
     * Creates a new minesweaper board with the specified dimensions
     * @param width      Width of the board
     * @param height     Height of the board
     * @param mineCount  Total mines
     */
    public MineSweeperBoard(int width, int height, int mineCount) {
        this(width, height, mineCount, 1);
    }



    /* Main logic */

    /**
     * Populates the board with mines with a random seed
     * @param space  Space clicked
     */
    public void generateMines(Space space) {
        generateMines(RANDOM.nextLong(), space);
    }

    /**
     * Populates the board with mines
     * @param seed  Seed to generate mines. Two fields with the same size and
     * seed will have the same mines locations
     * @param space space clicked when generating the mines
     */
    public void generateMines(long seed, Space space) {
        // store the current seed for saving games
        this.seed = seed;


        // determine the total safe spaces on the board
        int safeCount = 0;

        // cheat click handling: null spaces
        boolean noSpace = space == null;

        // against the left or right edge gives us just two columns
        int x = 0, y = 0;

        // only adjust if we have a clicked space
        if(!noSpace) {
            x = space.getX();
            if(x == 0 || x == width - 1) {
                safeCount = 2;
            }
            else {
                safeCount = 3;
            }
            // against the top or bottom gives us two rows
            y = space.getY();
            if(y == 0 || y == height - 1) {
                safeCount *= 2;
            }
            else {
                safeCount *= 3;
            }
        }

        // total count
        int size = width * height - safeCount;

        // clear the mines first, as we run this multiple times on first click
        this.mines = new boolean[height][width];

        // new seeded random object, so results can be controlled
        Random random = new Random(seed);

        // first we generate the relative positions of the mines
        int[] minePos = new int[mineCount];
        for(int i = 0; i < mineCount; i++) {
            // each one is one less since we skip the spot of the last added mine
            minePos[i] = random.nextInt(size - i);
        }

        // add the mines to the board
        for(int mine : minePos) {
            int position = 0;

            boolean done = false;
            for(int r = 0; r < mines.length && !done; r++) {
                boolean[] row = mines[r];
                for(int c = 0; c < row.length && !done; c++) {
                    // if on or around the space clicked then skip
                    if(!noSpace) {
                        boolean safe = false;
                        for(int i = -1; i <= 1; i++) {
                            for(int j = -1; j <= 1; j++) {
                                if((r + i == y) && (c + j == x)) {
                                    safe = true;
                                    break;
                                }
                            }
                        }
                        if(safe) {
                            continue;
                        }
                    }

                    // if there is already a bomb there, it is also skipped
                    // not even counted for the position
                    if(row[c]) {
                        continue;
                    }

                    // otherwise, if we have a position place the bomb and break
                    if(position == mine) {
                        row[c] = true;
                        done = true;
                    }
                    else {
                        position++;
                    }
                }
            }
        }
    }

    /**
     * Creates a new game with the same dimensions
     */
    public void newGame() {
        // first, regenerate the mines
        this.mines = new boolean[height][width];

        // next, clear game specific data
        resetData();

        // then mark all spaces for a rendering update
        for(Space space : getAllSpaces()) {
            markUpdate(space);
        }
    }

    /**
     * Restarts the current game
     */
    public void restart() {
        // if we have not clicked yet, do nothing
        // we don't have a seed to restart from
        if(!firstClick) {
            return;
        }

        // reset any relevant data
        resetData();

        // but assume we already clicked so the mines don't change
        firstClick = true;

        // and mark all spaces for an update
        for(Space space : getAllSpaces()) {
            markUpdate(space);
        }
    }

    /**
     * Saves the board to a binary file
     * @param filename  Location of the game
     */
    public void saveGame(String filename) {
        // TODO: better saving logic
        try {
            FileOutputStream file = new FileOutputStream(filename + ".bin");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(this);
            stream.flush();
            stream.close();
        }
        catch (IOException e) {
        }
    }


    /**
     * Load the board from a binary file
     * @param filename  Location of the game
     * @return  The board, or null if invalid
     */
    public static MineSweeperBoard loadGame(String filename) {
        try {
            FileInputStream file = new FileInputStream(filename + ".bin");
            ObjectInputStream stream = new ObjectInputStream(file);
            MineSweeperBoard board = (MineSweeperBoard) stream.readObject();
            stream.close();
            return board;
        }
        catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resets generic data
     */
    private void resetData() {
        this.pieces = new Piece[height][width];
        gameOver = false;
        firstClick = false;
        cheats = cheatsAllowed;
        flagCount = 0;
        victory = false;
    }

    /**
     * Ends the game with a loss
     * @param clicked  Location clicked causing the loss
     * @return  Mine clicked
     */
    public void loseGame(Space clicked) {
        // set the game to over
        gameOver = true;

        showMines(clicked, false);
    }

    /**
     * Checks if the game has been won, and sets the appropiate statuses
     */
    public void checkVictory() {
        // check all spaces
        for(Space space : getAllSpaces()) {

            // only spaces without mines need to be checked, we don't care about
            // flags or marks on mine spaces
            if(!isMine(space)) {
                // if the piece is unclicked or not a number, we failed
                Piece piece = getPiece(space);
                if(piece == null || !piece.isNumber()) {
                    return;
                }
            }
        }

        // made it through that? we won
        victory = true;
        gameOver = true;

        showMines(null, true);
    }

    /**
     * Shows all the mines on the board
     * @param space    Space that was clicked
     * @param victory  Determines if the game was won or lost
     */
    private void showMines(Space clicked, boolean victory) {
        // set the mine color based on victory
        Piece mine = victory ? Piece.MINE_GREEN : Piece.MINE;

        // set the clicked space to red
        if(clicked != null && isMine(clicked)) {
            setPiece(clicked, Piece.MINE_RED);
        }

        // show mines on the board
        for(Space space : getAllSpaces()) {
            if(space.equals(clicked)) {
                continue;
            }
            Piece piece = getPiece(space);
            if(isMine(space)) {
                // but only on empty or marks
                if(piece == null || piece.isMark()) {
                    setPiece(space, mine);
                }
            }
            else {
                // if the space has a flag but no mine, mark it as such
                if(piece == Piece.FLAG) {
                    setPiece(space, Piece.FLAG_NOT);
                }
            }
        }
    }


    /* Clicking */

    /**
     * Handle standard game clicks
     * @param space   Space clicked
     * @param action  Type of click
     */
    public void handleClick(Space space, ClickAction action) {
        // stop playing when the game is over
        if(gameOver) {
            return;
        }
        // simply passes it along to the dedicated function
        switch(action) {
            case DEFAULT:
                handleDefaultClick(space);
                break;
            case FLAG:
                handleFlagClick(space);
                break;
            case MARK:
                handleMarkClick(space);
                break;
            case CHEAT:
                handleCheatClick(space);
                break;
        }
    }

    /**
     * Space clicking core, does the actual clicking action
     * @param space  Space clicked
     */
    private void clickSpace(Space space) {
        // if the space is not replaceable, stop now
        // called here so we don't click flags as they are mines
        if(!isReplaceable(space)) {
            return;
        }

        // if the space is a mine, we lose
        if(isMine(space)) {
            loseGame(space);
        }

        // otherwise do a normal click
        else {
            // sets the piece to the number of surrounding mines
            Piece newPiece = getNumber(space);
            setPiece(space, newPiece);

            // if the piece is 0, update surrounding pieces as none of them are mines
            // this won't eternally recur since this space is no longer clickable
            if(newPiece == Piece.N0) {
                for(Space neighbor : getNeighbors(space)) {
                    clickSpace(neighbor);
                }
            }
        }
    }

    /**
     * Default clicks, clicks a space and displays a number or loses the game
     * @param space  Space clicked
     */
    private void handleDefaultClick(Space space) {
        // first click should generate the minefield
        if(!firstClick) {
            firstClick(space);
        }

        // if the space cannot be replaced then stop here
        // as we run this function on every click
        if(isNumber(space)) {
            // if the number of flags around this space is the same as the number
            Space[] neighbors = getNeighbors(space);

            // count the flags around the piece
            int flags = 0;
            for(Space neighbor : neighbors) {
                if(getPiece(neighbor) == Piece.FLAG) {
                    flags++;
                }
            }

            // same number of flags as the piece number?
            if(flags == getPiece(space).getNumber()) {
                // click all the spaces. This is a risky move if a flag is wrong
                for(Space neighbor : neighbors) {
                    clickSpace(neighbor);
                }
            }
        }
        // otherwise if not a number, just click the space
        else {
            clickSpace(space);
        }

        // check just once at the end of clicking
        checkVictory();
    }

    /**
     * Handle the first click in the game. This click will generate the
     * minefield as it cannot be on a mine
     * @param space  Space clicked
     */
    private void firstClick(Space space) {
        generateMines(space);
        firstClick = true;
    }

    /**
     * Flag clicks, essentially click once to add
     * @param space  Space clicked
     */
    private void handleFlagClick(Space space) {
        // if already a flag, set a mark
        Piece piece = getPiece(space);
        if(piece == Piece.FLAG) {
            setPiece(space, Piece.MARK_RED);
            flagCount--;
        }
        // remove marks
        else if(piece != null && piece.isMark()) {
            setPiece(space, null);
        }
        // otherwise if we can add one
        else if(isReplaceable(space)) {
            setPiece(space, Piece.FLAG);
            flagCount++;
        }
    }

    /**
     * Mark clicks, changes the color of a mark
     * @param space  Space clicked
     */
    private void handleMarkClick(Space space) {
        // tobble the mark
        Piece piece = getPiece(space);
        if(piece == Piece.MARK_RED) {
            setPiece(space, Piece.MARK_GREEN);
        }
        // remove marks
        else if(piece == Piece.MARK_GREEN) {
            setPiece(space, Piece.MARK_BLUE);
        }
        // otherwise if we can add one
        else if(piece == Piece.MARK_BLUE) {
            setPiece(space, Piece.MARK_RED);
        }

        // if its a flag, remove it
        else if(piece == Piece.FLAG) {
            setPiece(space, Piece.MARK_RED);
            flagCount--;
        }

        // essentially all that is left here is null, but still
        else if(isReplaceable(space)) {
            setPiece(space, Piece.MARK_RED);
        }
    }

    /**
     * Cheat clicks, click a space and it will show a "ghost" bomb if one exists
     * @param space  Space clicked
     */
    private void handleCheatClick(Space space) {
        // already ran, don't run again
        if(!canCheat()) {
            return;
        }

        // no mines if we have not clicked
        if(!firstClick) {
            // you cheated for the first click, useless move as now we don't care
            // about click protection
            firstClick(null);
        }

        // only marks and empty spaces can be cheated, to save accidental clicks
        Piece piece = getPiece(space);
        if(piece != null && !piece.isMark()) {
            return;
        }

        // if its a mine, show that
        if(isMine(space)) {
            setPiece(space, Piece.MINE_GREEN);
        }
        // otherwise click the space as we know its safe
        else {
            clickSpace(space);
        }

        // use a cheat
        cheats--;

        // need to check, since it could be the last click
        checkVictory();
    }


    /* Helper functions */

    /**
     * Gets a list of all spaces
     * @return  a list of all spaces
     */
    public Space[] getAllSpaces() {
        Space[] spaces = new Space[width * height];

        // loop through all spaces, adding them to an array
        int i = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                spaces[i] = new Space(x, y);
                i++;
            }
        }

        return spaces;
    }

    /**
     * Helper function to get the coordinates of all 8 neighbors to the space
     * Note that some may be invalid spaces
     * @param space  Space around which to get neighbors
     * @return  an array of spaces of the 8 neighbors, or less on edges
     */
    public Space[] getNeighbors(Space space) {
        // if not valid, return no neighbors
        if(!isValid(space)) {
            return new Space[0];
        }

        // array cannot be bigger than 8
        Space[] neighbors = new Space[8];
        int x = space.getX();
        int y = space.getY();

        // 2D loop from x - 1, y - 1 up to x + 1, y + 1
        int neighborCount = 0;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                // skip offset of 0,0 as that is the input space
                if(i == 0 && j == 0) {
                    continue;
                }

                // make sure the space is valid before returning it
                Space neighbor = new Space(x + i, y + j);
                if(isValid(neighbor)) {
                    neighbors[neighborCount] = neighbor;
                    neighborCount++;
                }
            }
        }

        return neighbors;
    }

    /**
     * Marks a space as needing update by the graphics display
     * @param space  space needing an update
     */
    public void markUpdate(Space space) {
        if(!update.contains(space)) {
            update.add(space);
        }
    }

    /**
     * Returns the number to display on the space after clicking it
     * Does not take bombs on this space into account, check isMine() first
     * @param space  Space to check
     * @return  The number of bombs around this space
     */
    public Piece getNumber(Space space) {
        // no space? no return
        if(!isValid(space)) {
            return null;
        }

        int count = 0;
        // check all the neighbors for a bomb
        for(Space neighbor : getNeighbors(space)) {
            // if they have one, add to the running total
            if(isMine(neighbor)) {
                count++;
            }
        }

        // return the specific piece
        return Piece.fromNumber(count);
    }


    /* Getters, setters, and basic return logic */

    /**
     * Gets the piece on the specified space
     * @param space  Space to check for a piece
     * @return the piece on the specified space
     */
    public Piece getPiece(Space space) {
        // invalid gets null
        if(!isValid(space)) {
            return null;
        }

        // otherwise go with the contained piece
        return pieces[space.getY()][space.getX()];
    }

    /**
     * Placed a piece on a space
     * @param space  Space to place the piece
     * @param piece  Piece to place on the space
     */
    public void setPiece(Space space, Piece piece) {
        // must be valid
        if(isValid(space)) {
            pieces[space.getY()][space.getX()] = piece;
            markUpdate(space);
        }
    }

    /**
     * Determines if a space contains a bomb
     * @param space  Space to check
     * @return  True if the space contains a bomb
     */
    public boolean isMine(Space space) {
        // not a valid space?
        if(!isValid(space)) {
            return false;
        }

        return mines[space.getY()][space.getX()];
    }

    /**
     * Determines if a space appears enabled, or pressed up
     * @param space  Space to check
     * @return  True if the space appears enabled
     */
    public boolean isEnabled(Space space) {
        if(!isValid(space)) {
            return false;
        }

        Piece piece = getPiece(space);

        // if the space is empty, it pops up
        if(piece == null) {
            return true;
        }

        // otherwise go based on the contained value
        return piece.isEnabled();
    }

    /**
     * Determines if the contents of a space can be replaced
     * @param space  Space clicked
     */
    private boolean isReplaceable(Space space) {
        // invalid spaces are always false
        if(!isValid(space)) {
            return false;
        }

        Piece piece = getPiece(space);

        // if the space is empty, it can be clicked
        if(piece == null) {
            return true;
        }

        // otherwise go based on the contained value
        return piece.isReplaceable();
    }

    /**
     * Determines if the space is a number
     * @param space  Space clicked
     */
    private boolean isNumber(Space space) {
        // invalid spaces are always false
        if(!isValid(space)) {
            return false;
        }

        Piece piece = getPiece(space);

        // if the space is empty, it is not a number
        if(piece == null) {
            return false;
        }

        // otherwise go based on the contained value
        return piece.isNumber();
    }


    /* Data */

    /**
     * Gets the width of the board
     * @return  The width of the board
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the board
     * @return  The height of the board
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the number of mines left on the board
     * @return  number of mines remaining
     */
    public int getRemainingMines() {
        return mineCount - flagCount;
    }

    /**
     * Checks if the player has used their cheat
     * @return  if the player has used their cheat
     */
    public boolean canCheat() {
        return cheats > 0;
    }

    /**
     * Checks how many cheats the player has left
     * @return  remaining cheats
     */
    public int getCheats() {
        return cheats;
    }

    /**
     * Checks if the game ended
     * @return  true if the game ended
     */
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * Checks if the player won the game
     * @return  true if the player won the game
     */
    public boolean hasWon() {
        return victory;
    }

    /**
     * Checks if a space is within the bounds of this board
     * @param space  Space to check
     * @return  true if the space is within this board
     */
    public boolean isValid(Space space) {
        // no space?
        if(space == null) {
            return false;
        }

        // first, check if the space is entirely invalid
        if(space.getX() < 0 || space.getY() < 0) {
            return false;
        }

        // next, check if this is outside of this board
        if(space.getY() >= height || space.getX() >= width) {
            return false;
        }

        // fits in both? return true then
        return true;
    }


    /**
     * Gets a list of spaces pending updates to update the button displays
     * @return a list of spaces needing an update
     */
    public ArrayList<Space> getUpdates() {
        return update;
    }

    /**
     * Actions that can be performed on click.
     * Used by buttons to pass along a constant rather than a list of modifiers.
     */
    public enum ClickAction {
        /** Normal click */
        DEFAULT,
        /** Placing a flag or mark */
        FLAG,
        /** Placing a mark of different colors */
        MARK,
        /** Safely clicking a mine */
        CHEAT;
    }
}