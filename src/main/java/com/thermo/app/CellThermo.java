package com.thermo.app;

public class CellThermo implements ICellObservable{ 
   private double temperature;
   private IThermoObserver observer;
   private Boolean isSourceOfHeat = false;
   private Boolean isDeathCell = false ;
   private boolean isActivatedHeat = false;

    // constructeur
    public CellThermo(int temperature,Boolean isDeathCell,Boolean isSourceOfHeat,Boolean isActivatedHeat)
    {
        this.temperature = temperature;
        this.isDeathCell = isDeathCell;
        this.isSourceOfHeat = isSourceOfHeat;
        this.isActivatedHeat = isActivatedHeat;
    }
    
    @Override
    public void notifyObservers(double temperature,int row,int col,CellThermo cell) {
        observer.updateThermo(temperature,row,col,cell);
    }
    
    // beaucoup de duplication peut être optimisé 
    public void calculateTemperatureCell(CellThermo[][] matrix,int posRow,int posCol,int rows,int cols,int tempExt)
    {
        if (!this.isDeathCell && !this.isActivatedHeat)
        {
            if (isLocatedOnCorner(posRow, posCol, rows, cols))
            {
                setTemperature(calculateTemperatureCorner(matrix, posRow, posCol, tempExt),posRow,posCol);
            }
            else 
            {
                setTemperature(calculateTemperatureBetweenTwoCells(matrix, posRow, posCol, tempExt),posRow,posCol);
            }
        }
        
    }

    private boolean isLocatedOnCorner(int row, int col, int rows, int cols) {
        return (row == 0 && col == 0) || (row == 0 && col == cols - 1) || (row == rows - 1 && col == 0) || (row == rows - 1 && col == cols - 1);
    }
    private double calculateTemperatureCorner(CellThermo[][] matrix,int row, int col, int temperatureExterieure) {

        double temperature = 0;
        int extoriorNeighbor = 5;
        int cellAdj = CountAdjacentLiveCells(matrix, row, col) + extoriorNeighbor;
        // coin supérieur gauche 
        if (row == 0 && col == 0){
            temperature = (extoriorNeighbor*temperatureExterieure + matrix[row][col+1].getTemperature() + matrix[row+1][col+1].getTemperature() 
            + matrix[row+1][col].getTemperature()+ this.temperature) / cellAdj;
        } 
        // Coin supérieur droit
        else if (row == 0 && col == matrix[0].length - 1){
            temperature = (extoriorNeighbor * temperatureExterieure + matrix[row][col - 1].getTemperature() + matrix[row + 1][col - 1].getTemperature()
             + matrix[row + 1][col].getTemperature() + this.temperature) / cellAdj;
        } 
        // Coin inférieur gauche
        else if (row == matrix.length - 1 && col == 0){
            temperature = (extoriorNeighbor * temperatureExterieure + matrix[row][col + 1].getTemperature() + matrix[row - 1][col + 1].getTemperature() 
            + matrix[row - 1][col].getTemperature() +this.temperature) / cellAdj;
        } 
        // Coin inférieur droit
        else if (row == matrix.length - 1 && col == matrix[0].length - 1) {
            temperature = (extoriorNeighbor * temperatureExterieure + matrix[row][col - 1].getTemperature() + matrix[row - 1][col - 1].getTemperature() 
            + matrix[row - 1][col].getTemperature() +this.temperature) / cellAdj;
        }  
        return temperature;
    }

    private double calculateTemperatureBetweenTwoCells(CellThermo[][] matrix,int row, int col, int temperatureExterieure) {

        double temperature = 0;
        int extoriorNeighbor = 3;
        int cellAdj = CountAdjacentLiveCells(matrix, row, col) ;
        // ligne horizontal haut 
        if (row == 0)
        {
            cellAdj += extoriorNeighbor;
            temperature = (extoriorNeighbor*temperatureExterieure + this.temperature + matrix[row + 1][col].getTemperature() + matrix[row][col - 1].getTemperature() + matrix[row][col + 1].getTemperature()
            + matrix[row + 1][col - 1].getTemperature() + matrix[row+1][col+1].getTemperature())/cellAdj;
        }
        else if (row == matrix.length - 1)
        {
            // Ligne horizontale inférieure
            cellAdj += extoriorNeighbor;
            temperature = (extoriorNeighbor*temperatureExterieure + this.temperature + matrix[row-1][col].getTemperature() + matrix[row][col-1].getTemperature() + matrix[row][col+1].getTemperature())/cellAdj;
      
        }
        else if (col == 0)
        {
            // Colonne gauche
            cellAdj += extoriorNeighbor;
            temperature = (extoriorNeighbor*temperatureExterieure + this.temperature + matrix[row-1][col].getTemperature() + matrix[row+1][col].getTemperature() + matrix[row][col+1].getTemperature()
            + matrix[row-1][col+1].getTemperature() + matrix[row+1][col+1].getTemperature())/cellAdj;
          
        }
        else if (col == matrix[0].length - 1 )
        {
            // Colonne droite
            cellAdj += extoriorNeighbor;
            temperature = (extoriorNeighbor*temperatureExterieure +this.temperature + matrix[row-1][col].getTemperature() + matrix[row+1][col].getTemperature() + matrix[row][col-1].getTemperature()
            + matrix[row-1][col-1].getTemperature() + matrix[row+1][col-1].getTemperature())/cellAdj; 
        
        }
        else if (cellAdj == 0)
        {
            temperature += this.temperature;
        }
        else {
            // Pas sur un bord
            cellAdj += extoriorNeighbor;
            temperature = (this.temperature + matrix[row - 1][col].getTemperature() + matrix[row + 1][col].getTemperature() + matrix[row][col - 1].getTemperature()
                    + matrix[row][col + 1].getTemperature() + matrix[row - 1][col - 1].getTemperature() + matrix[row - 1][col + 1].getTemperature()
                    + matrix[row + 1][col - 1].getTemperature() + matrix[row + 1][col + 1].getTemperature())/cellAdj;
        }

        return temperature;
    } 
    // calcule les voisins
    private int CountAdjacentLiveCells(CellThermo[][] matrix, int row, int col) {
        int adjacentLiveCells = 0;
        int rowNumber = matrix.length;
        int colNumber = matrix[0].length; 
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rowNumber && j >= 0 && j < colNumber && (i != row || j != col)) {
                    if (!matrix[i][j].getIsDeathCell()) {
                        adjacentLiveCells++;
                    }
                }
            }
        }
        return adjacentLiveCells;
    }
    
    public void setTemperature(double temperature,int row,int col)
    {
        this.temperature = temperature;
        notifyObservers(temperature,row,col,this);
    }
    @Override
    public void addObserver(IThermoObserver observer) {this.observer = observer;}
    public double getTemperature() {return temperature;}
    public boolean isActivatedHeat() {return isActivatedHeat;}
    public void setActivatedHeat(boolean isActivatedHeat) {this.isActivatedHeat = isActivatedHeat;}
    public Boolean getIsSourceOfHeat() {return isSourceOfHeat;}
    public void setTemperature(double temperature){this.temperature = temperature;}
    public void setIsSourceOfHeat(Boolean isSourceOfHeat) {this.isSourceOfHeat = isSourceOfHeat;}
    public Boolean getIsDeathCell() {return isDeathCell;}
    public void setIsDeathCell(Boolean isDeathCell) {this.isDeathCell = isDeathCell;}

}
