package Controller;

import Model.ConnectionModel;
import Model.FavLoadModel;
import Model.FavSaveModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the connect window defined in GUIConnect.fxml. Handles click events from the connect and
 * favorite buttons. Reads data from the addressField and unameField of the connect window. Updates the statusLabel
 * to reflect the status of attempted connections. Builds new GUI and self-destructs upon successful connection.
 *
 * @see Model.ConnectionModel
 */
public class ConnectGUIController{

    @SuppressWarnings("unused")

    @FXML private Label statusLabel;
    @FXML private TextField addressField;
    @FXML private TextField unameField;
    @FXML private TextField portField;

    ConnectionModel model;


    @FXML
    @SuppressWarnings("unused")
    protected void initialize(){
        FavLoadModel favModel = new FavLoadModel(addressField, unameField, portField);
        Thread favThread = new Thread(favModel);
        favThread.run();
    }

    /**
     * Method called on the connect button click event. When called, first checks if a connection model already exists,
     * then if a model does exist, checks if that model is currently already attempting to connect to a server.
     * If there is no connection attempt already in progress, the method instantiates a new model with the address and
     * username provided in the addressField and unameField. The model is then run in a separate thread to prevent the
     * GUI from freezing during the connection attempt.
     *
     * @see Model.ConnectionModel
     */
    public void connect(){

        if(model != null && model.connectionInProgress()){
            System.out.println("Cannot connect...connection in progress");

        }else{
            //Starting a JavaFX task so that the connect attempt (which takes a few seconds) can run in the background
            model = new ConnectionModel(
                    addressField.getText(),
                    unameField.getText(),
                    statusLabel, this,
                    Integer.parseInt(portField.getText())
            );

            Thread modelThread = new Thread(model);
            modelThread.start();
        }


    }

    /**
     * Method called on the favorite button click event. Passes data in address, uname and port fields to an instance of
     * the FavSaveModel, then runs the FavSaveModel in a separate thread to prevent GUI freezing. This allows the data
     * to be saved and loaded the next time the application is started.
     *
     * @see Model.FavSaveModel
     */
    public void favorite(){
        System.out.println("favorite called");
        String address = addressField.getText();
        String userName = unameField.getText();
        String portNum = portField.getText();

        FavSaveModel favModel = new FavSaveModel(address, userName, portNum);
        Thread favThread = new Thread(favModel);
        favThread.start();

    }

    /**
     * Method called by the connect model upon a successful connection. Destroys the current connect window and starts
     * the main chat window, passing connection resources to the main chat window's controller.
     *
     * @see Model.ConnectionModel
     * @see Controller.MainGUIController
     */
    public void initiateChatMain(){
        Stage oldStage = (Stage) statusLabel.getScene().getWindow();
        Stage newStage = new Stage();



        try{
            //Load main GUI onto new stage
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GUI/GUIMain.fxml"));
            Parent root = fxmlLoader.load();

            //Set controller and pass connection resources
            MainGUIController newController = fxmlLoader.getController();
            newController.setInFromServer(model.getInputStream());
            newController.setOutToServer(model.getOutputStream());

            //Show new stage
            newStage.setTitle("Chat Client - " + model.getServerAddress());
            newStage.setScene(new Scene(root, 400, 475));
            newController.setSafeClose();
            newStage.show();

            //Self destruct
            oldStage.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
