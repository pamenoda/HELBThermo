package com.thermo.app;

public interface IThermoObserver {
    void updateThermo(double temperature,int row,int col,CellThermo cell);
}
