package main;

/**
 * Created by Леонид on 27.05.2016.
 */
public class FXClientRunLater implements Runnable {
    private Controller.ClientExecutionStatus clientExecutionStatus;
    private Controller controller;

    public FXClientRunLater(Controller.ClientExecutionStatus clientExecutionStatus, Controller controller) {
        this.clientExecutionStatus = clientExecutionStatus;
        this.controller = controller;
    }

    @Override
    public void run() {
     controller.setClientExecutionStatus(clientExecutionStatus);
    }
}
