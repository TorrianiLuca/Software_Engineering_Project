package network.structure;

import controller.ClientController;
import javafx.application.Application;
//import view.gui.MainGui;
import view.gui.Gui;
import view.tui.Tui;
//import view.cli.Cli;
//import view.gui.JavaFXGUI;

/**
 * Main class for the client. The client can decide if he wants to launch tui or gui, and rmi or socket.
 */
public class ClientMain {
    public static void main(String[] args) {
        boolean isTui = true;
        boolean isSocket = true;
        boolean proceed = true;

        if (args.length != 2) {
            System.out.println("Number of parameters not  corrected. Required: 2, provided: " + args.length);
            proceed = false;
        } else {
            if (args[0].equals("-t")) {
                isTui = true;
            } else if (args[0].equals("-g")) {
                isTui = false;
            } else {
                System.out.println("Invalid parameter: " + args[0]);
                proceed = false;
            }

            if (args[1].equals("-s")) {
                isSocket = true;
            } else if (args[1].equals("-r")) {
                isSocket = false;
            } else {
                System.out.println("Invalid parameter: " + args[1]);
                proceed = false;
            }
        }

        if (proceed) {
            if (isTui) {
                Tui view = new Tui(isSocket);
                ClientController clientController = new ClientController(view, isSocket);
                view.addObserver(clientController);
                view.initTui();
                while (true) {
                    try {
                        Thread.sleep(1000); // Sleep to avoid busy-waiting
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } else {
                Gui.setIsSocket(isSocket);
                Application.launch(Gui.class);
            }
        } else {
            System.exit(-1);
        }
    }
}