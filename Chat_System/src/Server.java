import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Server {

    private SimpleDateFormat displayTime;
    /** Keep a list of clients' username, and we use set because the username in this program cannot be duplicated */
    private Set<String> usernames;
    /** Keep all clients' thread*/
    private ArrayList<ServerThread> listOfClients;
    private int port_number;
    private ServerSocket serverSocket;

    /**
     * A constructor of a Server class
     * @param  port_number  port number for a TCP connection
     */
    public Server(int port_number){
        this.displayTime = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        this.usernames = new HashSet<>();
        this.listOfClients = new ArrayList<>();
        this.port_number = port_number;
    }

    /**
     * Bind a server socket, so server is listening for an incoming client connection
     */
    public void connect_serverSocket(){
        try {
            while(true) {
                serverSocket = new ServerSocket(port_number);
                System.out.println(printTime () +"Chat Server started");
                System.out.println(printTime () +"Waiting for a client at " + port_number + "...");
                accept_client_socket ();
            }
        } catch (IOException e) {
            System.err.println(printTime () +e.getMessage());
        }
    }

    /**
     * Server is accepting an incoming request from clients, and after the connection is establish, create each Thread to handle each client
     */
    public void accept_client_socket(){
        try {
            while (true) {
                Socket s = serverSocket.accept ();
                System.out.println("\t" + printTime () + "New client connected...");
                // After the server accepts a request from a client then creating a new Thread to handle each client
                ServerThread new_client = new ServerThread(s, this);
                listOfClients.add(new_client);
                new_client.start();
            }
        } catch (IOException e) {
            System.err.println(printTime ()+"Error occurring in the server chat: "+e.getMessage());
        }
    }

    /**
     * Broadcast a message of a client to all connected users who are currently active in a chat session
     * @param  message a message from a client
     * @param current_user a client Thread of a client whose message will be broadcast
     */
    public void broadcast(String message, ServerThread current_user) {
        for (ServerThread each_user : listOfClients) {
            if (each_user != current_user) {
                each_user.send_message(message);
            }
        }
    }

    /**
     * Add username to the list of usernames of all connected clients
     * @param  username  a username of a new connected client.
     */
    public void add_username(String username) {
        usernames.add(username);
    }

    /**
     * Remove a user from a chat session
     * @param  username a username of connected client in a chat session
     * @param  user a ServerThread that handles a client who will be removed from a chat session.
     */
    public void remove_disconnected_user(String username, ServerThread user) {
        boolean removed = usernames.remove(username);
        if (removed) {
            listOfClients.remove(user);
            System.out.println("\t" + printTime ()+ "The user " + username + " has left the chat...");
        }
    }

    /**
     * Accessor of a list of usernames in a Server class
     * @return usernames of a Server class
     */
    public Set<String> getUsernames() {
        return this.usernames;
    }

    /**
     * Check if there are any other users connected in a chat session, not including the currently connected user
     * @return if a 'usernames' is not empty then there is at least one client connected
     */
     boolean is_chat_empty() {
        return !this.usernames.isEmpty();
    }

    /**
     * Display the current date and time
     * @return s the string of current date and time
     */
    public String printTime() {
        String time = displayTime.format(new Date());
        String s = time +": ";
        return s;
    }

    /** A UserThread is created to help the server chat handle with multiple clients, so
     * the server can connect to the clients more than one TCP socket connections.*/
    class ServerThread extends Thread {
        private Socket s;
        private Server server;
        private PrintWriter write;

        /**
         * A constructor of a ServerThread
         * @param  s a TCP socket connection
         * @param  server an object of a Server class
         */
        public ServerThread(Socket s, Server server){
            this.s = s;
            this.server = server;
        }

        /**
         * Perform a communication via TCP connection between a server and clients
         */
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                write = new PrintWriter(s.getOutputStream(), true);
                display_connected_users();
                String username = reader.readLine();
                server.add_username(username);
                String server_message = "\t" + printTime () +"'"  + username + "' just joined the chat session!";
                server.broadcast(server_message, this);
                String client_message;
                do {
                    client_message = reader.readLine();
                    server_message = "[" + username + "]: " + client_message;
                    server.broadcast(server_message, this);
                } while (!client_message.equalsIgnoreCase ("exit"));
                server.remove_disconnected_user(username, this);
                s.close();
                server_message = "\t" + printTime () + username + " has left the chat session.";
                server.broadcast(server_message, this);
            } catch (IOException ex) {
                printTime ();
                System.err.println(printTime ()+"Error in UserThread: " + ex.getMessage());
            }
        }

        /**
         * Display connected users who are currently in a chat session, not including the currently connected user
         */
        public void display_connected_users() {
            if (server.is_chat_empty ()) {
                write.println(printTime () +"Connected users: " + server.getUsernames());
            } else {
                write.println(printTime () +"Nobody is in the chat room yet...");
            }
        }

        /**
         * Server sends a message to each client
         * @param message a message to be sent to via socket connection from server to clients
         */
        public void send_message(String message) {
            write.println(message);
        }
    }
}