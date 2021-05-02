
import UserData.UserData;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

class UserOnlineCheck implements Runnable {
	private final Thread t;
	ArrayList<ThreadUser> UserList;
	UserOnlineCheck(String Name){
		this.UserList = new ArrayList<>();
		t = new Thread(this, Name);
		t.start();
	}

	public void add(Socket user){
		UserList.add(new ThreadUser("User" + (UserList.size()+1), user));
	}

	@Override
	public void run(){
		while (true) {
			UserList.removeIf(T -> !T.online);
			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class ThreadUser implements Runnable {

	private final Thread t;
	private final Socket SocketUser;
	private InputStream in = null;
	private OutputStream out = null;
	private UserData User = null;
	public boolean online = true;
	ThreadUser(String Name, Socket User){
		this.SocketUser = User;
		t = new Thread(this, Name);
		if (Connection()) t.start();
	}

	public String Calculating(String operation, double a, double b){
		switch (operation) {
			case ("+") -> {
				return String.valueOf(a + b);
			}
			case ("-") -> {
				return String.valueOf(a - b);
			}
			case ("*") -> {
				return String.valueOf(a * b);
			}
			case ("/") -> {
				if (b == 0) return "Error: divining to 0";
				return String.valueOf(a / b);
			}
			case ("^") -> {
				return String.valueOf(Math.pow(a, b));
			}
			default -> {
				return "There is no such operation";
			}
		}
	}

	private boolean Connection(){
		try {
			in = SocketUser.getInputStream();
			out = SocketUser.getOutputStream();
			System.out.println("Connection on...");
			return true;
		} catch (Exception e){
			System.out.println("Error to connection...");
			return false;
		}
	}

	@Override
	public void run() {
		while (true) {
			System.out.println(t.getName() + " Wait...");

			try {
				byte[] bts = new byte[2000];
				int count = in.read(bts);
				System.out.println("Count: " + count);

				ByteArrayInputStream bis = new ByteArrayInputStream(bts);
				ObjectInputStream ois = new ObjectInputStream(bis);

				Object obj = ois.readObject();
				User = (UserData) obj;
				if (User.getUserName() == null) break;
				System.out.println("Access: " + User.getUserName());
				System.out.println(User.getA() + " " + User.getOperation() + " " + User.getB());
				User.setResult(Calculating(User.getOperation(), User.getA(), User.getB()));
				System.out.println(User.getResult());


				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				obj = User;
				oos.writeObject(obj);
				oos.flush();

				bts = bos.toByteArray();
				System.out.println(bts.length);
				out.write(bts);

				oos.close();
				ois.close();
				bos.close();
				bis.close();

			} catch (Exception e) {
				System.out.println("Not data...");
				System.out.println(e.getMessage());
				if (e.getMessage().equals("Connection reset")) break;
				try {
					t.sleep(1000);
				} catch (Exception ignored) {
				}
			}

		}
		online = false;
	}
}

class Net
{
	public static void main(String args[]) {
		UserOnlineCheck UserCheck = new UserOnlineCheck("UserList");
		try {
			ServerSocket ss = new ServerSocket(1111);
			Socket s;

			while (true) {
				System.out.println("Waiting connection...");
				s = ss.accept();

				UserCheck.add(s);
				System.out.println("Local port: " + s.getLocalPort());
				System.out.println("Remote port: " + s.getPort());
			}

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
}



