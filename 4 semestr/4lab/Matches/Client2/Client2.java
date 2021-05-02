import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ServerThread implements Runnable {

    private final Thread t;
    private final Socket socket;
    private InputStream in = null;
    private OutputStream out = null;
    private String ServerMsg;


    ServerThread(String Name, Socket server) {
        this.socket = server;
        t = new Thread(this, Name);
        if (Connection()) t.start();
    }

    private boolean Connection() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            System.out.println("Connection on...");
            return true;
        } catch (Exception e) {
            System.out.println("Error to connection...");
            return false;
        }
    }

    @Override
    public void run() {

        Scanner scan = new Scanner(System.in);

        while (true) {

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                int Enter = 0;
                if (ServerMsg != null && ServerMsg.equals("Your turn!") ) {

                    do {
                        System.out.println("Enter -1 if you want to exit.");
                        System.out.println("Pull 1 to 5 matches: ");
                        Enter = scan.nextInt();
                    } while (Enter != -1 && Enter < 1 || Enter > 5);

                    Object obj = String.valueOf(Enter);
                    System.out.println("My:"  + Enter);
                    oos.writeObject(obj);
                    oos.flush();


                }

                out.write(bos.toByteArray());
                oos.close();
                bos.close();
                if (Enter == -1) break;

            } catch (Exception e) {
                System.out.println("The message was not sent to the server...");
                System.out.println("Error: "+ e.getMessage());
                break;
            }

            ServerMsg = null;

            try {
                byte[] bts = new byte[in.available()];
                in.read(bts);
                ByteArrayInputStream bis = new ByteArrayInputStream(bts);
                ObjectInputStream ois = new ObjectInputStream(bis);
                ServerMsg = (String) ois.readObject();
                System.out.println(ServerMsg);
            } catch (Exception e) {
                if (e.getMessage() != null){
                    System.out.println(e.getMessage());
                    break;
                }
            }

            try {

                t.sleep(1000);
            } catch (Exception ignored) {
            }
        }

        try {
            System.out.println("Exit");
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}


public class Client2 {

    public static void main(String[] args) {

        try{
            Socket s = new Socket("127.0.0.1",1111);
            System.out.println("Local port: " +  s.getLocalPort());
            System.out.println("Remote port: " + s.getPort());

            new ServerThread("Server",s);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
