package ui;

/**This class is meant to be the user interface for the Connect 4 gameplay. What is done here is that the game
 * starts and a board is initialized. Then players take turns making moves until the game is over.
 * @author Justin Kolich
 * @version 1.0
 */

import java.util.Scanner;
import core.Connect4;
import core.Connect4ComputerPlayer;

public class Connect4TextConsole {

    private char[][] game = new char[6][7];


    /**
     *This class encapsulates a player, and stores the symbol of the player, and their remaining pieces
     */
    private class Player{
        private final char classifier;
        private int pieces;

        /**
         *
         * @param classifier is the symbol each player has
         */
        Player(char classifier){
            this.classifier = classifier;
            pieces = 21;
        }

        /**
         * Decrements the number of pieces by 1 when a move is made
         */
        void moveMade(){
            pieces--;
        }

        /**
         *
         * @return Symbol that the character has "X" or "O"
         */
        char getC(){
            return classifier;
        }

        /**
         *
         * @return Number of pieces the player has remaining
         */
        int getP(){
            return pieces;
        }
    }

    /**
     *Main function that runs the program
     * @param args
     */
    public static void main(String[] args){
        Scanner gType = new Scanner(System.in);
        int valid = -1;
        do {
            System.out.println("Please enter 'g' to play the game as a GUI, or 'c' for console based game play");
            String inp = gType.nextLine();
            if (inp.compareToIgnoreCase("g") == 0) {
                valid =1;
                Connect4GUI.run(args);
            } else if (inp.compareToIgnoreCase("c") == 0) {
                valid = 1;
                Connect4TextConsole main = new Connect4TextConsole();
                main.run();
            } else {
                System.out.println("Invalid input, Please try again");
            }
        }while(valid ==-1);

    }

    /**
     * Running of the program. Creates two players, and accepts input on where on the board a player will play.
     * Incorrect inputs will make the user try again, and a draw is possible.
     */
    public void run(){
        initializeGame();
        printGame();
        System.out.println("Begin Game. Enter ‘P’ if you want to play against another player; \n" +
                "enter ‘C’ to play against computer.");
        String s;
        Scanner porc = new Scanner(System.in);
        do {
            s = porc.nextLine();
            if(s.compareToIgnoreCase("c")!=0 && s.compareToIgnoreCase("p")!=0){
                System.out.println("Invalid input. Enter ‘P’ if you want to play against another player; \n" +
                        "enter ‘C’ to play against computer.");
            }
        }while(s.compareToIgnoreCase("c")!=0 && s.compareToIgnoreCase("p")!=0);
        if(s.compareToIgnoreCase("p")==0){
            playVplayer();
        }
        else if(s.compareToIgnoreCase("c")==0){
            playVcomputer();
        }
    }

    /**
     * Method that runs if the user indicates that there will be two players playing
     */
    public void playVplayer(){
        Player playerX = new Player('X');
        Player playerO = new Player('O');
        Scanner move = new Scanner(System.in);
        Connect4.reset();
        Player currPlayer = playerX;
        boolean gameOver;
        do{
            int column = 0;
            int row;
            int validInput = -1;
            do{
                System.out.println("Player"+currPlayer.getC()+" – your turn. Choose a column number from 1-7.");
                do{
                    try {
                        column = Integer.parseInt(move.nextLine());
                        validInput = 1;
                    }
                    catch (NumberFormatException e){
                        System.out.println("Input must be a number, please try again.");
                    }

                }while(validInput ==-1);
                row = Connect4.place(column,currPlayer.getC());
                if(row == -1){
                    System.out.println("Not a valid move. Please try again");
                }
            }while(row == -1);
            currPlayer.moveMade();
            gameOver = Connect4.fourInARow(currPlayer.getC());
            game[row][column-1] = currPlayer.getC();
            printGame();
            if(gameOver){
                System.out.println("Player"+currPlayer.getC()+" Has won!");
            }
            if(playerO.getP() == 0 && playerX.getP() == 0){
                System.out.println("The game has ended in a draw.");
                gameOver = true;
            }
            if(currPlayer.getC() == 'X'){
                currPlayer = playerO;
            }
            else{
                currPlayer = playerX;
            }
        }while(!gameOver);
    }

    /**
     * method that runs if the user indicates that they want to play against a computer
     */
    public void playVcomputer(){
        Player playerX = new Player('X');
        Connect4ComputerPlayer comp = new Connect4ComputerPlayer();
        Scanner move = new Scanner(System.in);
        boolean gameOver;
        int validInput=-1;
        System.out.println("Start game against computer.");
        do{
            int col=0, row;
            do {
                do {
                    System.out.println("It's your turn. Choose a column number from 1-7.");
                    try {
                        col = Integer.parseInt(move.nextLine());
                        validInput = 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Input must be a number, please try again.");
                    }
                }while(validInput ==-1);
                row = Connect4.place(col, playerX.getC());
                if(row ==-1){
                    System.out.println("Not a valid move. Please try again");
                }
            }while(row == -1);
            playerX.moveMade();
            game[row][col-1] = playerX.getC();
            gameOver = Connect4.fourInARow(playerX.getC());
            printGame();
            if(gameOver){
                System.out.println("Player"+playerX.getC()+" Has won!");
                break;
            }
            //playerX is done with their turn, time for computer to make a move
            System.out.println("The computer is playing...");
            comp.makeMove(game);
            comp.moveMade();
            game[comp.getRow()][comp.getCol()] = comp.getC();
            printGame();
            gameOver = Connect4.fourInARow(comp.getC());
            if(gameOver){
                System.out.println("The computer Has won!");
            }
            if(comp.getP() == 0 && playerX.getP() == 0 && !gameOver){
                System.out.println("The game has ended in a draw.");
                gameOver = true;
            }

        }while(!gameOver);
    }

    /**
     * Fills the board with spaces automatically for printing purposes
     */
    public void initializeGame(){
        for(int i=0; i<Connect4.getRows(); i++){
            for(int j = 0; j<Connect4.getCols();j++){
                game[i][j] = ' ';
            }
        }
    }

    /**
     * Prints the board with "|" symbol in between pieces
     */
    public void printGame(){
        for(int i=0; i<Connect4.getRows(); i++){
            for(int j = 0; j<Connect4.getCols();j++){
                System.out.print("|" + game[i][j]);
            }
            System.out.println("|");
        }
    }
}
