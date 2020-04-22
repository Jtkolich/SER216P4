package core;

import core.Connect4;

import java.util.Random;

/**
 * This class contains the logic behind the cpu player for connect4. Currently, the cpu will block the opponent
 * if it finds that they have 3 in a row in either the vertical or horizontal direction, and besides that it
 * will make random moves until the game is over
 * @author justin Kolich
 * @version 1.0
 */
public class Connect4ComputerPlayer {
    private char classifier;
    private int pieces, row=0, col=0;

    /**
     * Constructor that sets the classifier to "O" and the pieces to 21
     */
    public Connect4ComputerPlayer(){
        classifier = 'O';
        pieces = 21;
    }

    /**
     *
     * @return row number that was last played by the computer
     */
    public int getRow(){
        return row;
    }

    /**
     *
     * @return column number that was last played by the computer
     */
    public int getCol(){
        return col;
    }

    /**
     * Decremnts the number of pieces if a move was made
     */
    public void moveMade(){
        pieces--;
    }

    /**
     *
     * @return number of pieces the cpu has
     */
    public int getP(){
        return pieces;
    }

    /**
     *
     * @return classifier of the cpu, aka "O"
     */
    public char getC(){
        return classifier;
    }

    /**
     * Makes a move based on the current state of the board, first sees if it can win, and if it cannot then
     * the computer blocks the player if they have 3 in a row, and otherwise the move made is random
     * @param game String representation of the board
     */
    public void makeMove(char [][] game){
        int win = scanBoard(game, 'O');
        int block = scanBoard(game, 'X');
        if(win == -1 && block != -1){
            win = block;
        }
        int validMove = -1;
        do{
            if(win == -1) {
                Random rand = new Random();
                this.col = rand.nextInt(8);
                validMove = Connect4.place(this.col+1, classifier);
                if (validMove != -1) {
                    this.row = validMove;
                }
            }
            else{
                this.col = win;
                validMove = Connect4.place(this.col+1, classifier);
                if (validMove != -1) {
                    this.row = validMove;
                }
                else{
                    win = -1;
                }
            }
        }while(validMove == -1);
    }

    /**
     * Scans the 7 spots on the board that can be played in, if a 4 in a row will be completed by playing in
     * one of the spots, then that is the value that is returned, otherwise -1 is returned
     * @param board the board that is currently in play
     * @param p letter that is being scanned for, "X" or "O"
     * @return the column number that should be played in
     */
   public int scanBoard(char[][] board, char p){
       int bestMove = -1;
       for(int i = 5; i >=0; i--){
           for(int j = 0; j < 7; j++){
               if((i == 5 || board[i+1][j]!= ' ') && board[i][j] == ' ') {
                   int x = Connect4.horizontal(i, j, p);
                   if (x >= 3) {
                       bestMove = j;
                   }
                   x = Connect4.vertical(i, j, p);
                   if (x >= 3) {
                       bestMove = j;
                   }
                   x = Connect4.posDiag(i, j, p);
                   if (x >= 3) {
                       bestMove = j;
                   }
                   x = Connect4.negDiag(i, j, p);
                   if (x >= 3) {
                       bestMove = j;
                   }
               }
           }
       }
       return bestMove;
   }
}
