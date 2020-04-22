package core;

import core.Connect4;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client class for connect4
 *
 * @author Justin Kolich
 * @version 1.0
 */
public class Connect4Client extends Application implements Connect4Constants {

    //Only move when it is the players turn
    private boolean myTurn = false;
    //Empty char for myPiece, it will be updated later
    private char myPiece = ' ';
    //Empty char for thre other players piece
    private char otherPiece = ' ';
    //Create a 2D char array for storing the board
    private char[][] cellBoard = new char[Connect4Constants.ROWS][Connect4Constants.COLS];
    // Establish the gridPane here so that it can be written through throughout
    private GridPane gBoard = new GridPane();
    // labels to tell the players what is happening
    private Label titlelbl = new Label();
    private Label statuslbl = new Label();
    // indicate when the game is over
    private boolean gameOver = false;
    // Row and column the client has played in
    private int rowMove;
    private int colMove;
    //Create input and output streams to write and receive data
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    // waiting for the player to make a move, becomes false when the player makes a move
    private boolean waiting = true;
    //host name
    private String host = "localhost";
    @Override
    /**
     * Create the GUI for the connect4 game
     * @param primaryStage is the stage that will hold panes
     */
    public void start(Stage primaryStage){
        for(int i = 0; i < Connect4Constants.ROWS; i++){
            for(int j = 0; j < Connect4Constants.COLS; j++){
                Circle c = new Circle(Connect4Constants.CIRCLE_RAD, Color.WHITE);
                gBoard.add(c,j,i);
            }
        }
        gBoard.setStyle("-fx-background-color: blue");
        gBoard.setPadding(new Insets(5,5,5,5));
        gBoard.hgapProperty().bind(primaryStage.widthProperty().divide(50));
        gBoard.vgapProperty().bind(primaryStage.heightProperty().divide(50));
        gBoard.prefHeightProperty().bind(primaryStage.heightProperty());
        gBoard.prefWidthProperty().bind(primaryStage.widthProperty());
        gBoard.setAlignment(Pos.CENTER);

        HBox buttons = new HBox();
        buttons.spacingProperty().bind(Bindings.min(primaryStage.widthProperty().divide(12),
                primaryStage.heightProperty().divide(12)));
        buttons.setPadding(new Insets(5,5,5,5));
        buttons.setAlignment(Pos.CENTER);
        for(int i = 0; i < Connect4.getCols(); i++){
            Button button = new Button(Integer.toString(i+1));
            button.setOnMouseClicked(e->handleButton(e));
            buttons.getChildren().add(button);
        }

        VBox labels = new VBox();
        labels.setSpacing(5);
        labels.getChildren().addAll(titlelbl, statuslbl);
        labels.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(labels);
        borderPane.setCenter(gBoard);
        borderPane.setBottom(buttons);

        Scene scene = new Scene(borderPane,500,500);
        primaryStage.setTitle("Connect4 Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e->Platform.exit());

        connectToServer();
    }

    /**
     * Creates a socket as well as input and output streams, and then creates a thread to handle set up
     * player number, as well as to run the game
     */
    private void connectToServer(){
        try {
            // Socket to connect to the server
            Socket socket = new Socket(host,8000);

            // create input and output streams
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //Create a new thread to handle the game
        new Thread(()->{
            try{
                int player = fromServer.readInt();
                if(player == Connect4Constants.PLAYER1){
                    myPiece = Connect4Constants.PLAYER1_CHAR;
                    otherPiece = Connect4Constants.PLAYER2_CHAR;
                    Platform.runLater(()-> {
                        titlelbl.setText("Player 1 with color Red");
                        statuslbl.setText("Waiting for player 2 to join");
                    });
                    // notification to start the game
                    fromServer.readInt();
                    Platform.runLater(()-> statuslbl.setText("Player 2 has joined. You start first"));
                    // if you are player 1, you start first
                    myTurn = true;
                }
                else if(player == Connect4Constants.PLAYER2){
                    myPiece = Connect4Constants.PLAYER2_CHAR;
                    otherPiece = Connect4Constants.PLAYER1_CHAR;
                    Platform.runLater(()->{
                        titlelbl.setText("Player 2 with color Yellow");
                        statuslbl.setText("Waiting for player 1 to move");
                    });
                }
                while(!gameOver){
                    if(player == Connect4Constants.PLAYER1){
                        waitForPlayer();
                        sendMove();
                        Platform.runLater(()->statuslbl.setText("Waiting for player 2 to move"));
                        recieveFromServer();
                    }
                    else if(player == Connect4Constants.PLAYER2){
                        recieveFromServer();
                        waitForPlayer();
                        sendMove();
                        Platform.runLater(()->statuslbl.setText("Waiting for player 1 to move"));
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Waits for player to make a move, that way the function doesnt keep moving without the player making a move
     * @throws InterruptedException if the thread si interrupted
     */
    private void waitForPlayer() throws InterruptedException{
        while(waiting){
            Thread.sleep(100);
        }
        waiting = true;
    }

    /**
     * Sends the move to the server after it is made
     * @throws IOException if there is an error writing a move to the server
     */
    private void sendMove() throws IOException{
        toServer.writeInt(rowMove);
        toServer.writeInt(colMove);
    }

    /**
     *  read the status of the game from the server, handle wins, draws, and continue
     * @throws IOException if there is an error reading data
     */
    private void recieveFromServer() throws IOException{
        int status = fromServer.readInt();
        if(status == Connect4Constants.PLAYER1_WIN){
            gameOver = true;
            if(myPiece == Connect4Constants.PLAYER1_CHAR){
                Platform.runLater(()-> statuslbl.setText("You won!"));
            }
            else if(myPiece == Connect4Constants.PLAYER2_CHAR){
                Platform.runLater(()-> statuslbl.setText("Player 1 has won"));
                recieveMove();
            }
        }
        else if(status == Connect4Constants.PLAYER2_WIN){
            gameOver = true;
            if(myPiece == Connect4Constants.PLAYER2_CHAR){
                Platform.runLater(()-> statuslbl.setText("You won!"));
            }
            else if (myPiece == Connect4Constants.PLAYER1_CHAR){
                Platform.runLater(()-> statuslbl.setText("Player 2 has won!"));
                recieveMove();
            }
        }
        else if (status == Connect4Constants.DRAW) {
            gameOver = true;
            Platform.runLater(()-> statuslbl.setText("The game has ended in a draw"));
            if(myPiece == Connect4Constants.PLAYER2_CHAR){
                recieveMove();
            }
        }
        else{
            recieveMove();
            Platform.runLater(()->statuslbl.setText("Your turn"));
            myTurn = true;
        }
    }

    /**
     * Method to recieve a move from the other player
     * @throws IOException if there is a an error reading data
     */
    private void recieveMove() throws IOException{
        //get the other players move
        int row = fromServer.readInt();
        int col = fromServer.readInt();
        Connect4.place(col+1, otherPiece);
        Color c = myPiece == 'O'? Color.RED : Color.YELLOW;
        Platform.runLater(()->gBoard.add(new Circle(Connect4Constants.CIRCLE_RAD, c), col, row));
    }

    /**
     * Handles the event of each click of a button
     * @param e a description of the event that happened
     */
    private void handleButton(MouseEvent e){
        if(myTurn) {
            Object node = e.getSource();
            Button b = (Button) node;
            Color c = myPiece == 'X' ? Color.RED : Color.YELLOW;
            int col = Integer.parseInt(b.getText());
            int i = Connect4.place(col, myPiece);
            if (i != -1) {
                colMove = col-1;
                rowMove = i;
                Platform.runLater(() -> gBoard.add(new Circle(Connect4Constants.CIRCLE_RAD, c), colMove, i));
                waiting = false;
                myTurn = false;
            }
        }
    }

    /**
     * Main method that lets the UI run
     * @param args arguments passed in by the user
     */
    public static void main(String[] args){
        launch(args);
    }
}
