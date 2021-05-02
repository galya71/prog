import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Game implements Runnable {
    String User1Msg, User2Msg;
    int NumberMatches = 37;
    boolean PlayerTurn = true, SentMessage = false;
    private final Thread t;
    private ThreadUser User1;
    private ThreadUser User2;
    private InputStream in = null;
    private OutputStream out = null;

    public Game(String Name) {
        User1 = null;
        User2 = null;
        t = new Thread(this, Name);
        t.start();
    }

    public void OutMsg(Socket user, String Msg) {
        try {
            in = user.getInputStream();
            out = user.getOutputStream();
            System.out.println("Server msg: " + Msg);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(Msg);
            oos.flush();

            out.write(bos.toByteArray());

        } catch (Exception e) {
            System.out.println("Error msg to connection...");
            try {
                user.close();
            } catch (Exception ignore) {
            }
        }
    }

    public void setUser1(ThreadUser user) {
        this.User1 = user;
    }

    public void setUser2(ThreadUser user) {
        this.User2 = user;
    }


    private void SwitchPlayer(String Msg1, String Msg2, boolean player) {
        if (player) OutMsg(User1.getSocketUser(), Msg1);
        else OutMsg(User2.getSocketUser(), Msg2);
    }

    @Override
    public void run() {
        while (true) {

            User1Msg = null;
            User2Msg = null;

            if (User1 != null) {
                if (User1.ListMsg.size() != 0) {
                    User1Msg = User1.ListMsg.get(0);
                    User1.ListMsg.remove(0);
                    if (User1Msg.equals(String.valueOf(-1)) && User2 != null) OutMsg(User2.getSocketUser(), "player1 out");
                }
                if (!User1.online) {
                    User1 = null;
                }
            }

            if (User2 != null) {
                if (User2.ListMsg.size() != 0) {
                    User2Msg = User2.ListMsg.get(0);
                    User2.ListMsg.remove(0);
                    if (User2Msg.equals(String.valueOf(-1)) && User1 != null) OutMsg(User1.getSocketUser(), "player2 out");
                }
                if (!User2.online) User2 = null;
            }

            if (User1 == null || User2 == null) {
                if (User1 != null) OutMsg(User1.getSocketUser(), "Wait connect 2 player");
                if (User2 != null) OutMsg(User2.getSocketUser(), "Wait connect 2 player");
                NumberMatches = 37;
                PlayerTurn = true;
            } else {

                if ((User1Msg != null || User2Msg != null)) SentMessage = false;
                if (!SentMessage) {
                    SentMessage = true;

                    if ((User1Msg == null && PlayerTurn) || (User2Msg == null && !PlayerTurn)) {
                        SwitchPlayer("Your turn!", "Your turn!", PlayerTurn);
                        SwitchPlayer("Wait step player2", "Wait step player1", !PlayerTurn);
                    } else {
                        SentMessage = false;
                        String msg = "";
                        if (PlayerTurn) msg = User1Msg;
                        else msg = User2Msg;

                        switch (msg) {
                            case "0", "1", "2", "3", "4", "5" -> {
                                NumberMatches -= Integer.parseInt(msg);
                                SwitchPlayer("player2: pulled out " + msg + " matches.\n" +
                                        NumberMatches + " matches left", "player1: pulled out " + msg + " matches\n" +
                                        NumberMatches + " matches left", !PlayerTurn);

                                if (NumberMatches <= 0) {
                                    try {
                                        t.sleep(1000);
                                    } catch (Exception ignored) {
                                    }
                                    SwitchPlayer("Player2 WON!\n You lose...\n Start new game!", "Player1 WON!\n You lose...\n Start new game!", !PlayerTurn);
                                    try {
                                        t.sleep(1000);
                                    } catch (Exception ignored) {
                                    }
                                    SwitchPlayer("Player2 LOSE!\n You WIN...\n Start new game!", "Player1 LOSE!\n You WIN...\n Start new game!", PlayerTurn);
                                    NumberMatches = 37;
                                    PlayerTurn = true;
                                } else PlayerTurn = !PlayerTurn;

                            }
                            default -> {
                                if (PlayerTurn) System.out.println("Unknown player1 msg:" + msg);
                                else System.out.println("Unknown player2 msg:" + msg);

                            }
                        }
                    }
                }
            }

            try {
                t.sleep(1000);
            } catch (Exception ignored) {
            }

        }
    }
}




class ThreadUser implements Runnable {

    private final Thread t;
    private final Socket SocketUser;
    private InputStream in = null;
    private OutputStream out = null;
    public boolean online = true;
    public ArrayList<String> ListMsg = new ArrayList<>();


    ThreadUser(String Name, Socket User) {
        this.SocketUser = User;
        t = new Thread(this, Name);
        if (Connection()) t.start();

    }


    public Socket getSocketUser() {
        return SocketUser;
    }

    private boolean Connection() {
        try {
            in = SocketUser.getInputStream();
            out = SocketUser.getOutputStream();
            System.out.println("Connection on...");
            return true;
        } catch (Exception e) {
            System.out.println("Error to connection...");
            return false;
        }
    }

    @Override
    public void run() {
        while (true) {

            System.out.println(t.getName() + " Wait...");

            try {
                byte[] bts = new byte[in.available()];
                in.read(bts);
                ByteArrayInputStream bis = new ByteArrayInputStream(bts);
                ObjectInputStream ois = new ObjectInputStream(bis);
                String Msg = (String) ois.readObject();
                System.out.println(Msg);
                ListMsg.add(Msg);
                if (Msg.equals(String.valueOf(-1))) break;

            } catch (Exception e) {
                if (e.getMessage() == null)
                System.out.println(t.getName() + ": Not Msg");
                else {
                    System.out.println(e.getMessage());
                    break;
                }
            }

            try {
                t.sleep(1000);
            } catch (Exception ignored) {
            }
        }
        online = false;
    }
}




public class Server {

    public static void ServerMsg(Socket user, String Msg){
        try {
            InputStream in = user.getInputStream();
            OutputStream out = user.getOutputStream();
            System.out.println(user);
            System.out.println("Server msg: " + Msg);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(Msg);
            out.write(bos.toByteArray());
            oos.close();
            bos.close();
        } catch (Exception e){
            System.out.println("Error msg to connection...");
        }
    }

    public static void main(String[] args) {

        ThreadUser User1 = null;
        ThreadUser User2 = null;
        Game game = new Game("Game");

        try {
            ServerSocket ss = new ServerSocket(1111);
            Socket s;

            while (true) {
                System.out.println("Waiting connection...");
                s = ss.accept();

                if (User1 == null || !User1.online){
                    User1 = new ThreadUser("User1", s);
                    game.setUser1(User1);
                }
                else if (User2 == null || !User2.online){
                    User2 = new ThreadUser("User2", s);
                    game.setUser2(User2);
                }
                else ServerMsg(s,"No vacancies please wait when it's your turn.");

                System.out.println("Local port: " + s.getLocalPort());
                System.out.println("Remote port: " + s.getPort());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}

