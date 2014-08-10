import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Entry point for chat server application.
 *
 * 1.) Reads server port number and maximum allowable connections from ServerParams.xml
 * 2.) Creates a welcome socket
 * 3.) Instantiates the server state object
 * 4.) Begins waiting for connections
 * 5.) Begins a new client thread for every connection received on the welcome socket
 *
 * @see ClientController
 * @see ServerState
 */
public class Main {
    static int PORT_NUM;
    static int MAX_CONNECTIONS;

    public static void main(String[] args) throws Exception{
        //(1) Read ServerParams.xml
        getServerParams();

        //(2) Create welcome socket
        ServerSocket welcomeSocket = new ServerSocket(PORT_NUM);
        //(3) Instantiate server state object
        ServerState state = new ServerState(MAX_CONNECTIONS);
        System.out.println("Server ready for connection");

        //(4) Connection wait loop
        while(true){
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("New client attempting connection...");

            //(5) Begin client thread
            Runnable clientThread = new ClientController(connectionSocket, state);
            new Thread(clientThread).start();

        }
    }

    /**
     * Opens the file ServerParams.xml and reads data into PORT_NUM and MAX_CONNECTIONS variables.
     * This allows the server to open a welcome socket on the correct port and instantiate the server
     * state with the correct value for maximum allowable connections.
     *
     */
    private static void getServerParams(){
        try{
            File f = new File("ServerParams.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            doc.getDocumentElement().normalize();

            PORT_NUM = Integer.parseInt(doc.getElementsByTagName("portNumber").item(0).getTextContent());
            MAX_CONNECTIONS = Integer.parseInt(doc.getElementsByTagName("maxConnections").item(0).getTextContent());

        }catch(Exception e){
            System.out.println("Warning: ServerParams.xml missing or damaged, using default params");
        }
    }
}
