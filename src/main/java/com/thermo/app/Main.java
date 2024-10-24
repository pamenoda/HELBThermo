package com.thermo.app;
import javafx.application.Application;
import javafx.stage.Stage;
 
public class Main extends Application {
 
    @Override
    public void start(Stage primaryStage) throws Exception {
         
        ThermoView view = new ThermoView(primaryStage);
        ThermoController controller = new ThermoController(view);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}