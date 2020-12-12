package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.Main;

import java.awt.*;

import static sample.Main.*;

public class Controller {
    public static Text text;
    public static Text g_text;
    private static final int CELL_DIMENSION = 50;
    GridPane gridPane = new GridPane();
    private static final StackPane[][] grid = new StackPane[16][16];
    private Rectangle[][] rectangles = new Rectangle[boardSize][boardSize];



    Pane getRootScene() {
        g_text = new Text(game_text);
        g_text.setFont(Font.font(30));
        gridPane.addRow(boardSize+2,g_text);
        gridPane.setAlignment(Pos.CENTER);
        initialize();
        add_button();
        return gridPane;

    }

    private void add_button() {
        javafx.scene.control.Button time_plus_button = new javafx.scene.control.Button("Time++");
        HBox hbBtn3 = new HBox(5);
        hbBtn3.setAlignment(Pos.CENTER);
        hbBtn3.getChildren().add(time_plus_button);
        gridPane.add(hbBtn3, boardSize+2, 1);

        javafx.scene.control.Button catch_button = new Button("Catch!");
        HBox hbBtn2 = new HBox(5);
        hbBtn2.setAlignment(Pos.CENTER);
        hbBtn2.getChildren().add(catch_button);
        gridPane.add(hbBtn2, boardSize+2, 2);

        time_plus_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                click_time_plus();
            }
        });

        catch_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                attempt_catch(maybe_Row,maybe_Col);
            }
        });


    }

    public void display_state() {

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {

                if(row == maybe_Row && col == maybe_Col) {
                    addRectangle(row,col,sensor_color,states[row][col]);
                }
                else
                    addRectangle(row, col, Color.WHITE,states[row][col]);
            }
        }
    }


    public void initialize() {
        for (byte col = 0; col < boardSize; col++) {
            for (byte row = 0; row < boardSize; row++) {
                final byte _col = col, _row = row;
                grid[row][col] = new StackPane();
                grid[row][col].setOnMouseClicked(event -> Main.check_here(_row, _col));
            }
        }

        for (byte row = 0; row < boardSize; row++) {
            for (byte col = 0; col < boardSize; col++) {
                gridPane.add(grid[row][col], col, row+1);
            }
        }

        new Thread(()->{
            while (true) {
                Platform.runLater(this::display_state);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void addRectangle(int row, int col, Color color,double val) {
        Rectangle rectangle = new Rectangle(WIDTH/boardSize, HEIGHT/boardSize,
                Color.AQUA);
        if(val>=0.01)
            text = new Text(String.format("%.4f",val) );
        else
            text = new Text("<0.0001");

        rectangle.setStroke(color);
        rectangle.setStrokeWidth(5);

        if(catch_button && row == maybe_Row && col == maybe_Col) {
            if (caught == true) {
                text = new Text("Caught!!");
            } else {
                text = new Text("Failed!!");
            }
        }

        text.setFont(Font.font(20));
        grid[row][col].getChildren().addAll(rectangle,text);



    }



}


