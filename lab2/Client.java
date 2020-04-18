import java.io.*;
import java.util.*;
import java.net.Socket;


public class Client {
	private Scanner read;
	private Socket sock;
	private OutputStream out;
	private InputStream in;
	private boolean gone = false;

	private Client() throws IOException {
		read = new Scanner(System.in);

		/* opens a socket, writer, and reader */
		System.out.println("Entering the chat room...");
		sock = new Socket("localhost", 7777);
		out = sock.getOutputStream();
		in = sock.getInputStream();
		System.out.println("... entered");

		// Greeting; name request and response
		byte[] inputBuffer = new byte[500];
		in.read(inputBuffer);
		System.out.println(new String(inputBuffer).trim());
		String name = read.nextLine().trim();
		out.write(name.getBytes());

		// Fire off a new thread to handle incoming messages from server
		ServerHandler incoming = new ServerHandler(in);
		incoming.setDaemon(true);
		incoming.start();
	}

	/**
	 * Get console input and send it to server;
	 * stop & clean up when server has hung up (noted by hungup)
	 */
	private void handleUser() throws IOException {
		while (!gone) {
			String inputLine = read.nextLine().trim();
			out.write(inputLine.getBytes());
			if (inputLine.equals("quit")) {
				System.exit(0);
			}
		}

		// Clean up
		out.close();
		in.close();
		sock.close();
	}

	/**
	 * Handles communication from the server (via "in"), printing to System.out
	 */
	private class ServerHandler extends Thread {
		private InputStream in;

		private ServerHandler(InputStream in) {
			this.in = in;
		}

		public void run() {
			try {
				byte[] bytes = new byte[500];
				while (in.read(bytes) != -1) {
					System.out.println(new String(bytes).trim());
					bytes = new byte[500];
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				gone = true;
				System.out.println("Server hung up");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Client chat = new Client();
		chat.handleUser();
	}
}
