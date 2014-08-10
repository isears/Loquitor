package Controller;

import Model.ReceiveMessageModel;
import Model.SendMessageModel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;

/**
 * Controller for the main chat window defined in GUIMain.fxml. Handles both send button click events and incoming data
 * from the server. Employs the use of SendMessageModel and ReceiveMessageModel classes to handle data from the user and
 * data from the server, respectively.
 *
 * @see Model.SendMessageModel
 * @see Model.ReceiveMessageModel
 */
public class MainGUIController {
    @FXML private TextArea transcript;
    @FXML private TextField sendField;

    DataOutputStream outputStream;
    BufferedReader inputStream;
    ReceiveMessageModel receiveModel;

    /**
     * Method called on send button click event. For every send button click event, a new SendMessageModel object is
     * instantiated and the output stream to the server and the text entered in the send field are passed in the
     * constructor. The SendMessageModel is then run in a separate thread to prevent the GUI from freezing while the
     * message is being sent. After the SendMessageModel has begun, the sendField is cleared so that the user can enter
     * more messages.
     *
     * @see Model.SendMessageModel
     */
    public void send(){
        SendMessageModel sendModel = new SendMessageModel(outputStream, sendField.getText());
        new Thread(sendModel).start();
        sendField.clear();
    }

    /**
     * Method used to set an output stream to the server. Must be called before the client is ready to send messages
     * to the server.
     *
     * @param outToServer a DataOutputStream object that was established during the connection phase.
     */
    public void setOutToServer(DataOutputStream outToServer){
        outputStream = outToServer;
    }

    /**
     * Method used to set an input stream from the server. When this method is called, the Controller's internal
     * ReceiveMessageModel will be instantiated and run in a separate thread. Must be called before the client is ready
     * to receive messages from the server.
     *
     * @param inFromServer an input stream that receives data from the server that was established during the connection
     *                     phase.
     */
    public void setInFromServer(BufferedReader inFromServer){
        inputStream = inFromServer;

        receiveModel = new ReceiveMessageModel(inputStream, transcript);
        Thread modelThread = new Thread(receiveModel);
        modelThread.start();
    }

    /**
     * Method that creates a close event handler for the Main GUI. Should be called as the last task before the Main GUI
     * is ready to operate.
     */
    public void setSafeClose(){
        Stage thisStage = (Stage) transcript.getScene().getWindow();
        /**
         * Close request event handler set on main chat window. Allows main window to attempt a clean shutdown.
         */
        thisStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                SendMessageModel finalSendModel = new SendMessageModel(outputStream, ">>quit");
                finalSendModel.run();
                System.exit(0);
            }
        });

    }

}
