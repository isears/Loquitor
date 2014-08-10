package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Standard GUI creation. Starts the connect GUI based on GUIConnect.fxml
 */
public class ConnectGUI extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUIConnect.fxml"));
        primaryStage.setTitle("Connect to a Chat Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    public void launchClientConnect(){
        launch();
    }
}
