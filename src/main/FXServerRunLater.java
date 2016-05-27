package main;

/**
 * Created by Леонид on 27.05.2016.
 */
public class FXServerRunLater implements Runnable {

    private Controller.StatusServer statusServer;
    private Controller controller;

    public FXServerRunLater(Controller.StatusServer statusServer, Controller controller) {
        this.controller=controller;
        this.statusServer = statusServer;
    }

    @Override
    public void run() {
        controller.setServerStatus(statusServer);
    }
}
