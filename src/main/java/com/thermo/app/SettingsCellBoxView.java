package com.thermo.app;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsCellBoxView 
{
    private static int minWidth = 500;
    private static VBox mainLayout;
    private static HBox positionBox, deathCellBox, sourceHeatBox, temperatureBox;
    private static CheckBox deathCellCheckBox, sourceHeatCheckBox;
    private static Label temperatureLabel;
    private static Scene scene;
    private static Stage window;
    private static Button validateButton;
   
    public void  DisplayCellInformation(SettingsCellBoxController controllerCellBox,CellThermo cell,  int row, int col)
    {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Cell Information");
        window.setMinWidth(minWidth);

        // principal vbox
        mainLayout = new VBox();
        mainLayout.setSpacing(ThermoView.PADDING_SPACING_DEFAULT);
        mainLayout.setPadding(new Insets(ThermoView.PADDING_SPACING_DEFAULT));

        // hbox pour afficher la position
        positionBox = new HBox();
        positionBox.getChildren().add(new Label("Position of the cell: (" + row + ", " + col + ")"));

        // hbox pour définir une cellule morte 
        deathCellBox = new HBox();
        deathCellCheckBox = new CheckBox();
        deathCellCheckBox.setSelected(cell.getIsDeathCell());
        deathCellBox.getChildren().addAll(new Label("Define as a dead cell:   "), deathCellCheckBox);

        // hbox pour définir une source de chaleur 
        sourceHeatBox = new HBox();
        sourceHeatCheckBox = new CheckBox();
        sourceHeatCheckBox.setSelected(cell.getIsSourceOfHeat());
        sourceHeatBox.getChildren().addAll(new Label("Define as a heat source:   "), sourceHeatCheckBox);

        // Hbox pour set la temperature de la source de chaleur si définit 
        temperatureBox = new HBox();
        temperatureLabel = new Label("Temperature to set to the hot cell : ");

        TextField temperatureInput = new TextField();
        temperatureInput.setPromptText("Enter temperature");
        temperatureInput.setDisable(!sourceHeatCheckBox.isSelected());

        temperatureBox.getChildren().addAll(temperatureLabel, temperatureInput);
        
        sourceHeatCheckBox.setOnAction(event -> {
            if (sourceHeatCheckBox.isSelected()) temperatureInput.setDisable(false);
            else temperatureInput.setDisable(true);
        });

        // gestion du clic sur le bouton valider
        validateButton = new Button("Validate"); // j'appelle une fonction du controller pour appliquer 
        validateButton.setOnAction(event -> { controllerCellBox.checkValidation(deathCellCheckBox, sourceHeatCheckBox, temperatureInput, window);
        });
        // j'ajoute les hbox dans le mainLayout 
        mainLayout.getChildren().addAll(positionBox, deathCellBox, sourceHeatBox, temperatureBox,validateButton);

        scene = new Scene(mainLayout);
        window.setScene(scene);
        window.showAndWait();
    }   
}
