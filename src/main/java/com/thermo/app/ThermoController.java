package com.thermo.app;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.util.Duration;

public class ThermoController {
    private CellThermo[][] thermoMatrix;
    private static final int MAX_TEMPERATURE = 100,MIN_TEMPERATURE = 0,FRAME_TIME_LINE_IN_SECOND = 1,DEFAULT_HOT_CELL_TEMP = Math.abs(MAX_TEMPERATURE /4);
    private ThermoView view;
    private Timeline timeline;
    private int elapsedTimeInSeconds, indexOfTempExt, variableTemperatureExt, row = 5, col = 5, minColRow = 3, maxColRow = 12;
    private double temperatureAvg, priceHotCell;
    private CellFactory cellFactory;
    private DataService dataService;
    private Button[] controlButtons;
    private List<Integer> exteriorTemperature;
    private ComboBox<String> heatingModeComboBox;
    private SettingsCellBoxController settingsCellBoxController;
    private HashMap<String, Button[]> hotCellsLinked;
    private IStrategyHeat heatStrategy;

    // constructeur du controlleur 
    public ThermoController(ThermoView view) {
        dataService = new DataService(); // instance qui gère l'ouverture du fichier 
        cellFactory = new CellFactory(); // instance qui gère la création de cellule 
        settingsCellBoxController = new SettingsCellBoxController(new SettingsCellBoxView(), this);
        initialize(view);  // initialise toute la view 
        LogsInfo.initializeLogFile(); // initialise le dossier log 
        heatStrategy = ManualHeatStrategy.getManualHeatStrategyInstance(); // stratégie définie 
    }
    // méthode d'initialisation
    public void initialize(ThermoView view) {
        this.view = view;
        createCustomThermoSystem(row, col);
        initializeParameters();
        exteriorTemperature = dataService.readTemperatureDataFromFile();  // recupere les données du fichier 
        variableTemperatureExt = exteriorTemperature.get(indexOfTempExt); // recupere la 1 ere donnée 
        view.defaultUI(this);
        view.updateTempExt(variableTemperatureExt);
        initializeTimeline();
        initializeSetOnActionButton();
        breakTimeline();
    }

    // initialise les paramètres 
    public void initializeParameters()
    {
        indexOfTempExt = 0;
        priceHotCell = 0;
        temperatureAvg = 0;
        elapsedTimeInSeconds = 0;
    }
    // timeline fréquence 1 seconde
    public void initializeTimeline()
    {
        timeline = new Timeline(
            new KeyFrame(Duration.seconds(FRAME_TIME_LINE_IN_SECOND), event -> run())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    //  initialisation des gestion de clic au lancement de l'application 
    public void initializeSetOnActionButton() {
        controlButtons = view.getControlButtons();
        // j'ajoute des écouteurs d'événements aux boutons
        controlButtons[0].setOnAction(event -> startTimeline());
        controlButtons[1].setOnAction(event -> breakTimeline());
        controlButtons[2].setOnAction(event -> resetTimeline());

        heatingModeComboBox = view.getHeatingModeComboBox();
        // stratégie détection 
        heatingModeComboBox.setOnAction(event -> {
            String selectedMode = heatingModeComboBox.getValue();
            if (selectedMode.equals(view.getStrategy()[0])) {
                heatStrategy = ManualHeatStrategy.getManualHeatStrategyInstance();
                heatStrategy.adjustHeatSources(thermoMatrix, temperatureAvg);

            } else if (selectedMode.equals(view.getStrategy()[1])) {
                heatStrategy = TargetHeatStrategy.getTargeHeatStrategyInstance();
            }
        });
        setOnActionHotCellLayout();
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                Button button = view.getButtonOfGridPane(i, j);
                int positionRow = i;
                int positionCol = j;
                view.getButtonOfGridPane(i, j).setOnAction(event -> 
                {
                    breakTimeline();
                    // 2 ieme controlleur qui gère la logique 
                    settingsCellBoxController.getCellInformation(thermoMatrix[positionRow][positionCol], button, positionRow, positionCol);
                    startTimeline();
                });
            }
        }
        view.getPrimaryStage().setOnCloseRequest(event -> {
            // j'appele la méthode de fermeture de fichier de journal
            LogsInfo.closeLogFile();
        });
    }

    // méthode appeler toutes les secondes via la timeline 
    private void run() {
        elapsedTimeInSeconds += 1;
        if (indexOfTempExt < exteriorTemperature.size() - 1) indexOfTempExt += 1;
        calculateTemperatureSystem();
        updateUI();
        variableTemperatureExt = exteriorTemperature.get(indexOfTempExt);
        if (heatStrategy.getStrategyDescription().equals(view.getStrategy()[1])) heatStrategy.adjustHeatSources(thermoMatrix, temperatureAvg);
        LogsInfo.logSystemState(elapsedTimeInSeconds, priceHotCell, temperatureAvg, variableTemperatureExt);
    }

