package ui; /**
 * This class handles the GUI for the connect4 game
 * @version 1.0
 * @author Justin Kolich
 */

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.Optional;

public class Connect4GUI extends Application{
    /**
     * Private inner class that is for a player object
     */
    private static class Player{
        private char classifier;
        private int pieces = 21;
        private int num;
        private Color color;
        Player(char x, Color y){
            if(x == 'X'){
                num = 1;
            }
            else{
                num = 2;
            }
            classifier = x;
            color = y;
        }

        /**
         * Returns color type of a player
         * @return color of player
         */
        public Color getColor(){
            return color;
        }

        /**
         *
         * @return classifier of the player
         */
        public char getC(){ return classifier; }

        /**
         *
         * @return number that the player has, will be 1 or 2
         */
        public int getNum(){ return num; }

        /**
         * Decrements pieces by one
         */
        public void moveMade() {pieces--;}

        /**
         *
         * @return number of pieces a player has
         */
        public int getPieces(){return pieces;}
    }

    // These are used to initialize different things that are used throughout different modules.
    // it makes things like having button pushing under one module much easier, removes a lot of duplicate code
    private static boolean twoP;
    private static Player p1 = new Player('X', Color.RED);
    private static Player p2 = new Player('O', Color.YELLOW);
    private static Connect4ComputerPlayer c = new core.Connect4ComputerPlayer();
    private static Player currP = p1;
    private static Label label;
    private static Label errLabel;
    private static boolean win = false;
    private static int cRadius = 25;
    private static GridPane board = new GridPane();
    private static char[][] gBoard = new char[6][7];

    /**
     * Starting method for the GUI, First sends an alert to the user asking if they would like to play versus
     * a computer or another player. From there it initializes buttons, a VBox, a HBox, and a borderpane
     * @param primaryStage the primary stage that will be displayed on.
     */
    @Override
    public void start(Stage primaryStage){
        initialize();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Choose your opponent");
        alert.setHeaderText("Would you like to play vs another player or a computer");
        ButtonType buttonP = new ButtonType("Another Player");
        ButtonType buttonC = new ButtonType("Play vs CPU");
        alert.getButtonTypes().setAll(buttonP,buttonC);
        Optional<ButtonType> result = alert.showAndWait();
        label = new Label("Player 1 it is your turn");
        errLabel = new Label("");
        int boardGap = 10;

        if(result.get().equals(buttonP)){
            twoP = true;
        }
        else{
            twoP = false;
        }

        //create a HBox of buttons for users to click where they want to play
        Button col1 = new Button("1");
        Button col2 = new Button("2");
        Button col3 = new Button("3");
        Button col4 = new Button("4");
        Button col5 = new Button("5");
        Button col6 = new Button("6");
        Button col7 = new Button("7");
        HBox bRow = new HBox();
        bRow.setSpacing(35);
        bRow.setAlignment(Pos.CENTER);
        bRow.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7);
        label.setFont(new Font(20));
        errLabel.setFont(new Font(15));

        VBox info = new VBox();
        info.setSpacing(5);
        info.getChildren().addAll(label, errLabel);

        //create a grid to display a typical connect4 grid on the screen
        for(int i = 0; i < Connect4.getRows(); i++){
            for(int j = 0; j < Connect4.getCols(); j++){
                Circle circle = new Circle(cRadius, Color.WHITE);
                board.add(circle, j, i);
            }
        }
        board.setHgap(boardGap);
        board.setVgap(boardGap);
        board.setStyle("-fx-background-color:blue");
        board.setMaxWidth(Connect4.getCols()*(boardGap+cRadius));
        board.setMaxHeight(Connect4.getRows()*(boardGap+cRadius));
        board.setPadding(new Insets(boardGap/2,boardGap/2,boardGap/2,boardGap/2));

        // borderpane for a layout
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(board);
        borderPane.setTop(info);
        borderPane.setBottom(bRow);
        info.setAlignment(Pos.CENTER);

        // setting the scene
        Scene scene = new Scene(borderPane, 420, 440);
        primaryStage.setTitle("core.Connect4");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        // lambda expressions for each button click
        col1.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
        col2.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
        col3.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
        col4.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
        col5.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
        col6.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
        col7.setOnMouseClicked(e->{
            Object node = e.getSource();
            Button b = (Button)node;
            buttonHandler(Integer.parseInt(b.getText()));
        });
    }

    /**
     * Initializes the string board so that it contains non null values
     */
    public void initialize(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                gBoard[i][j] = ' ';
            }
        }
    }

    /**
     * runs the program
     * @param args passed by the main() method
     */
    public static void run(String[] args){
        launch(args);
    }

    /**
     * Places a piece in the board and checks if anybody has won
     * @param col column number that wants to be placed in
     * @param p which player is playing
     * @return the row number that was placed in, otherwise -1 if the move was invalid
     */
    public static int place(int col, Player p){
        int row =  Connect4.place(col, p.getC());
        if(row!=-1) {
            gBoard[row][col - 1] = p.getC();
            win = Connect4.fourInARow(currP.getC());
        }
        return row;
    }

    /**
     * Switches from player 1 to player 2 or vice versa
     * @param p Current player
     */
    public static void switchP(Player p){
        if(p.getC()== 'X' && twoP){
            currP = p2;
        }
        else{
            currP = p1;
        }
    }

    /**
     * calls the connect4ComputerPlayer class object in order to make the proper move
     */
    public static void comp(){
        c.makeMove(gBoard);
        c.moveMade();
        gBoard[c.getRow()][c.getCol()] = c.getC();
        win = Connect4.fourInARow(c.getC());
    }

    /**
     * Handles all instances of the buttons being pressed
     * @param col the text of the button, aka the column number that is to be placed in
     */
    public static void buttonHandler(int col){
        if(!win) {
            int row = place(col, currP);
            if (row != -1) {
                board.add(new Circle(cRadius, currP.getColor()), col-1, row);
                currP.moveMade();
                if(win){
                    errLabel.setText("Player " + currP.getNum() + " has won!");
                }
                else if(twoP) {
                    switchP(currP);
                    label.setText("Player " + currP.getNum() + " it is your turn");
                    errLabel.setText("");
                }
                else{
                    comp();
                    board.add(new Circle(cRadius, p2.getColor()), c.getCol(), c.getRow());
                    p2.moveMade();
                    if(win){
                        label.setText("The computer has won!");
                    }
                }
            } else {
                errLabel.setText("Invalid move, please try again");
            }

        }
        if(currP.getPieces() == 0){
            label.setText("The game has ended in a draw");
            win = true;
        }
    }
}
