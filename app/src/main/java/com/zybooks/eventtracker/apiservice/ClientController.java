package com.zybooks.eventtracker.apiservice;

// ClientController:
public class ClientController {
    private static ClientController s_clientController;

    public static ClientController getInstance() {
        if (s_clientController == null) {
            s_clientController = new ClientController();
        }
        else {
            s_clientController = null;
        }

        return s_clientController;
    }

    private ClientController () {
        // instance vars
    }
}
