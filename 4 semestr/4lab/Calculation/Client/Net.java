import UserData.UserData;
import java.net.*;
import java.io.*;
import java.util.Scanner;

class T implements Runnable {

	private final Thread t;
	private final Socket socket;
	private InputStream in = null;
	private OutputStream out = null;
	private UserData User = new UserData();
	T(String Name, Socket User){
		this.socket = User;
		t = new Thread(this, Name);
		if (Connection()) t.start();
	}

	private boolean Connection(){
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			System.out.println("Connection on...");
			return true;
		} catch (Exception e){
			System.out.println("Error to connection...");
			return false;
		}
	}

	@Override
	public void run() {

		Scanner scan = new Scanner(System.in);

		while (true) {
			try {

				System.out.println("Enter 'Stop' if you finished calculating or enter any key: ");
				String Enter = scan.next();

				if (Enter.equals("Stop")) {
					User.setUserName(null);
				} else {
					double A = 0, B = 0;
					System.out.println("Enter to A values: ");
					A = scan.nextDouble();
					System.out.println("Enter to B values: ");
					B = scan.nextDouble();
					System.out.println("Enter to operation: ");
					String operation = scan.next();
					System.out.println(A + " | " + B + " | " + operation);
					User.setValue(A, B);
					User.setOperation(operation);
					User.setUserName("User1");
				}


				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);

				Object obj = User;
				oos.writeObject(obj);
				oos.flush();

				byte[] bts = bos.toByteArray();
				System.out.println("Count out: " + bts.length);
				out.write(bts);

				if (Enter.equals("Stop")) break;

				bts = new byte[2000];
				int count = in.read(bts);
				System.out.println("Count in: " + count);

				ByteArrayInputStream bis = new ByteArrayInputStream(bts);
				ObjectInputStream ois = new ObjectInputStream(bis);
				obj = ois.readObject();
				UserData User = (UserData) obj;
				System.out.println("Result: " + User.getResult());


			} catch (Exception e) {
				System.out.println("Not server data...");
				System.out.println(e.getMessage());
			}
			try {
				t.sleep(1000);
			} catch (Exception ignored) {
			}
		}

		try {
			socket.close();
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}

class Net
{
	public static void main(String args[])
	{
		try{
			Socket s = new Socket("127.0.0.1",1111);
			System.out.println("Local port: " +  s.getLocalPort());
			System.out.println("Remote port: " + s.getPort());

			new T("Server",s);


		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}

