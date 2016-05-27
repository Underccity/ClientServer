package main;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import server.Server;

import java.io.File;

public class Controller {
    private Server server = new Server(this);
    private Client client;
    @FXML
    private Label label_connectionStatus;
    @FXML
    private Button bt_startServer;
    @FXML
    private TextField field_IP;
    @FXML
    private TextField field_Port;
    @FXML
    private Button bt_joinToServer;
    @FXML
    private Button bt_inputFile;
    @FXML
    private Button bt_outputFile;
    @FXML
    private TextField field_inputFile;
    @FXML
    private TextField field_outputFile;
    @FXML
    private TextField field_countNumbers;
    @FXML
    private Label label_countNumbers;
    @FXML
    private Button bt_calculate;
    @FXML
    private Label label_executionStatus;
    @FXML
    private Label label_executionStatusValue;
    @FXML
    private Label label_inputFile;
    @FXML
    private Label label_outputFile;


    private StatusServer statusServer;
    private ClientExecutionStatus clientExecutionStatus;

    public Controller() {
        client = new Client(this);
    }

    public enum ClientExecutionStatus {
        WAIT, SUCCESS, FILE_NOT_FOUND, SERVER_SHUT_DOWN, DISCONNECTED, ERROR_INPUT
    }

    public enum StatusServer {
        STOP, WAITING, CONNECTED, DISCONNECTED, ERROR
    }

    public void initialize() {
        statusServer = StatusServer.STOP;
        clientExecutionStatus = ClientExecutionStatus.DISCONNECTED;
    }

    public void startServer() {
        if (statusServer.equals(StatusServer.CONNECTED) || statusServer.equals(StatusServer.WAITING)) {
            stopServer();
            bt_startServer.setText("Запустить сервер");
            return;
        }
        if (server.createConnection()) {
            new Thread(server).start();
            bt_startServer.setText("Остановить сервер");
        }
        else setServerStatus(StatusServer.ERROR);
    }

    private void stopServer() {
        server.stopServer();
    }

    public TextField getField_Port() {
        return field_Port;
    }

    public TextField getField_IP() {
        return field_IP;
    }

    public void connectToServer() {
        if ((clientExecutionStatus.equals(ClientExecutionStatus.SUCCESS)
                || clientExecutionStatus.equals(ClientExecutionStatus.WAIT))) {
            setClientExecutionStatus(ClientExecutionStatus.DISCONNECTED);
            disconnectClient();
            setDisabledClient(true);

            return;
        }
        if (client.connectedToServer()) {
            setClientExecutionStatus(ClientExecutionStatus.WAIT);
            setDisabledClient(false);

        }
    }


    private void disconnectClient() {
        client.disconnect();
    }

    public void setServerStatus(StatusServer statusServer) {
        this.statusServer = statusServer;
        switch (statusServer) {
            case WAITING: {
                label_connectionStatus.setText("Ожидание клиента...");
                label_connectionStatus.setTextFill(Color.ORANGE);
                break;
            }
            case CONNECTED: {
                label_connectionStatus.setText("Соединение установлено");
                label_connectionStatus.setTextFill(Color.GREEN);
                break;
            }
            case DISCONNECTED: {
                label_connectionStatus.setText("Клиент отключился");
                label_connectionStatus.setTextFill(Color.BLUE);
                break;
            }
            case STOP: {
                label_connectionStatus.setText("Сервер не запущен");
                label_connectionStatus.setTextFill(Color.BLACK);
                break;
            }
            case ERROR: {
                label_connectionStatus.setText("Ошибка запуска");
                label_connectionStatus.setTextFill(Color.RED);
                break;
            }
        }
    }

    public void setClientExecutionStatus(ClientExecutionStatus clientExecutionStatus) {
        this.clientExecutionStatus = clientExecutionStatus;
        switch (clientExecutionStatus) {
            case FILE_NOT_FOUND: {
                label_executionStatusValue.setText("Файл не найден");
                label_executionStatusValue.setTextFill(Color.RED);
                break;
            }
            case SUCCESS: {
                label_executionStatusValue.setText("Успешно");
                label_executionStatusValue.setTextFill(Color.GREEN);
                break;
            }
            case WAIT: {
                label_executionStatusValue.setText("Ожидание ввода");
                label_executionStatusValue.setTextFill(Color.ORANGE);
                break;
            }
            case SERVER_SHUT_DOWN: {
                label_executionStatusValue.setText("Сервер не отвечает");
                label_executionStatusValue.setTextFill(Color.RED);
                break;
            }
            case DISCONNECTED: {
                label_executionStatusValue.setText("Отсоединено");
                label_executionStatusValue.setTextFill(Color.BLUE);
                break;
            }
            case ERROR_INPUT: {
                label_executionStatusValue.setText("Incorrect value");
                label_executionStatusValue.setTextFill(Color.RED);
                break;
            }
        }
    }

    public void calculate() {

            for (char c : field_countNumbers.getText().toCharArray()) {
                if (!Character.isDigit(c)) {
                    setClientExecutionStatus(ClientExecutionStatus.ERROR_INPUT);
                    return;
                }
            }
        if (field_inputFile.getText().isEmpty() || field_outputFile.getText().isEmpty() || field_countNumbers.getText().isEmpty()) {
            setClientExecutionStatus(ClientExecutionStatus.ERROR_INPUT);
        }
        else new Thread(client).start();
    }


    public TextField getField_outputFile() {
        return field_outputFile;
    }

    public TextField getField_countNumbers() {

        return field_countNumbers;
    }

    public TextField getField_inputFile() {

        return field_inputFile;
    }

    private void setDisabledClient(boolean flag) {
        if (flag)
            bt_joinToServer.setText("Подключиться к серверу");
        else bt_joinToServer.setText("Отключиться от сервера");

        bt_inputFile.setDisable(flag);
        bt_outputFile.setDisable(flag);
        field_inputFile.setDisable(flag);
        field_outputFile.setDisable(flag);
        field_countNumbers.setDisable(flag);
        label_countNumbers.setDisable(flag);
        bt_calculate.setDisable(flag);
        label_inputFile.setDisable(flag);
        label_outputFile.setDisable(flag);
    }

    public void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать файл");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Леонид\\Desktop"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;
        if (event.getSource().equals(bt_inputFile))
            field_inputFile.setText(file.getAbsolutePath());
        if (event.getSource().equals(bt_outputFile))
            field_outputFile.setText(file.getAbsolutePath());
    }
}


