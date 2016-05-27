package server;

import javafx.application.Platform;
import main.Controller;
import main.FXServerRunLater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Created by Леонид on 25.05.2016.
 */
public class Server implements Runnable {
    private Controller controller;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    public Server(Controller controller) {
        this.controller = controller;
    }

    public void run() {

        while (true) {
            try {
                Platform.runLater(new FXServerRunLater(Controller.StatusServer.WAITING, controller));

                clientSocket = serverSocket.accept();

                Platform.runLater(new FXServerRunLater(Controller.StatusServer.CONNECTED, controller));
            } catch (IOException e) {
                if (serverSocket.isClosed())
                    Platform.runLater(new FXServerRunLater(Controller.StatusServer.STOP, controller));
                else
                    Platform.runLater(new FXServerRunLater(Controller.StatusServer.ERROR, controller));
                return;
            }

            try (BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String input;
                String output = "";
                int N = 0;
                Pattern getN = Pattern.compile("N:\\d+");
                while ((input = inStream.readLine()) != null) {
                    if (input.equals("EOF")) {
                        outStream.println(calculateNumbers(output, N));
                        output = "";
                        continue;
                    }
                    if (getN.matcher(input).matches()) {
                        input = input.replaceAll("N:", "");
                        N = Integer.parseInt(input);
                        continue;
                    }
                    output += input;
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            finally {
                if(clientSocket.isClosed()) Platform.runLater(new FXServerRunLater(Controller.StatusServer.DISCONNECTED, controller));
                if (serverSocket.isClosed()) {
                    Platform.runLater(new FXServerRunLater(Controller.StatusServer.STOP, controller));
                    return;
                }

            }
        }
    }

    public boolean createConnection() {
        try {
            serverSocket = new ServerSocket(27015);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void stopServer() {
        try {
            if (clientSocket != null) clientSocket.close();
            serverSocket.close();
            controller.setServerStatus(Controller.StatusServer.STOP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String calculateNumbers(String inputString, int N) {

        int count = 0;
        String outputString;
        String[] tempArr;

        outputString = inputString.replaceAll("[^0-9]+", " ");
        tempArr = outputString.trim().split(" ");
        outputString = "";
        for (String aTempArr : tempArr) {
            if (count < N && !aTempArr.equals("")) {
                outputString += aTempArr + " ";
                count++;
            } else return outputString;
        }
        return outputString;
    }
}
