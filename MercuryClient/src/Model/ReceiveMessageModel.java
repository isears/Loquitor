package Model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Model class for receiving messages from the server
 */
public class ReceiveMessageModel extends Task<Void> {
    BufferedReader inFromServer;
    TextArea transcript;

    /**
     * Constructor for ReceiveMessageModel
     *
     * @param serverInputStream an input stream from the server
     * @param t a pointer to the text area of the main GUI where the output will be printed
     */
    public ReceiveMessageModel(BufferedReader serverInputStream, TextArea t){
        inFromServer = serverInputStream;
        transcript = t;
    }

    @Override
    /**
     * Thread method of the runnable ReceiveMessageModel. This thread is meant to run continuously while the application
     * is in the main phase (unlike the SendMessageModel runnable, which is meant to be instantiated for each message
     * to be sent). Here, the ReceiveMessageModel listens for input from the server, and updates the GUI's text area
     * whenever information is received.
     */
    public Void call(){
        System.out.println("Listening for server input...");
        String serverData;
        try{
            while((serverData = inFromServer.readLine()) != null){
                updateTranscript(serverData);
            }
        }catch(IOException e){
            updateTranscript("<<Error: lost connection to server, consider restarting client>>");
        }

        return null;
    }

    /**
     * private method to update the main GUI's text area with a new message
     *
     * @param newMessage the message to be appended to the text area
     */
    private void updateTranscript(final String newMessage){
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                System.out.println("Updating outToClient");
                transcript.appendText(newMessage + '\n');
            }

        });
    }
}
