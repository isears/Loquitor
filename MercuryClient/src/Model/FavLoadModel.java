package Model;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Model class for loading a set of preferences from xml
 */
public class FavLoadModel implements Runnable{
    String address;
    String userName;
    String portNum;
    TextField userNameField;
    TextField addressField;
    TextField portNumberField;

    /**
     * FavLoadModel constructor
     *
     * @param addrField a pointer to the address field in the ConnectGUI
     * @param uNameField a pointer to the username field in the ConnectGUI
     * @param portNumField a pointer to the port number field in the ConnectGUI
     */
    public FavLoadModel(TextField addrField, TextField uNameField, TextField portNumField){
        userNameField = uNameField;
        addressField = addrField;
        portNumberField = portNumField;
    }

    /**
     * Thread method that reads the preference file. The preference file should be composed of three elements: address,
     * userName, and port. This function will pre-load the address, user name, and port text fields of the connectGUI
     * with the data corresponding to each of the respective elements found in pref.xml.
     * If no file is found, there will simply be no preference data loaded into the GUI.
     *
     * Expected format of pref.xml:
     * <preference>
     *     <address>**Address of preferred server**</address>
     *     <userName>**Preferred username**</userName>
     *     <port>**Preferred port**</port>
     * </preference>
     */
    public void run(){
        try{
            File f = new File("pref.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            doc.getDocumentElement().normalize();

            address = doc.getElementsByTagName("address").item(0).getTextContent();
            userName = doc.getElementsByTagName("userName").item(0).getTextContent();
            portNum = doc.getElementsByTagName("port").item(0).getTextContent();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    userNameField.setText(userName);
                    addressField.setText(address);
                    portNumberField.setText(portNum);
                }
            });

        }catch(Exception e){
            System.out.println("There was an error reading pref.xml");
            e.printStackTrace();
        }
    }

}
