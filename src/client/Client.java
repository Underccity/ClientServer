package client;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.application.Platform;
import main.Controller;
import main.Controller.ClientExecutionStatus;
import main.FXClientRunLater;

import java.io.*;
import java.net.Socket;

/**
 * Created by Леонид on 25.05.2016.
 */
public class Client implements Runnable {

    private Controller controller;
    private Socket fromserver;

    private BufferedReader in;
    private PrintWriter out;


    public Client(Controller controller) {
        this.controller = controller;
        fromserver = null;
    }

    public void run() {

        try {


            File inputFile = new File(controller.getField_inputFile().getText());
            BufferedReader inf = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));

            String fuser, fserver;

            out.println("N:" + controller.getField_countNumbers().getText());
            while ((fuser = inf.readLine()) != null) {
                out.println(fuser);
            }
            out.println("EOF");
            fserver = in.readLine();
            if (fserver == null) {
                Platform.runLater(new FXClientRunLater(ClientExecutionStatus.SERVER_SHUT_DOWN,controller));
                return;
            }
            File outputFile = new File(controller.getField_outputFile().getText());
            BufferedWriter outf = new BufferedWriter(new FileWriter(outputFile));
            outf.write(fserver);
            Platform.runLater(new FXClientRunLater(ClientExecutionStatus.SUCCESS,controller));
            outf.close();
            inf.close();
        } catch (FileNotFoundException e) {
            Platform.runLater(new FXClientRunLater(ClientExecutionStatus.FILE_NOT_FOUND,controller));
        } catch (IOException e) {
            Platform.runLater(new FXClientRunLater(ClientExecutionStatus.SERVER_SHUT_DOWN,controller));
        }
        try {
            Thread.sleep(2000);
            Platform.runLater(new FXClientRunLater(ClientExecutionStatus.WAIT,controller));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            in.close();
            out.close();
            fromserver.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connectedToServer() {
        try {

            fromserver = new Socket(controller.getField_IP().getText(), Integer.parseInt(controller.getField_Port().getText()));
            in = new BufferedReader(new InputStreamReader(fromserver.getInputStream()));
            out = new PrintWriter(fromserver.getOutputStream(), true);
            controller.setClientExecutionStatus(ClientExecutionStatus.WAIT);
        } catch (IOException e) {
            controller.setClientExecutionStatus(ClientExecutionStatus.SERVER_SHUT_DOWN);
            return false;
        } catch (NumberFormatException e) {
            controller.setClientExecutionStatus(ClientExecutionStatus.ERROR_INPUT);
            return false;
        }
        return true;
    }
}

