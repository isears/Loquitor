package Controller;

import GUI.ConnectGUI;

/**
 * Entry point of program. Launches the connect GUI.
 */
public class Main {
    public static void main(String[] args){
        ConnectGUI startGUI = new ConnectGUI();
        startGUI.launchClientConnect();
    }
}
