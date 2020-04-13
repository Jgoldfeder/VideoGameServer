import java.util.*; 
import java.net.*; 
import java.io.*; 
import java.util.concurrent.locks.ReentrantLock;

public class Client
{
    
    private static String ip_name;
    private static int port;
    private Socket socket            = null;  
    private DataInputStream dis;
    private DataOutputStream dos;

    
    Client(String ip_name,int port){
        this.ip_name=ip_name;
        this.port=port;
    }
    
    
    void send(char c){
        try{
            dos.writeChar(c);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    void send(int i){
        send((char)i);
    }
    
    int connect(int playerNum){
     
          try
        { 
            System.out.println("Connecting to network");  

            InetAddress ip = InetAddress.getByName(ip_name); 
      
            // establish the connection with server port  
            socket = new Socket(ip, port);
            System.out.println("Connected");

            // obtaining input and out streams             
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream()); 

            //try sending what player we are
            dos.writeInt(playerNum);
            
            // see if this player num is allowed
            int result = dis.readInt();
            if(playerNum!=result){
                ///rejected
                return -1;
            }
            return playerNum;
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
         
        return -1;
    }
    
    void close(){
        try{
            // closing resources 
            this.dis.close(); 
            this.dos.close();
            this.socket.close();            
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    }
}
