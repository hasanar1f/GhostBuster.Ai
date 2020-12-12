package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Random;

public class Main extends Application {


    ///////////// Game Settings /////////////
    public static int boardSize = 8;
    public static int[] toRow = {0,0,0,-1,1,1,1,-1,-1};
    public static int[] toCol = {0,-1,1,0,0,1,-1,1,-1};
    public static double[] trans_probability = {0.04,0.2,0.2,0.2,0.2,0.04,0.04,0.04,.04};
    /////////////////////////////////////////

    public static int WIDTH = 1000;
    public static int HEIGHT = 600;
    public static int nTrans = 9;
    public static double[][] states = new double[boardSize][boardSize];
    private static double[][] temp_states = new double[boardSize][boardSize];
    public static Random random = new Random(100);
    public static int[] a_move = new int[100];
    public static double[][][] ep;
    public static int maybe_Row =  -1;
    public static int maybe_Col = - 1;
    public static int real_Row;
    public static int real_col;
    public static javafx.scene.paint.Color sensor_color;
    public static boolean caught;
    public static boolean catch_button;
    public  static String game_text = "Welcome!";


    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller newGame = new Controller();
        primaryStage.setTitle("Catch The Ghost");
        primaryStage.setScene(new Scene(newGame.getRootScene(), WIDTH+200, HEIGHT+100));
        primaryStage.show();
    }

    public static void click_time_plus() {

        maybe_Row = -1; maybe_Col = -1;
        catch_button = false;
        caught = false;
        int move = a_move[ random.nextInt(100) ];

        int rt = real_Row + toRow[move];
        int ct = real_col + toCol[move];

        if( isValid(rt,ct) ) {
            real_Row = rt;
            real_col = ct;
        }

        System.out.println("Ghost is here : "+real_Row+" , "+real_col);


        for(int r=0;r<boardSize;r++) {
            for(int c=0;c<boardSize;c++) {
                double temp = 0.0;
                double sum = 0.0;
                for(int i=0;i<nTrans;i++) {
                    if(isValid(r+toRow[i],c+toCol[i]))
                        sum += trans_probability[i];
                }


                for(int i=0;i<nTrans;i++) {
                    if(isValid(r+toRow[i],c+toCol[i])) {
                        temp += (states[r + toRow[i]][c + toCol[i]] * (trans_probability[i] / sum * 1.0));
                    }
                }

                temp_states[r][c] = temp ;


            }
        }

        states = temp_states.clone();

    }

    public static void check_here(int row,int col) {
        maybe_Row = row;
        maybe_Col = col;
        catch_button = false;
        caught = false;
        int dist = getDist(row,col,real_Row,real_col);
        int jj = getIJ(row,col);
        if(dist <= 1) {
            for(int i=0;i<boardSize;i++) {
                for(int j=0;j<boardSize;j++) {
                    int ii = getIJ(i,j);
                    temp_states[i][j] = states[i][j]*ep[ii][jj][Sensor.RED.val];
                }
            }
            states = normalize(temp_states);
            sensor_color = Color.RED;

        }
        else if(dist == 2) {
            for(int i=0;i<boardSize;i++) {
                for(int j=0;j<boardSize;j++) {
                    int ii = getIJ(i,j);
                    temp_states[i][j] = states[i][j]*ep[jj][ii][Sensor.YELLOW.val];
                }
            }
            states = normalize(temp_states);
            sensor_color = Color.YELLOW;
        }
        else {
            for(int i=0;i<boardSize;i++) {
                for(int j=0;j<boardSize;j++) {
                    int ii = getIJ(i,j);
                    temp_states[i][j] = states[i][j]*ep[jj][ii][Sensor.GREEN.val];
                }
            }
            states = normalize(temp_states);
            sensor_color = Color.GREEN;
        }



    }



    public static boolean isValid(int r,int c) {
        if(r < 0 || r >= boardSize || c < 0 || c >= boardSize)
            return false;
        else return true;
    }

    private static void init_game() {
        game_text = "";
        caught = false;
        double d = 1.0 / (boardSize*boardSize);
        for(int i=0;i<boardSize;i++) {
            for(int j=0;j<boardSize;j++) {
                states[i][j] = d;
            }

        }



        int k = 0;
        for(int i=0;i<nTrans;i++) {
            int N = (int) (trans_probability[i]*100);
            for(int j=0;j<N;j++,k++) {
                a_move[k] = i;
            }
        }

        int N = boardSize*boardSize;

        ep = new double[N][N][3];

        for(int i=0;i<boardSize;i++) {
            for(int j=0;j<boardSize;j++) {
                int II = getIJ(i,j);
                for(int ii=0;ii<boardSize;ii++) {
                    for(int jj=0;jj<boardSize;jj++) {
                        int JJ = getIJ(ii,jj);
                        int dist = getDist(i,j,ii,jj);

                        if(dist<=1) { // RED COLOR
                            ep[II][JJ][Sensor.RED.val] =  0.9;
                            ep[II][JJ][Sensor.YELLOW.val] =  0.007;
                            ep[II][JJ][Sensor.GREEN.val] =  0.003;
                        }
                        else if(dist==2) { // YELLOW
                            ep[II][JJ][Sensor.RED.val] =  0.05;
                            ep[II][JJ][Sensor.YELLOW.val] =  0.9;
                            ep[II][JJ][Sensor.GREEN.val] =  0.05;
                        }
                        else { // GREEN
                            ep[II][JJ][Sensor.RED.val] =  0.002;
                            ep[II][JJ][Sensor.YELLOW.val] =  0.008;
                            ep[II][JJ][Sensor.GREEN.val] =  0.9;
                        }

                    }
                }
            }
        }

//        for(int i=0;i<N;i++) {
//            for(int j=0;j<N;j++) {
//                System.out.print(ep[i][j][0]+" ");
//            }
//            System.out.println();
//        }


    }

    public static double[][] normalize(double[][] arr) {

        double sum = 0.0;

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                sum += arr[i][j];
            }
        }

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {

                arr[i][j] = arr[i][j]/sum;

            }
        }

        return arr;

    }

    public static void attempt_catch(int row,int col) {

        catch_button = true;
        if(row==real_Row && col==real_col) {
            caught = true;
            game_text = "You just caught the Ghost!!!";
        }
        else
        {
            game_text = "you failed to catch the Ghost!";
        }

    }


    public static void main(String[] args) {
        init_game();
        launch(args);
    }

    public static int getIJ(int i,int j) {
        return i*boardSize+j;
    }

    public static int getDist(int x1,int y1, int x2,int y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }


}

enum Sensor {
    RED(0), YELLOW(1), GREEN(2);

    int val;
    Sensor(int color) {
        this.val = color;
    }

}



