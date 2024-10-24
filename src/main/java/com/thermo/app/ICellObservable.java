package com.thermo.app;

public interface ICellObservable {
    void addObserver(IThermoObserver observer);
    void notifyObservers(double temperature,int row,int col,CellThermo cell);
}
        

