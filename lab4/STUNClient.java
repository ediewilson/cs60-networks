import java.io.*;
import java.net.*;
import java.util.*;

public class STUNClient {
    Socket socket;

    public STUNClient(Socket sock) {
        this.sock = sock;
    }

    public String canConnect() {
        
        String myIP = "";
        /* send a message and see if you can find your ip-address:port-number
        * if you can {
        *    testResult = true
        *}
        * else {
        *   System.out.println("Connect test failed"");
        *}
        */
        
        System.out.println("Peer can connect to" + myIP+ ".");
        return myIP; 
    } 

    public String unreachableIP() {
        
        String myIP = "";
        /* send a message and see if you can find your ip-address:port-number
        * if you can & are reachable {
        *    testResult = true
        *} */
        System.out.println("At " +myIP + ", but peer can't connect.");
        return myIP; 
    } 

    private boolean unreachableServer() {
        
        boolean testResult = false;

        if(result.NetType != STUN_NetType.UdpBlocked) {
            System.out.println("The server doesn't want to talk to you.");
            return !testResult;
        }
        
        return testResult; 
    } 


    public static void main(String [] args) {
        Socket socket = new Socket(AddressFamily.InterNetwork,SocketType.Dgram,ProtocolType.Udp);
        socket.Bind(new IPEndPoint(IPAddress.Any,0));
        
        StunClient thisClient = new StunClient(socket);

        if (thisClient.unreachableServer(socket)) {
            System.exit(1);
        }
        else {
            String myIP = thisClient.unreachableIP();
            if (myIP == null) {
                myIP = thisClient.unreachableIP();
            }
            if (myIP == null) {
                System.exit(2);
            }
            else System.exit(0);
        }
    }

}