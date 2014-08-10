import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Defines a data structure that holds information relevant to a single client connection. This information includes the
 * connection socket used by the client, the client's username, and a reference to the output stream to the client.
 */
public class ConnectionBundle {
    Socket connection;
    String userName;
    DataOutputStream outputStream;

    /**
     * ConnectionBundle constructor
     *
     * @param conn the client's connection socket
     * @param uName the client's username
     * @param oStream an output stream to the client
     */
    public ConnectionBundle(Socket conn, String uName, DataOutputStream oStream){
        connection = conn;
        userName = uName;
        outputStream = oStream;
    }

    /**
     * Getter method for the output stream to the client
     *
     * @return output stream to the client
     */
    public DataOutputStream getOutputStream(){
        return outputStream;
    }

    /**
     * Getter method for the client's username
     *
     * @return the client's username
     */
    public String getUserName(){
        return userName;
    }

}
