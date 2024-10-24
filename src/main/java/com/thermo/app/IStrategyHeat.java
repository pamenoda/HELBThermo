package com.thermo.app;

public interface IStrategyHeat {
    void adjustHeatSources(CellThermo[][] thermoMatrix,double tempAverage);
    String getStrategyDescription();
}

class TargetHeatStrategy  implements IStrategyHeat{
    private static final double TARGET_TEMPERATURE = 20;
    private boolean targetTemperatureReached = false;
    private static TargetHeatStrategy targeHeatStrategyInstance;
    private TargetHeatStrategy(){}

    // stratégie de target si on est au dessus de 20 degrè on desactive les sources de chaleur sinon on active
    @Override
    public void adjustHeatSources(CellThermo[][] thermoMatrix, double tempAverage) {
        if (tempAverage > TARGET_TEMPERATURE) {
            if (!targetTemperatureReached) {
                // je Désactive les sources de chaleur 
                setHeatSources(thermoMatrix, false);
                targetTemperatureReached = true;
            }
        } else {
            if (targetTemperatureReached) {
                // je Réactive les sources de chaleur 
                setHeatSources(thermoMatrix, true);
                targetTemperatureReached = false;
            }
        }
    }
    private void setHeatSources(CellThermo[][] thermoMatrix, boolean activate) {
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                if (thermoMatrix[i][j].getIsSourceOfHeat()) {
                    thermoMatrix[i][j].setActivatedHeat(activate);
                    if (activate) {
                        thermoMatrix[i][j].setTemperature(ThermoController.SetHotCellTemperature());
                    }
                }
            }
        }
    }
    // singleton 
    public static TargetHeatStrategy getTargeHeatStrategyInstance() {
        if (targeHeatStrategyInstance == null ) 
            targeHeatStrategyInstance = new TargetHeatStrategy();
        return targeHeatStrategyInstance;
    }

    @Override
    public String getStrategyDescription() {return "Target Heating";}
}


class ManualHeatStrategy implements IStrategyHeat{

    private static ManualHeatStrategy manualHeatStrategyInstance;
    private ManualHeatStrategy(){}

    // stratégie qui active toutes les sources de chaleur
    @Override
    public void adjustHeatSources(CellThermo[][] thermoMatrix,double tempAverage) {
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                if (thermoMatrix[i][j].getIsSourceOfHeat())
                {
                    thermoMatrix[i][j].setActivatedHeat(true);
                    thermoMatrix[i][j].setTemperature(ThermoController.SetHotCellTemperature());
                }    
            }
        }
    }
    @Override
    public String getStrategyDescription() {return "ManualStrategy";}
    // singleton 
    public static ManualHeatStrategy getManualHeatStrategyInstance() {
        if (manualHeatStrategyInstance == null ) manualHeatStrategyInstance = new ManualHeatStrategy();
        return manualHeatStrategyInstance;
    }

}