    // addenda Vérifie si les dimensions spécifiées sont valides
    private void createCustomThermoSystem(int rows, int columns) { 
        if (isValidDimensions(rows, columns)) {
            thermoMatrix = new CellThermo[rows][columns];
        } else {
                ThermoView.ErrorMessage("Warning", "Invalid  selection Row and col ", "defined 5x5");
                row = 5;
                col = 5;      
                thermoMatrix = new CellThermo[row][col];    
        }
        initializeThermoMatrix(); // j'initialise la nouvelle matrice de cellules de température
    }
    
    private void initializeThermoMatrix() {
        // j'initialise la matrice avec des cellules de température via l'appelle de la méthode dans la fabrique
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                thermoMatrix[i][j] = cellFactory.CreateCell(variableTemperatureExt, i, j, this);
                thermoMatrix[i][j].addObserver(view); // on ajoute la view comme observer a chaque cellule observable 
            }
        }
        
    }
    // addenda vérifie si les dimensions sont respectés
    private boolean isValidDimensions(int rows, int columns) {return (rows >= minColRow && rows <= maxColRow && columns >= minColRow && columns <= maxColRow);}
    // renvoie la matrice de cellule
    public  CellThermo[][] getThermoMatrix() {return thermoMatrix;}
    // play l'application 
    public void startTimeline() {timeline.play();}
    // met en pause l'application 
    public void breakTimeline() {timeline.pause();}
    // restart l'application avec tout a 0
    public void resetTimeline() {
        elapsedTimeInSeconds = 0;
        priceHotCell = 0;
        breakTimeline();
    }

   // je  met a jour les éléments graphique via des méthodes de la view appeler chaque seconde 
    public void updateUI()
    {
        view.updateHotCellView(hotCellsLinked, thermoMatrix);
        view.updateElapsedTime(elapsedTimeInSeconds);
        view.updateTempExt(exteriorTemperature.get(indexOfTempExt));
        view.updateTempMoy(temperatureAvg);
        view.updatePriceHotCell(priceHotCell);
        setOnActionHotCellLayout();
    }

    // gestion du clic sur le panneau de source de chaleur 
    private void setOnActionHotCellLayout() {
        hotCellsLinked = view.getLinkHotCells();
        // j'itére sur chaque élément de la HashMap 
        for (Map.Entry<String, Button[]> entry : hotCellsLinked.entrySet()) {
            String[] indices = entry.getKey().split(";");
            int rowIndex = Integer.parseInt(indices[0]);
            int colIndex = Integer.parseInt(indices[1]);
            CellThermo cell = thermoMatrix[rowIndex][colIndex];
            Button buttonHotCell = entry.getValue()[0];
            Button buttonGridPane = entry.getValue()[1];

            // je définis l'action pour chaque bouton du panneau à gauche 
            buttonHotCell.setOnAction(event -> {
                if (cell.isActivatedHeat()) {
                    // si activé on désactive la source de chaleur 
                    thermoMatrix[rowIndex][colIndex].setActivatedHeat(false);
                } else {
                    // si désactivé on la réactive 
                    double temperature = ThermoController.SetHotCellTemperature();
                    thermoMatrix[rowIndex][colIndex].setActivatedHeat(true);
                    thermoMatrix[rowIndex][colIndex].setTemperature(temperature, rowIndex, colIndex);
                    buttonHotCell.setText(String.format(ThermoView.DECIMAL_FORMAT, temperature));
                    buttonGridPane.setBackground(ThermoView.calculateColorForTemperature(cell.getTemperature()));
                    buttonGridPane.setText(String.format(ThermoView.DECIMAL_FORMAT, temperature));
                    buttonHotCell.setBackground(ThermoView.calculateColorForTemperature(cell.getTemperature()));
                }
            });
        }
    }
    // j'appelle toutes les secondes pour calculer la température dans le modèle cellThermo et le prix ainsi que la temp moyenne
    private void calculateTemperatureSystem() {
        int cpt = 0;
        temperatureAvg = 0;
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                thermoMatrix[i][j].calculateTemperatureCell(thermoMatrix, i, j, thermoMatrix.length, thermoMatrix[0].length, variableTemperatureExt);   
                if (thermoMatrix[i][j].getIsSourceOfHeat() && thermoMatrix[i][j].isActivatedHeat()) 
                    priceHotCell += thermoMatrix[i][j].getTemperature() * thermoMatrix[i][j].getTemperature();
                if (!thermoMatrix[i][j].getIsDeathCell())
                {
                    temperatureAvg += thermoMatrix[i][j].getTemperature();
                    cpt++;
                } 
            }
        }
        temperatureAvg /= cpt;
    }
    public static int SetHotCellTemperature() {return DEFAULT_HOT_CELL_TEMP;}
    public int getRow() {return row;}
    public int getMAX_TEMPERATURE() {return MAX_TEMPERATURE;}
    public int getMIN_TEMPERATURE() {return MIN_TEMPERATURE;}
    public int getCol() {return col;}
    public int getMinColROw() {return minColRow;}
    public int getMaxColRow() {return maxColRow;}
    public static int getMaxTemperature() {return MAX_TEMPERATURE;}
    public static int getMinTemperature() {return MIN_TEMPERATURE;}

}
