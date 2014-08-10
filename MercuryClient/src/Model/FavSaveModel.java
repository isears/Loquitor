package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Model class for the preference saving task
 */
public class FavSaveModel implements Runnable{
    String address;
    String userName;
    String portNumber;

    /**
     * Constructor for the FavSaveModel
     *
     * @param addr address preference to be saved
     * @param uName username preference to be saved
     * @param portNum port number preference to be saved
     */
    public FavSaveModel(String addr, String uName, String portNum){
        address = addr;
        userName = uName;
        portNumber = portNum;
    }

    /**
     * Thread method that attempts to open the pref.xml file and save the data specified by the constructor.
     *
     * pref.xml will be saved in the following format:
     * <preference>
     *     <address>**Address of preferred server**</address>
     *     <userName>**Preferred username**</userName>
     *     <port>**Preferred port**</port>
     * </preference>
     */
    public void run(){
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("preference");
            doc.appendChild(rootElement);

            //Address element
            Element addressNode = doc.createElement("address");
            addressNode.appendChild(doc.createTextNode(address));
            rootElement.appendChild(addressNode);

            //Username element
            Element userNameNode = doc.createElement("userName");
            userNameNode.appendChild(doc.createTextNode(userName));
            rootElement.appendChild(userNameNode);

            //Port element
            Element portNode = doc.createElement("port");
            portNode.appendChild(doc.createTextNode(portNumber));
            rootElement.appendChild(portNode);

            //write content out to xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("pref.xml"));

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);

            System.out.println("Preference xml saved");

        }catch(ParserConfigurationException e){
            e.printStackTrace();
        }catch(TransformerException e){
            e.printStackTrace();
        }


    }
}
