import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

/**
 * The global server state object
 */
public class ServerState {
    Vector<ConnectionBundle> connectionList;
    int clientCount;

    /**
     * ServerState constructor
     *
     * @param maxConnections the maximum number of allowable connections on the server
     */
    public ServerState(int maxConnections){
        connectionList = new Vector<ConnectionBundle>(maxConnections);
        clientCount = 0;
    }

    /**
     * Add a client to the server state object's internal list of clients
     *
     * @param conn client connection socket
     * @param uName client's username
     * @throws IOException
     */
    public void appendClient(Socket conn, String uName) throws IOException {
        connectionList.add(
                new ConnectionBundle(conn, uName, new DataOutputStream(conn.getOutputStream()))
        );

        clientCount++;
    }

    /**
     * Get all output streams to all clients
     *
     * @return a list of output streams to clients
     */
    public DataOutputStream[] getActiveOutputStreams(){
        DataOutputStream[] ret = new DataOutputStream[connectionList.size()];

        for(int i = 0; i < connectionList.size(); i++){
            ret[i] = connectionList.elementAt(i).getOutputStream();
        }

        return ret;
    }

    /**
     * Get all client user names
     *
     * @return a list of user names
     */
    public String[] getActiveUsers(){
        String[] ret = new String[connectionList.size()];

        for(int i = 0; i < connectionList.size(); i++){
            ret[i] = connectionList.elementAt(i).getUserName();
        }

        return ret;
    }

    /**
     * Remove a client from the server state object's internal list of clients
     *
     * @param userName the username of the client to be removed
     */
    public void removeUser(String userName){
        for(int i = 0; i< connectionList.size(); i++){
            if(connectionList.elementAt(i).getUserName().equals(userName)){
                connectionList.remove(i);
            }
        }
    }
}
