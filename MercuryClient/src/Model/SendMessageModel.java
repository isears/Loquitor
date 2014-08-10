package Model;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Model class for sending a message to the server
 */
public class SendMessageModel implements Runnable {
    DataOutputStream outputStream;
    String message;

    /**
     * Constructor for SendMessageModel
     *
     * @param outToServer an output stream to the server
     * @param msg the message to be sent
     */
    public SendMessageModel(DataOutputStream outToServer, String msg){
        outputStream = outToServer;
        message = msg;
    }

    @Override
    /**
     * The thread method of the runnable SendMessageModel. Takes the message specified in the constructor and puts it
     * into the output stream. Note that the SendMessageModel (as opposed to the ReceiveMessageModel) is meant to be
     * re-instantiated every time the client sends a new message.
     */
    public void run(){
        try{
            outputStream.writeBytes(message + '\n');
        }catch(IOException e){
            System.out.println("Client tried to send message to unreachable server");
        }

    }
}
