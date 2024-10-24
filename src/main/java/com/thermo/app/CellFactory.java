package com.thermo.app;
import java.util.Random;

public class CellFactory {
    private Random random = new Random();
    public CellThermo CreateCell(int temperature,int row, int col,ThermoController controller) {
        // je calcule la probabilité d'être une cellule morte 1 chance sur 8
        double fixedDeadCellProbability = 1.0 / 8.0;
        double randomProbabilityToBeDeadCell = random.nextDouble();
        if (isCornerCell(row, col,controller) || 
        (isCenterOddCell(row, col,controller) 
        && isCenterCell(row, col,controller)))
        {
            return createSourceOfHeatCell(ThermoController.SetHotCellTemperature()); 
        } 
        else if (randomProbabilityToBeDeadCell <= fixedDeadCellProbability) 
        {return createDeadCell(0);}
        else return createNormalCell(temperature);
    }
    // si c'est dans le coin 
    private boolean isCornerCell(int row, int col,ThermoController controller) {return (row == 0 || row == controller.getRow() - 1) && (col == 0 || col == controller.getCol()- 1);}
    // si c'est la cellule au milieu défini
    private boolean isCenterCell(int row, int col,ThermoController controller) {return (row == controller.getRow()/ 2) && (col == controller.getCol() / 2);}
    // si les 2 sont impairs row et col 
    private boolean isCenterOddCell(int row, int col,ThermoController controller) {return (controller.getRow() % 2 != 0) && (controller.getCol()  % 2 != 0);}
    
    private CellThermo createSourceOfHeatCell(int temperature) {return new CellThermo(temperature,false,true,true);}
    private CellThermo createNormalCell(int temperature) {return new CellThermo(temperature,false,false,false);}
    private CellThermo createDeadCell(int temperature) {return new CellThermo(temperature,true,false,false);}
}
