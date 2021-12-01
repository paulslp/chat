package com.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    public TextField input;
    public ListView<String> listView; // список файлов в директории клиента
    private IoNet net;

    public void sendMsg(ActionEvent actionEvent) throws IOException {
        net.sendMsg(input.getText());
        input.clear();
        // String item = listView.getSelectionModel().getSelectedItem();
        // отправить выбранный в listView файл на сервер
        // придумать как это сделать
    }

    private void addMessage(String msg) {
        Platform.runLater(() -> listView.getItems().add(msg));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            net = new IoNet(this::addMessage, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
