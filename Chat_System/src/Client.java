import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {

    private Socket clientSocket;
    private String address;
    private int port;
    private String username;
    private SimpleDateFormat displayTime;

    /**
     * A constructor of a Server class
     * @param port server port number for a TCP connection
     * @param address server IP address for a TCP connection
     */
    public Client(String address, int port) {
        this.displayTime = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        this.port = port;
        this.address = address;
    }

    /**
     * Mutator of a Client class, so that a client can change the username when there is a new connected client
     * @param  username username of a client
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Accessor a Client class
     * @return return username of a connected client
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Connect Client Socket via TCP communication
     */
    public void connect_Socket() {
        // establish a connection
        try {
            clientSocket = new Socket (address, port);
            System.out.println (printTime ()+"Client connected to the chat server");
            new ClientReadThread (clientSocket, this).start ();
            new ClientWriteThread (clientSocket, this).start ();
        } catch (IOException e) {
            System.err.println (printTime ()+e.getMessage ());
        }
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

    /**
     * A Thread to help a client to handle with TCP communication
     */
    class ClientReadThread extends Thread {
        private BufferedReader read;
        private Socket s;
        private Client client;

        /**
         * A constructor of a ClientReadThread class
         * @param  s a TCP socket connection
         * @param client an object of Client Class
         */
        public ClientReadThread(Socket s, Client client) {
            this.s = s;
            this.client = client;
            try {
                read = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                System.err.println(printTime ()+"Error getting Inputstream: " + e.getMessage());
            }
        }

        /**
         * Perform a communication via TCP connection between clients and a server
         */
        public void run() {
            while (true) {
                try {
                    String msg = read.readLine();
                    System.out.println("\n" + msg);
                    // display the username after displaying the server's message
                    if (client.getUsername() != null) {
                        System.out.print("[" + client.getUsername() + "]: ");
                    }
                } catch (IOException e) {
                    System.err.println(printTime ()+"Error reading from server: " + e.getMessage());
                    System.exit (0);
                }
            }
        }
    }

    /**
     * A Thread to help a client to handle with TCP communication
     */
    class ClientWriteThread extends Thread {
        private PrintWriter write;
        private Socket s;
        private Client client;

        /**
         * A constructor of a ClientWriteThread class
         * @param  s a TCP socket connection
         * @param client an object of Client Class
         */
        public ClientWriteThread(Socket s, Client client) {
            this.s = s;
            this.client = client;
            try {
                write = new PrintWriter(s.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println(printTime ()+"Error getting Outputstream: " + e.getMessage());
            }
        }

        /**
         * Perform a communication via TCP connection between clients and a server
         */
        public void run() {
            System.out.println ("\nWelcome to a chat session! \n\nYou can type 'exit' to exit the chat at anytime");
            Console console = System.console();
            String username = console.readLine("\nPlease enter your name: ");
            client.setUsername(username);
            write.println(username);
            String msg;
            do {
                msg = console.readLine("[" + username + "]: ");
                write.println(msg);
            } while (!msg.equalsIgnoreCase ("exit"));
            if(msg.equalsIgnoreCase ("exit")){
                System.out.println (printTime ()+username + " is leaving the chat... Bye bye...");
                System.exit (0);
            }
            try {
                s.close();
            } catch (IOException e) {
                System.err.println(printTime ()+"Error writing to server: " + e.getMessage());
            }
        }
    }

    /** The main class for a coordinator to create or join a new chat session for a client */
    public static void main(String[] args){
        Coordinator c = new Coordinator();
        c.connection_UDP ();
    }
}