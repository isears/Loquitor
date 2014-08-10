import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Defines the thread that will be run for each client establishing a connection with the server.
 */
public class ClientController implements Runnable{
    static char[] ILLEGAL_CHARS = {'/', '\\'};
    static int USERNAME_MAX_LENGTH = 15;
    static String WELCOME_MESSAGE = "<<Welcome to the chat server! Type >>help for a list of commands.>>";

    Socket conn;
    String clientData;
    ServerState state;
    String userName;
    String MSG_connectionAccepted;
    String MSG_duplicateUName;
    String MSG_longUName;
    String MSG_illegalUName;

    /**
     * Constructor for ClientController
     *
     * @param connection a connection to a single client
     * @param s a reference to the global ServerState object
     */
    public ClientController(Socket connection, ServerState s){
        conn = connection;
        state = s;

        getProtocol();
    }

    /**
     * The thread method of the runnable ClientController class. In this method, the initial protocol handshake is
     * performed, and then the thread simply waits for client inputs. Normal client inputs are broadcasted to all
     * active clients, but command inputs (inputs from the client prefixed with '>>') are handled separately by the
     * ServerCommands methods
     *
     * @see ServerCommands
     */
    public void run(){
        System.out.println("New Client Thread Running");
        boolean duplicateUserName = false;

        try{
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(conn.getOutputStream());
            userName = inFromClient.readLine();
            System.out.println("Got user name: " + userName);
            String[] activeUsers = state.getActiveUsers();

            for(String user : activeUsers){
                if(user != null && user.equals(userName)){
                    duplicateUserName = true;
                    break;
                }
            }

            if(duplicateUserName){
                System.out.println("Closing connection due to duplicate username");
                outToClient.writeBytes(MSG_duplicateUName + '\n');
                conn.close();
            }else if(userName.length() > USERNAME_MAX_LENGTH){
                System.out.println("Closing connection due to long username.");
                outToClient.writeBytes(MSG_longUName + '\n');
                conn.close();
            }else if(containsIllegalChars(userName)) {
                System.out.println("Closing connection due to illegal username.");
                outToClient.writeBytes(MSG_illegalUName + '\n');
                conn.close();
            }else{
                state.appendClient(conn, userName);
                System.out.println("Successful connection from " + userName);
                outToClient.writeBytes(MSG_connectionAccepted + '\n');
                broadCast("<<" + userName + " connected" + ">>");


                outToClient.writeBytes(WELCOME_MESSAGE +'\n');

                while((clientData = inFromClient.readLine()) != null){
                    System.out.println(userName + " sent: " + clientData);
                    broadCast(userName + ": " + clientData);

                    if(clientData.substring(0, 2).equals(">>")){
                        String commandReturn = ServerCommands.handle(clientData.substring(2), state, userName);
                        if(commandReturn != null){
                            outToClient.writeBytes(commandReturn + '\n');

                            if(clientData.substring(2).equals("quit")){
                                conn.close();
                                state.removeUser(userName);
                                return;
                            }

                        }else{
                            outToClient.writeBytes("<<unrecognized command: " + clientData.substring(2) + ">>" + '\n');
                        }
                    }
                }
            }

        }catch(SocketException e){
            System.out.println("Closing " + userName + "'s connection");
            state.removeUser(userName);
            broadCast("<<" + userName + " disconnected" + ">>");

        }catch(IOException e){
            System.out.println("IOException");
            state.removeUser(userName);
            broadCast("<<" + userName + " disconnected" + ">>");

        }
    }

    /**
     * Data sent through this method is sent to all active client connections
     *
     * @param data the message to be sent
     */
    private void broadCast(String data){
        DataOutputStream[] activeOutStreams = state.getActiveOutputStreams();

        for(DataOutputStream outStream : activeOutStreams){
            try{
                outStream.writeBytes(data + '\n');
            }catch(IOException e){
                System.out.println("Detected dead connection," +
                        " this could be indicative of an error in connection tear down ops");
            }

        }
    }

    /**
     * A helper function to determine if a string contains characters that would make it an illegal username
     *
     * @param s the string that is to be tested
     * @return 'true' if the string is an invalid username, false otherwise
     */
    private boolean containsIllegalChars(String s){
        boolean ret = false;
        for(int i = 0; i < s.length(); i++){
            for(char illegalChar : ILLEGAL_CHARS){
                if(s.charAt(i) == illegalChar){
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * Gets the protocol specifications from the Protocol.xml file. The Protocol.xml file used by the server must be
     * identical to protocol files used by the client. This method should be called in the constructor.
     *
     */
    private void getProtocol(){
        try{
            File f = new File("Protocol.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            doc.getDocumentElement().normalize();

            MSG_connectionAccepted = doc.getElementsByTagName("connectionSuccess").item(0).getTextContent();
            MSG_duplicateUName = doc.getElementsByTagName("duplicateUserName").item(0).getTextContent();
            MSG_longUName = doc.getElementsByTagName("longUserName").item(0).getTextContent();
            MSG_illegalUName = doc.getElementsByTagName("illegalUserName").item(0).getTextContent();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
