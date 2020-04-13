import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.KeyEvent;
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
    
    private static HashMap<Character,Integer> getP2(){
        HashMap<Character,Integer> map = new HashMap<>();
        map.put('u',KeyEvent.VK_0);
        map.put('d',KeyEvent.VK_0);
        map.put('l',KeyEvent.VK_1);
        map.put('r',KeyEvent.VK_3);
        map.put('A',KeyEvent.VK_4);
        map.put('S',KeyEvent.VK_5);
        map.put('D',KeyEvent.VK_6);
        map.put('W',KeyEvent.VK_7);
        map.put('Z',KeyEvent.VK_8);
        map.put('X',KeyEvent.VK_9);
        map.put('C',KeyEvent.VK_MINUS);
        map.put('V',KeyEvent.VK_EQUALS);
        return map;
    }
    
    private static HashMap<Character,Integer> getP3(){
        HashMap<Character,Integer> map = new HashMap<>();
        map.put('u',KeyEvent.VK_NUMPAD0);
        map.put('d',KeyEvent.VK_NUMPAD1);
        map.put('l',KeyEvent.VK_NUMPAD2);
        map.put('r',KeyEvent.VK_NUMPAD3);
        map.put('A',KeyEvent.VK_NUMPAD4);
        map.put('S',KeyEvent.VK_NUMPAD5);
        map.put('D',KeyEvent.VK_NUMPAD6);
        map.put('W',KeyEvent.VK_NUMPAD7);
        map.put('Z',KeyEvent.VK_NUMPAD8);
        map.put('X',KeyEvent.VK_NUMPAD9);
        map.put('C',KeyEvent.VK_OPEN_BRACKET);
        map.put('V',KeyEvent.VK_CLOSE_BRACKET);
        return map;
    }
    
    private static HashMap<Character,Integer> getP4(){
        HashMap<Character,Integer> map = new HashMap<>();
        map.put('u',KeyEvent.VK_F13);
        map.put('d',KeyEvent.VK_F14);
        map.put('l',KeyEvent.VK_F15);
        map.put('r',KeyEvent.VK_F16);
        map.put('A',KeyEvent.VK_F17);
        map.put('S',KeyEvent.VK_F18);
        map.put('D',KeyEvent.VK_F19);
        map.put('W',KeyEvent.VK_F20);
        map.put('Z',KeyEvent.VK_F21);
        map.put('X',KeyEvent.VK_F22);
        map.put('C',KeyEvent.VK_F23);
        map.put('V',KeyEvent.VK_F24);
        return map;
    }
    
    static HashMap<Character,Integer> getKeyMap(int playerNum){
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
    private int playerNum = -1;
  
    // Constructor 
    public ClientHandler(Socket s)  
    { 
        this.s = s;         
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
            playerNum = dis.readInt();
            if(Server.setPlayer(this,playerNum)){
                dos.writeInt(playerNum);
            }else{
                dos.writeInt(-1);
                System.out.println("player num invalid or already in use:"+playerNum);
                this.close();
                return;
            }
            
            HashMap<Character,Integer> map = Server.getKeyMap(playerNum);
            
            while(true){
                char c = dis.readChar();
                    
                if (c == 'E') {
                    //exit
                    break;
                }else{
                    Server.robot.keyPress(map.get(c));
                }
            }
            
            
           
                 
        }catch(SocketException e){
            System.out.println("disconnecting "+ playerNum);
        }catch(Exception e){ 
            e.printStackTrace(); 
        } finally{
             this.close();
        }
    } 

    
} 