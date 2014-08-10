package Model;

import Controller.ConnectGUIController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Model class for the connection phase of the application. Attempts to establish connections to the server
 * based on a protocol specified in Protocol.xml.
 */
public class ConnectionModel extends Task<Void>{
    String serverAddress;
    String userName;
    int portNumber;
    boolean connectionInProgress;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    Label status;
    ConnectGUIController parentController;
    String MSG_connectionAccepted;
    String MSG_duplicateUName;
    String MSG_longUName;
    String MSG_illegalUName;

    /**
     * Constructor for the ConnectionModel
     *
     * @param addr IP address of the server
     * @param uName Desired username
     * @param statusLabel A pointer to the status label on the connectGUI
     * @param parent A pointer to the controller that called this model
     * @param port The port number of the server application
     */
    public ConnectionModel(
            String addr,
            String uName,
            Label statusLabel,
            ConnectGUIController parent,
            int port
    ) {
        serverAddress = addr;
        userName = uName;
        status = statusLabel;
        connectionInProgress = true;
        parentController = parent;
        portNumber = port;

        getProtocol();
    }


    @Override
    /**
     * Thread method that attempts a connection to the server. The connection will be attempted with the protocol
     * specified in Protocol.xml. This xml file must be identical to the Protocol.xml file used by the server.
     * After the connection attempt is complete, the status label in the ConnectGUI will be updated with an appropriate
     * message. If the connection is successful, the ConnectGUIController will handle tear-down operations of the
     * ConnectGUI and setup operations of the MainGUI.
     *
     * @see Controller.ConnectGUIController
     * @see GUI.ConnectGUI
     */
    public Void call(){
        System.out.println("Connect called with " + serverAddress + " " + userName);
        updateStatus("Attempting to connect...");

        try{
            Socket clientSocket = new Socket(serverAddress, portNumber);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outToServer.writeBytes(userName + '\n');
            String initialReturn = inFromServer.readLine();

            if(initialReturn.equalsIgnoreCase(MSG_connectionAccepted)){
                updateStatus("Connected successfully!");
                connectionInProgress = false;

                Platform.runLater(new Runnable(){
                    @Override
                    public void run(){
                        parentController.initiateChatMain();
                    }


                });

            }else if(initialReturn.equalsIgnoreCase(MSG_duplicateUName)){
                updateStatus("Error: Duplicate username.");
                connectionInProgress = false;
            }else if(initialReturn.equalsIgnoreCase(MSG_longUName)){
                updateStatus("Error: server cannot accept a username that long.");
                connectionInProgress = false;
            }else if(initialReturn.equalsIgnoreCase(MSG_illegalUName)){
                updateStatus("Error: username contains characters that the server cannot accept.");
                connectionInProgress = false;
            }else{
                updateStatus("Error: unknown error, check protocol settings.");
                connectionInProgress = false;
            }

        }catch(Throwable e){
            System.out.println("caught exception");
            //e.printStackTrace();
            updateStatus("Error: Server unreachable.");
            connectionInProgress = false;
        }
        return null;
    }

    /**
     * Getter method for the server address.
     *
     * @return server address used in the connection attempt
     */
    public String getServerAddress(){
        return serverAddress;
    }

    /**
     * Getter method for the output stream to the server. Should only be used after a successful connection attempt.
     *
     * @return output stream to server established in the connection attempt.
     */
    public DataOutputStream getOutputStream(){
        return outToServer;
    }

    /**
     * Getter method for the input stream from the server. Should only be used after a successful connection attempt.
     *
     * @return input stream from the server established in the connection attempt.
     */
    public BufferedReader getInputStream(){
        return inFromServer;
    }

    /**
     * Getter method for the connectionInProgress property. Allows the controller to determine whether a connection
     * attempt is in progress.
     *
     * @return an indication of whether or not the ConnectionModel is actively attempting to make a connection.
     */
    public boolean connectionInProgress(){
        return connectionInProgress;
    }

    /**
     * Method called to update the status label in the ConnectGUI. The status can either indicate that a connection is
     * in progress, that the connection has encountered an error (e.g., bad username, bad address, etc.), or that the
     * connection has completed successfully
     *
     * @param newStatus the status of the connection
     */
    private void updateStatus(final String newStatus){
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                status.setText(newStatus);
            }

        });
    }

    /**
     * Method that reads the Protocol.xml file to get protocol messages used to connect to a server. In order for the
     * application to function, the Protocol.xml file on the server-side and the Protocol.xml file on the client-side
     * must be identical.
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
