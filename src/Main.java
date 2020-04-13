import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;


import java.net.*; 
import java.io.*; 
import java.util.concurrent.locks.ReentrantLock;
public class Main extends Application 
{
        
    private Client client;
    private Group root;
    private GraphicsContext gc;
    private Scene theScene;
    private static  String ip_name = "localhost";
    private static int port = 80;
    private static int playerNum = -1;
    public static void main(String[] args) {
        playerNum = Integer.parseInt(args[0]);
        if(args.length>1){
            ip_name = args[1];
        }
        if(args.length>2){
            port = Integer.parseInt(args[2]);
        }
 
        launch(args);
    }
    
    public void start(Stage theStage){
        
        theStage.setTitle( "VIDEO GAME CLIENT BY Yehuda Goldfeder" );
    
        root = new Group();
        theScene = new Scene( root );
        theStage.setScene( theScene );
        theStage.setAlwaysOnTop(true);   
        Canvas canvas = new Canvas(50, 50);
        root.getChildren().add( canvas );
             
        gc = canvas.getGraphicsContext2D();
                                  
        
        theStage.show();
        
        //register mouse listener
        theScene.setOnKeyPressed(event -> keyPress(event));

        //connect to server     
        client = new Client(ip_name,port);
        int ret = client.connect(playerNum);
        if(ret==-1){
            System.out.println("player num invalid or already in use:"+playerNum);
            client.close();
            System.exit(0);
        }
    }
    
    private void keyPress(KeyEvent e){
        KeyCode keyCode = e.getCode();
        if (keyCode.equals(KeyCode.UP)) {
            client.send('u');
            return;
        }
        if (keyCode.equals(KeyCode.DOWN)) {
            client.send('d');
            return;
        }
        if (keyCode.equals(KeyCode.LEFT)) {
            client.send('l');
            return;
        }
        if (keyCode.equals(KeyCode.RIGHT)) {
            client.send('r');
            return;
        }
        if (keyCode.equals(KeyCode.A)) {
            client.send('A');
            return;
        }
        if (keyCode.equals(KeyCode.S)) {
            client.send('S');
            return;
        }
        if (keyCode.equals(KeyCode.D)) {
            client.send('D');
            return;
        }
        if (keyCode.equals(KeyCode.W)) {
            client.send('W');
            return;
        }
                
        if (keyCode.equals(KeyCode.Z)) {
            client.send('Z');
            return;
        }
        if (keyCode.equals(KeyCode.X)) {
            client.send('X');
            return;
        }
        if (keyCode.equals(KeyCode.C)) {
            client.send('C');
            return;
        }
        if (keyCode.equals(KeyCode.V)) {
            client.send('V');
            return;
        }       

            
        if (keyCode.equals(KeyCode.ESCAPE)) {
            client.send('E');
            System.exit(0);
        }
    }
        

}
