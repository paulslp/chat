package com.geekbrains.chat.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    public ListView<String> listView; // список файлов в директории клиента
    private IoNet net;

    @FXML
    public void onClickMethod() {
        String item = listView.getSelectionModel().getSelectedItem();
        net.writeFileToClientSocketOutputStream(item);
    }

    private void addFilePath(List<String> files) {
        Platform.runLater(() -> listView.setItems(FXCollections.observableArrayList(files)));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            net = new IoNet(files -> addFilePath(files), socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
