package core;

/**
 * Logic based code for the core.Connect4 game, Deals with placing pieces, deciding if a move is legal or not, as well as
 * finding a connect 4 in a horizontal, vertical, or diagonal fashion.
 * @author justin Kolich
 * @version 1.0
 */
public class Connect4 {

    private static char[][] board = new char[6][7];
    private static int cols = 7;
    private static int rows = 6;

    /**
     *
     * @return Number of columns in the board
     */
    public static int getCols(){
        return cols;
    }

    /**
     *
     * @return Number of rows in the board
     */
    public static int getRows(){
        return rows;
    }


    /**
     * Resets the board to all empty spaces
     * This method is for if a play again structure is implemented
     */
    public static void reset(){
        board = new char[6][7];
    }

    public static void setBoard(char[][] c){
        board = c;
    }

    /**
     * Check if there is a connect4. This is done by checking the
     * horizontal, vertical, positive sloped diagonal, and negative sloped diagonal.
     * @param s String identifier of a player "X" or "O"
     * @return True if a connect4 is found, False if not
     */
    public static boolean fourInARow(char s){
        for(int i = rows-1; i >=0; i--){
            for(int j = cols-1; j>=0; j--){
                if(board[i][j]== s){
                    boolean h = (horizontal(i,j,s)>=4 || vertical(i,j,s)>=4 || negDiag(i,j,s)>=4 || posDiag(i,j,s)>=4);
                    if(h){
                        return true;
                    }
                }
            }
        }
        return false;

    }

    /**
     * Places the piece by starting at the bottom of the board. If the bottom of the column is open, then place the
     * piece, otherwise move up one row and try again. If the piece cannot be played then return -1.
     * @param col Column number the piece wants to be placed in
     * @param piece Identifier of the player "X" or "O"
     * @return returns the row if the move can be played, or -1 if the move cannot be made
     */
    public static int place(int col, char piece){
        if(col < 1 || col > cols){
            return -1;
        }
        for(int i = rows-1; i >=0; i--){
            if(board[i][col-1]==0){
                board[i][col-1] = piece;
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks horizontally for a connect 4 by moving up and down the column and counting consecutive pieces of the
     * same type. Stops counting if it goes off the board or if the other piece is encountered.
     * @param r Row number of last played piece
     * @param c Column number of last played piece
     * @param s String identifier of a player "X" or "O"
     * @return True if found, false otherwise
     */
    public static int horizontal(int r, int c, char s){
        int consec = 0;
        char x = board[r][c];
        if(x != 0 && x == s){
            consec++;
        }
        int i = 1;
        while(c+i < getCols()){
            x = board[r][c+i];
            if(x == 0 || x != s){
                i=1;
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        i=1;
        while(c-i >= 0){
            x = board[r][c-i];
            if(x==0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        return consec;
    }

    /**
     *Checks for a core.Connect4 in the vertical direction by going right and left and incrementing the consecutive pieces.
     * Stops counting if it goes off the board or if the other piece is encountered.
     * @param r Row number of last played piece
     * @param c Column number of last played piece
     * @param s String identifier of a player "X" or "O"
     * @return True if found, false otherwise
     */
    public static int vertical(int r, int c, char s){
        int consec = 0;
        char x = board[r][c];
        if(x != 0 && x == s){
            consec++;
        }
        int i = 1;
        while(r+i < getRows()){
            x = board[r+i][c];
            if(x == 0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        i=1;
        while(r-i >= 0){
            x = board[r-i][c];
            if(x==0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        return consec;
    }

    /**
     * Checks for a connect4 by moving up and to the right as well as down and to the left.
     * Stops counting if it goes off the board or if the other piece is encountered.
     * @param r Row number of last played piece
     * @param c Column number of last played piece
     * @param s String identifier of a player "X" or "O"
     * @return True if found, false otherwise
     */
    public static int posDiag(int r, int c, char s){
        int consec = 0;
        char x = board[r][c];
        if(x != 0 && x == s){
            consec++;
        }
        int i = 1;
        while(r+i < getRows() && c-i >= 0){
            x = board[r+i][c-i];
            if(x == 0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        i=1;
        while(r-i >= 0 && c+i < getCols()){
            x = board[r-i][c+i];
            if(x == 0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        return consec;
    }

    /**
     * Checks for a connect4 by moving up and to the left and down and to the right.
     * Stops counting if it goes off the board or if the other piece is encountered.
     * @param r Row number of last played piece
     * @param c Column number of last played piece
     * @param s String identifier of a player "X" or "O"
     * @return True if found, false otherwise
     */
    public static int negDiag(int r, int c, char s){
        int consec = 0;
        char x = board[r][c];
        if(x != 0 && x == s){
            consec++;
        }
        int i = 1;
        while(r+i < getRows() && c+i < getCols()){
            x = board[r+i][c+i];
            if(x == 0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        i=1;
        while(r-i >= 0 && c-i >= 0){
            x = board[r-i][c-i];
            if(x == 0 || x != s){
                break;
            }
            else{
                i++;
                consec+=1;
            }
        }
        return consec;
    }
}
