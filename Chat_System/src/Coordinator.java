import java.net.*;
import java.io.*;
import java.util.*;

public class Coordinator{

    private HashMap<String,Integer> sessions;

    /** Information to establish UDP connection;
     * A chat coordinator resides at a well-known network address */
    private final int PORT_UDP = 50001;
    private final String ADDRESS_UDP =  "239.255.255.255";  // Class D
    static volatile boolean finished = false;

    /** Information to create a chat session */
    private String server_address;
    private int server_port;
    private int option;

    // Scanner to read the use input
    Scanner keyboard;
    MulticastSocket socket;

    /**
    * A constructor of a Coordinator class
    */
    public Coordinator (){
        this.keyboard = new Scanner(System.in);
        this.sessions = new HashMap<String, Integer> ();
    }

    /**
    * Establish UDP connection with clients first to maintain a chat session directory
    */
    public void connection_UDP_establish_directory(){
        try {
            InetAddress group = InetAddress.getByName(ADDRESS_UDP);
            socket = new MulticastSocket(PORT_UDP);
            socket.setTimeToLive(0);
            socket.joinGroup(group);
            System.out.println("Welcome to the chat system!!! ......");
            System.out.println("Enter the session ID (server's IP address): ");
            server_address = keyboard.next();
            System.out.println("Enter server port to start a new chat session: ");
            server_port = keyboard.nextInt();
            Server server = new Server(server_port);
            Client client = new Client(server_address, server_port);
            // There is one chat server per chat session, so if a server socket binds to a new port; it will create a new chat session
            server.connect_serverSocket ();
            client.connect_Socket ();
        }catch (UnknownHostException e) {
            System.err.println("Unknown Host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
    * Establish clients UDP connection, and a coordinator class will prompt the address and port number of
    * a chat session to create a TCP connection with the chat server
    */
    public void connection_UDP(){
        try {
            InetAddress group = InetAddress.getByName(ADDRESS_UDP);
            socket = new MulticastSocket(PORT_UDP);
            socket.setTimeToLive(0);
            socket.joinGroup(group);
            System.out.println("Welcome to the chat system!!! ...... \nPlease select the option: ");
            do {
                System.out.println("Type 1: To START a chat \nType 2: To JOIN a chat\nType 3: To EXIT the chat system");
                option = keyboard.nextInt();
            } while (option!= 1 && option != 2 && option!=3);
            if(option == 3) {
                System.out.println ("Exiting the chat system... Bye Bye!");
                socket.leaveGroup (group);
                System.exit (0);
            }
            System.out.println("Enter the session ID (server's IP address): ");
            server_address = keyboard.next();
            System.out.println("Enter server port to start a new chat session: ");
            server_port = keyboard.nextInt();
            addSession (server_address,server_port);
            process_option(option, server_port, server_address);
        }catch (UnknownHostException e) {
            System.err.println("Unknown Host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
    * Process the user's option if they want to create or join a chat session, then establish a TCP connection
    */
    public void process_option(int option, int server_port, String server_address) {
        Server server = new Server(server_port);
        Client client = new Client(server_address, server_port);
        if (option == 1 || option == 2) {
                System.out.print ("Option# " + option + " is chosen.\n" + "Chat session ID is: ");
                displaySessions ();
                client.connect_Socket ();
        }
    }

    public HashMap<String, Integer> addSession(String server_address, int server_port){
        sessions.put (server_address,server_port);
        return sessions;
    }

    public HashMap<String, Integer> getSession(){
        return this.sessions;
    }

    public void displaySessions(){
        for(String address: sessions.keySet ()){
            String key = address.toString ();
            String value = sessions.get (address).toString ();
            System.out.println (key+" " + value);
        }
    }

    /** The main class for a coordinator to use UDP for communication with chat clients, set up chat servers for each chat session, and maintain a chat session directory */
    public static void main (String[] args){
        Coordinator c = new Coordinator();
        c.connection_UDP_establish_directory ();
    }
}