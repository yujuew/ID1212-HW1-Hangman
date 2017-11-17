/*
  Client side:
  Receives and displays data from the server
 */
package javahangman;

/**
 *
 * @author Shine
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static final String IP_ADDR = "localhost";//server address  
    public static final int PORT = 12345;//server port number.    

    public static void main(String[] args) {
        System.out.println("Server Up...");
        Socket socket = null;
        try {
            //create a socket and connect it to the certain port of the server
            socket = new Socket(IP_ADDR, PORT);
            //read data from the server ,read0   
            DataInputStream input0 = new DataInputStream(socket.getInputStream());
            String ret0 = input0.readUTF();
            System.out.println(ret0);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {

            try {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String ret = input.readUTF();//read 1
                System.out.println(ret);

                ret = input.readUTF();//read2
                System.out.println(ret);

                if (ret.contains("You have run out of chances")) {
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String str = "";
                    out.writeUTF(str);
                } else {
                    //if there are left chances,
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    System.out.print("--Please guess: \t");                  
                    String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    //enter"QUIT" to sleep the client thread
                    if ("QUIT".equals(str)) {
                        System.out.println("Client is disconect");
                        Thread.sleep(5000000);
                        break;
                    }
                    out.writeUTF(str);//write

                }
                ret = input.readUTF();//read3
                System.out.println(ret);
              
            } catch (Exception e) {
                System.out.println("Client Error:" + e.getMessage());
            }
        }
    }
}
