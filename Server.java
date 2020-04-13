import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.input.KeyCode;
import java.awt.Robot;

public class Server  
{ 
    static int numPlayers = 0;
    static ReentrantLock lock = new ReentrantLock();
    static HashMap<Integer,ClientHandler> players = new HashMap<>();
    static Robot robot;
    
    
    static boolean setPlayer(ClientHandler c,int playerNum){
        if((playerNum <2) || (playerNum > 4)){
            return false;
        }
        try{
            lock.lock();
            if(players.get(playerNum) == null){
                players.put(playerNum,c);
                return true;
            }else{
                ClientHandler old = players.get(playerNum);
                if (old.isClosed()){
                    old.close();
                    players.put(playerNum,c);
                    return true;
                }else{
                    return false;
                }
            }
        }finally{
            lock.unlock();
        }
    }
    
    private static HashMap<Character,KeyCode> getP2(){
        HashMap<Character,KeyCode> map = new HashMap<>();
        map.put('u',KeyCode.DIGIT0);
        map.put('d',KeyCode.DIGIT1);
        map.put('l',KeyCode.DIGIT2);
        map.put('r',KeyCode.DIGIT3);
        map.put('A',KeyCode.DIGIT4);
        map.put('S',KeyCode.DIGIT5);
        map.put('D',KeyCode.DIGIT6);
        map.put('W',KeyCode.DIGIT7);
        map.put('Z',KeyCode.DIGIT8);
        map.put('X',KeyCode.DIGIT9);
        map.put('C',KeyCode.DOLLAR);
        map.put('V',KeyCode.EQUALS);
        return map;
    }
    
    private static HashMap<Character,KeyCode> getP3(){
        HashMap<Character,KeyCode> map = new HashMap<>();
        map.put('u',KeyCode.NUMPAD0);
        map.put('d',KeyCode.NUMPAD1);
        map.put('l',KeyCode.NUMPAD2);
        map.put('r',KeyCode.NUMPAD3);
        map.put('A',KeyCode.NUMPAD4);
        map.put('S',KeyCode.NUMPAD5);
        map.put('D',KeyCode.NUMPAD6);
        map.put('W',KeyCode.NUMPAD7);
        map.put('Z',KeyCode.NUMPAD8);
        map.put('X',KeyCode.NUMPAD9);
        map.put('C',KeyCode.OPEN_BRACKET);
        map.put('V',KeyCode.CLOSE_BRACKET);
        return map;
    }
    
    private static HashMap<Character,KeyCode> getP4(){
        HashMap<Character,KeyCode> map = new HashMap<>();
        map.put('u',KeyCode.SOFTKEY_0);
        map.put('d',KeyCode.SOFTKEY_1);
        map.put('l',KeyCode.SOFTKEY_2);
        map.put('r',KeyCode.SOFTKEY_3);
        map.put('A',KeyCode.SOFTKEY_4);
        map.put('S',KeyCode.SOFTKEY_5);
        map.put('D',KeyCode.SOFTKEY_6);
        map.put('W',KeyCode.SOFTKEY_7);
        map.put('Z',KeyCode.SOFTKEY_8);
        map.put('X',KeyCode.SOFTKEY_9);
        map.put('C',KeyCode.LEFT_PARENTHESIS);
        map.put('V',KeyCode.RIGHT_PARENTHESIS);
        return map;
    }
    
    static HashMap<Character,KeyCode> getKeyMap(int playerNum){
        if(playerNum==2) return getP2();
        if(playerNum==3) return getP3();
        if(playerNum==4) return getP4();
        return null;
    }
    
    public static void main(String[] args) throws IOException  
    {    
        try{
        robot = new Robot();
        }catch(Exception e){
            System.out.println("Cannot Create Robot");
            System.exit(1);
        }
        int port = 80;    
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }

        // server is listening on port
        ServerSocket ss = new ServerSocket(port); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null;      
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept();                   
                System.out.println("A new client is connected : " + s); 
                System.out.println("Assigning new thread for this client"); 
                // create a new thread object 
                Thread t = new Thread(new ClientHandler(s)); 
                // Invoking the start() method 
                t.start(); 
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    private DataInputStream dis = null; 
    private DataOutputStream dos = null; 
    private final Socket s; 
    private int clientId;
  
    // Constructor 
    public ClientHandler(Socket s)  
    { 
        this.s = s; 
        this.clientId=clientId;
        
    } 
    
    boolean isClosed(){
        return s.isClosed();
    }
  
    void close(){
        try{
            // closing resources 
            this.dis.close(); 
            this.dos.close();
            this.s.close();            
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    }
    
    @Override
    public void run()  
    {     
        try
        { 
            this.dos = new DataOutputStream(s.getOutputStream());
            this.dis = new DataInputStream(s.getInputStream()); 
            //see if player num is legal
            int playerNum = dis.readInt();
            if(Server.setPlayer(this,playerNum)){
                dos.writeInt(playerNum);
            }else{
                dos.writeInt(-1);
                System.out.println("player num invalid or already in use:"+playerNum);
                this.close();
                return;
            }
            
            HashMap<Character,KeyCode> map = Server.getKeyMap(playerNum);
            
            while(true){
                char c = dis.readChar();
                    
                if (c == 'E') {
                    //exit
                    break;
                }else{
                    System.out.println("HERE");
                    Server.robot.keyPress(map.get(c).impl_getCode());
                }
            }
            
            
            this.close();
                 
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 

    
} 