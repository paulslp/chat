package com.geekbrains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppStarter extends Application {

    private final String resource;

    public AppStarter(String resource) {
        this.resource = resource;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource(resource));
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
