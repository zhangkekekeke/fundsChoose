package com.img.load;

import com.img.load.panes.MainPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import load.Global;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
        Platform.exit();
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println(         Global.Companion.getProjectPath());
            MainPane mainPane = new MainPane();
            Scene scene = new Scene(mainPane.getRoot(), -1.0, -1.0);
            primaryStage.setScene(scene);
            primaryStage.show();
    }
}
