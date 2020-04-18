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
	public synchronized void broadcast(ClientHandler from, String chat) {
		for (ClientHandler c : clients) {
			if (c != from) {
				c.out.println(chat);
			}
		}
	}

	/**
	 * Handles communication between a server and one client
	 */
	public class ClientHandler extends Thread {
		private Socket sock;
		private Server server;
		private PrintWriter out;

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
				out = new PrintWriter(sock.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

				/* get username */
				out.println("Enter a username:");
				name = in.readLine();
				System.out.println("It's " + name);
				out.println("Welcome " + name + "!");
				server.broadcast(this, name + " entered the room");

				/* The chat */
				while ((line = in.readLine()) != null) {
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

		/*terminates the program if the server wants to quit
		Scanner read = new Scanner(System.in);
		if (read.nextLine().equals("quit")) {
			System.exit(0);
		}*/
	}
}