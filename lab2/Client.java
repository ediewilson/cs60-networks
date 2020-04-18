import java.io.*;
import java.net.*;
import java.util.*;


public class Client {
	private Scanner read;
	private Socket sock;
	private PrintWriter out;
	private BufferedReader in;
	private boolean gone = false;

	public Client() throws IOException {
		read = new Scanner(System.in);

		/* opens a socket, writer, and reader */
		System.out.println("Entering the chat room...");
		sock = new Socket("localhost", 7777);
		out = new PrintWriter(sock.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		System.out.println("... entered");

		// Greeting; name request and response
		String line = in.readLine();
		System.out.println(line);
		String name = read.nextLine();
		out.println(name);

		// Fire off a new thread to handle incoming messages from server
		ServerHandler incoming = new ServerHandler(in);
		incoming.setDaemon(true);
		incoming.start();
	}

	/**
	 * Get console input and send it to server;
	 * stop & clean up when server has hung up (noted by hungup)
	 */
	public void handleUser() throws IOException {
		while (!gone) {
			out.println(read.nextLine());
			if (read.nextLine().equals("quit")) {
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
		private BufferedReader in;

		private ServerHandler(BufferedReader in) {
			this.in = in;
		}

		public void run() {
			String line;
			try {
				while ((line = in.readLine()) != null) {
					System.out.println(line);
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