import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class pop3Server {
       public static void main(String[] args) {
    		try {
    			ServerSocket ss=new ServerSocket(110);
    	 
    			System.out.println("pop3 server is ready");
    			while(true){
    			 Socket s=ss.accept();
    		     new Thread(new pop3ServerSession(s)).start();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
	}
}

