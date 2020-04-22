package core;

import java.io.*;
import java.net.*;
import java.util.Date;

import core.Connect4;
import core.Connect4Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 *Server for connect 4
 *
 * @author Justin Kolich
 * @version 1.0
 */
public class Connect4Server extends Application implements Connect4Constants {
    // Session number of the first game
    private int sessionNo = 1;

    /**
     * Start method for the Server UI, displays important messgae as well as info about who is joining
     * @param primaryStage the main stage that is
     */
    @Override
    public void start(Stage primaryStage){
        TextArea taLog = new TextArea();

        Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
        primaryStage.setTitle("Connect4 Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e->Platform.exit());

        new Thread(()->{
            try{
                // new serverSocket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(()-> taLog.appendText(new Date() + ": Server started at socket 8000\n"));

                //create a session for every 2 players
                while(true){
                    Platform.runLater(()-> taLog.appendText(new Date() +
                            ": Waiting for players to join session " + sessionNo +"\n")
                    );

                    // Connect to player 1
                    Socket player1 = serverSocket.accept();

                    Platform.runLater(()-> {taLog.appendText(new Date() +
                            ": Player 1 has joined session  " + sessionNo +"\n");
                            taLog.appendText("Player 1's IP address: " + player1.getInetAddress().getHostAddress() +"\n");
                    });

                    new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                    // Connect to player 2
                    Socket player2 = serverSocket.accept();

                    Platform.runLater(()-> {taLog.appendText(new Date() +
                            ": Player 2 has joined session  " + sessionNo +"\n");
                        taLog.appendText("Player 2's IP address: " + player2.getInetAddress().getHostAddress() +"\n");
                    });

                    new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

                    Platform.runLater(()-> taLog.appendText(new Date() +
                            ": Start a thread for session " + sessionNo++ + "\n")
                    );

                    new Thread(new HandleASession(player1, player2)).start();

                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handles each different session for a game
     */
    class HandleASession implements Runnable, Connect4Constants{
        // Create variables for p1 and p2
        private Socket player1;
        private Socket player2;

        private char[][] board = new char[ROWS][COLS];

        //create streams to and from both players
        private DataOutputStream toPlayer1;
        private DataOutputStream toPlayer2;
        private DataInputStream fromPlayer1;
        private DataInputStream fromPlayer2;

        //
        private boolean gameOver = false;

        /**
         * HAndles each session that is played by establishing a connection to 2 players
         * @param player1 player 1 Socket for the first player
         * @param player2 player 2 Socket for the second player
         */
        HandleASession(Socket player1, Socket player2){
            this.player1 = player1;
            this.player2 = player2;

            for(int i = 0; i < ROWS; i++){
                for(int j = 0; j < COLS; j++){
                    board[i][j] = ' ';
                }
            }
        }

        /**
         * Runs the session
         */
        public void run(){
            try{
                fromPlayer1 = new DataInputStream(player1.getInputStream());
                toPlayer1 = new DataOutputStream(player1.getOutputStream());
                fromPlayer2 = new DataInputStream(player2.getInputStream());
                toPlayer2 = new DataOutputStream(player2.getOutputStream());

                //tell player1 to start
                toPlayer1.writeInt(1);

                // game loop that is infinite
                while(true){
                    int row = fromPlayer1.readInt();
                    int col = fromPlayer1.readInt();
                    board[row][col] = PLAYER1_CHAR;
                    Connect4.setBoard(board);

                    if(Connect4.fourInARow(PLAYER1_CHAR)){
                        toPlayer1.writeInt(PLAYER1_WIN);
                        toPlayer2.writeInt(PLAYER1_WIN);
                        sendMove(toPlayer2, row, col);
                        break;
                    }
                    else{
                        toPlayer2.writeInt(CONT);
                        sendMove(toPlayer2, row, col);
                    }

                    row = fromPlayer2.readInt();
                    col = fromPlayer2.readInt();
                    board[row][col] = PLAYER2_CHAR;
                    Connect4.setBoard(board);

                    if(Connect4.fourInARow(PLAYER2_CHAR)){
                        toPlayer1.writeInt(PLAYER2_WIN);
                        toPlayer2.writeInt(PLAYER2_WIN);
                        sendMove(toPlayer1, row, col);
                    }
                    else if(isFull()){
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        sendMove(toPlayer2, row, col);
                        break;
                    }
                    else{
                        toPlayer1.writeInt(CONT);
                        sendMove(toPlayer1, row, col);
                    }


                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         * Sends the move to the specified player
         * @param out Output stream of the player
         * @param r row that was played
         * @param c column that was played
         * @throws IOException If there is a problem writing data
         */
        private void sendMove(DataOutputStream out, int r, int c) throws IOException{
            out.writeInt(r);
            out.writeInt(c);
        }

        /**
         * Is the 2D array full
         * @return true if the array is full, false if not
         */
        private boolean isFull(){
            for(int i = 0; i < ROWS; i++)
                for(int j = 0; j < COLS; j++)
                    if(board[i][j] ==' ')
                        return false;
            return true;
        }
    }

    /**
     * Main method for running the program
     * @param args arguments passed in the terminal
     */
    public static void main(String[] args){
        launch(args);
    }
}
