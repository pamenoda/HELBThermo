package com.thermo.app;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsCellBoxController {
    private SettingsCellBoxView settingsCellBoxView;
    private ThermoController thermoController;
    private CellThermo cell;
    private Button cellButton;
    private int row, col;

    // constructeur du controlleur pour la gestion de modification de cellule 
    public SettingsCellBoxController(SettingsCellBoxView settingsCellBoxView, ThermoController thermoController) {
        this.settingsCellBoxView = settingsCellBoxView;
        this.thermoController = thermoController;
    } 
    // recupere les informations de la cellule et est appelé pour la cellule clickée
    public void getCellInformation(CellThermo cell, Button cellButton, int row, int col) {
        this.cell = cell;
        this.cellButton = cellButton;
        this.row = row;
        this.col = col;

        settingsCellBoxView.DisplayCellInformation(this,cell,row,col);
    }
    
    // méthode appeler dans le set on action de la vue du bouton validation gère la logique de modification de cellule 
    public void checkValidation(CheckBox deathCellCheckBox, CheckBox sourceHeatCheckBox, TextField temperatureInput, Stage window) {
        if (deathCellCheckBox.isSelected() && sourceHeatCheckBox.isSelected()) {
            ThermoView.ErrorMessage("Warning", "Invalid selection", "Cell cannot be both dead and a heat source");
            return;
        }
        if (deathCellCheckBox.isSelected()) {
            cell.setIsSourceOfHeat(false);
            cell.setIsDeathCell(true);
            cell.setTemperature(0, row, col);
            cellButton.setBackground(ThermoView.blackBackground);
        } else if (sourceHeatCheckBox.isSelected()) {
            try {
                double temperatureValue = Double.parseDouble(temperatureInput.getText());
                if (temperatureValue > thermoController.getMAX_TEMPERATURE() || temperatureValue < thermoController.getMIN_TEMPERATURE()) {
                    ThermoView.ErrorMessage("Error", "Invalid temperature", "Please enter a correct value of temperature");
                    return;
                }
                cell.setTemperature(temperatureValue, row, col);
                cell.setIsDeathCell(false);
                cell.setIsSourceOfHeat(true);
                cell.setActivatedHeat(true);
            } catch (NumberFormatException e) {
                ThermoView.ErrorMessage("Error", "Invalid temperature", "Please enter a valid temperature value");
                return;
            }
        } else {
            cell.setIsDeathCell(false);
            cell.setIsSourceOfHeat(false);
            cell.setActivatedHeat(false);
        }

        window.close();
    }
}
