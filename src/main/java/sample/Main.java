package sample;

import gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MainWindow.createAndShowGui(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }

}
