import java.net.*;
import java.util.*;
import java.io.*;

/**
 * chat room server for lab 2, cs 60
 * clients connect on port 7777 and give a username, then chat with other clients
 */

public class Server {
	private ServerSocket listen; /* a socket that listens for connections and then adds them to the client handler array */
	private ArrayList<ClientHandler> clients; /* an array of client handlers (threads) that let the server communicate with multiple clients  */
	Scanner read;
	OutputStream out;

	public Server(ServerSocket listen) {
		this.listen = listen;
		clients = new ArrayList<ClientHandler>();
	}

	/**
	 * The usual loop of accepting connections and firing off new threads to handle them
	 */
	public void getConnections() throws IOException {
		while (true) {
			ClientHandler handler = new ClientHandler(listen.accept(), this);
			handler.setDaemon(true);
			handler.start();
			addHandler(handler);
		}
	}

	/**
	 * Adds the handler to the list of current client handlers
	 */
	public synchronized void addHandler(ClientHandler client) {
		clients.add(client);
	}

	/**
	 * Removes the handler from the list of current client handlers
	 */
	public synchronized void removeHandler(ClientHandler handler) {
		clients.remove(handler);
	}

	/**
	 * Makes sure that everyone in the room gets the message, except the sender for a second time
	 */
	public synchronized void broadcast(ClientHandler from, String chat) throws IOException {
		for (ClientHandler c : clients) {
			if (c != from) {
				c.out.write(chat.getBytes());
			}
		}
	}

	/**
	 * Handles communication between a server and one client
	 */
	public class ClientHandler extends Thread {
		private Socket sock;
		private Server server;
		private OutputStream out;
		InputStream in;
		Scanner read;

		public ClientHandler(Socket sock, Server server) {
			super("ClientHandler");
			this.sock = sock;
			this.server = server;
		}

		public void run() {
			String line, name;
			try {
				System.out.println("New user in the chat");

				/* communication channel */
				out  = sock.getOutputStream();
				in = sock.getInputStream();
				read = new Scanner(System.in);

				/* get username */
				out.write("Enter a username:".getBytes());
				byte[] inputBuffer = new byte[500];
				in.read(inputBuffer);
				name = new String(inputBuffer).trim();

				System.out.println("It's " + name);

				String message = "Welcome " + name + "!";
				out.write(message.getBytes());

				server.broadcast(this, name + " entered the room");

				byte[] buffer = new byte[500];
				in.read(buffer);
				line = new String(buffer).trim();
				/* The chat */
				while (line != null) {
					String chat = name + ": " + line;
					if(chat.equals(name + ": quit")) {
						System.out.println(name + " hung up.");
						server.broadcast(this, name + " has left the room.");

					}
					else {
						/* prints the chat locally */
						System.out.println(chat);
						/* broadcasts the chat to everyone else in the chat */
						server.broadcast(this, chat);
						buffer = new byte[500];
						in.read(buffer);
						line = new String(buffer).trim();
					}
				}

				/* when the user leaves... */
				System.out.println(name + " hung up.");
				server.broadcast(this, name + " has left the room.");

				/* removes the user that hangs up from the server, we remove the handler that was being
				 * used to accept messages from it */
				server.removeHandler(this);
				/* we then close the output, the input, and that socket */
				out.close();
				in.close();
				sock.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Waiting for connections...");
		new Server(new ServerSocket(7777)).getConnections();
	}
}