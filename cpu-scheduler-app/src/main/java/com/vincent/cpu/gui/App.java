package com.vincent.cpu.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 1000, 800);
        
        // Apply modern CSS styling - try to load from resources, fallback to inline if not found
        try {
            java.net.URL cssUrl = App.class.getResource("/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Fallback: apply styles directly to root
                mainView.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa 0%, #c3cfe2 100%);");
            }
        } catch (Exception e) {
            // Fallback: apply styles directly to root
            mainView.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa 0%, #c3cfe2 100%);");
        }
        
        primaryStage.setTitle("CPU Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
