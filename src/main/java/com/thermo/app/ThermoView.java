package com.thermo.app;
import java.io.InputStream;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ThermoView implements IThermoObserver {
    public final static int WIDTH = 1000, HEIGHT = 1000, PADDING_SPACING_DEFAULT = 20, BUTTON_SIZE_WIDTH = 150,BUTTON_SIZE_HEIGHT = 50,COMBO_BOX_SIZE= 150,ICON_BUTTON_SIZE = 32;
    public final static String DECIMAL_FORMAT = "%.2f",EURO =" €",DEGRE = "°",TITLE = "HELBTHERMO";
    public static Background normalBackground, blackBackground, grayBackround;
    private final String[] strategy = { "Manual Heating", "Target Heating" };
    private final String[] statInfo = {"Time ","Price ","Exterior Temp ","avg Temp "};
    private final String[] pathPicture = {"/images/play.png","/images/break.png","/images/refresh.png"};
    private final static Color COLD_COLOR = Color.WHITE, HOT_COLOR = Color.RED;
    private int cellSize = 50;
    private Stage primaryStage;
    private Button[] controlButtons;
    private HashMap<String, Button[]> linkHotCells = new HashMap<>();
    private Button buttonTemp, buttonPrice, buttonTempExt, buttonTempMoy;
    private GridPane gridPane;
    private Button[][] cellGridPane;
    private HBox topLayout, settingsLayout,hboxForGridAndHotCellLayout;
    private VBox mainLayout, hotCellLayout;
    private Scene scene;
    private ComboBox<String> heatingModeComboBox;

    public ThermoView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        blackBackground = createBackground(Color.BLACK);
        normalBackground = createBackground(Color.WHITE);
        grayBackround = createBackground(Color.GRAY);
        this.primaryStage.setTitle(TITLE);
        this.primaryStage.show();
    }
    // création de l'interface graphique 
    public void defaultUI(ThermoController controller) {
        createTopLayout();
        createSettingsLayout();
        createHotCellLayoutAndGridPane(controller);
        createMainLayout();
        primaryStage.setScene(scene);
    }
    // création d'une hbox pour les informations concernant le systeme 
    private void createTopLayout() {
        topLayout = new HBox();
        topLayout.setSpacing(PADDING_SPACING_DEFAULT);
        topLayout.setPadding(new Insets(PADDING_SPACING_DEFAULT, 0, 0, PADDING_SPACING_DEFAULT));
        
        buttonTemp = createButton(statInfo[0], BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT, normalBackground);
        buttonPrice = createButton(statInfo[1], BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT, normalBackground);
        buttonTempExt = createButton(statInfo[2], BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT, normalBackground);
        buttonTempMoy = createButton(statInfo[3], BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT, normalBackground);        
        heatingModeComboBox = new ComboBox<>();
        heatingModeComboBox.getItems().addAll(strategy);
        heatingModeComboBox.setPrefWidth(COMBO_BOX_SIZE);
        heatingModeComboBox.getSelectionModel().selectFirst();
    
        topLayout.getChildren().addAll(buttonTemp, buttonPrice, buttonTempExt, buttonTempMoy, heatingModeComboBox);
    }
    // création du layout pour mettre start pause et reset
    private void createSettingsLayout() {
        settingsLayout = new HBox();
        settingsLayout.setPadding(new Insets(PADDING_SPACING_DEFAULT, 0, 0, PADDING_SPACING_DEFAULT));
        controlButtons = new Button[3];
        controlButtons[0] = createIconButton(pathPicture[0]);
        controlButtons[1] = createIconButton(pathPicture[1]);
        controlButtons[2] = createIconButton(pathPicture[2]);
        settingsLayout.getChildren().addAll(controlButtons);
    }
    // création de la gridpane et du pannel a gauche
    private void createHotCellLayoutAndGridPane(ThermoController controller) {
        hotCellLayout = new VBox();
        hotCellLayout.setSpacing(PADDING_SPACING_DEFAULT);
        hotCellLayout.setPadding(new Insets(PADDING_SPACING_DEFAULT, 0, 0, PADDING_SPACING_DEFAULT));
        
        gridPane = new GridPane();
        gridPane.setHgap(PADDING_SPACING_DEFAULT / 2);
        gridPane.setVgap(PADDING_SPACING_DEFAULT / 2);
        gridPane.setPadding(new Insets(PADDING_SPACING_DEFAULT, PADDING_SPACING_DEFAULT, 0, PADDING_SPACING_DEFAULT));
        
        initializeGridPane(controller);
        
        hboxForGridAndHotCellLayout = new HBox();
        hboxForGridAndHotCellLayout.getChildren().addAll(hotCellLayout, gridPane);
        hboxForGridAndHotCellLayout.setSpacing(PADDING_SPACING_DEFAULT);
    }
    
    // vbox qui va contenir tout 
    private void createMainLayout() {
        mainLayout = new VBox();
        mainLayout.getChildren().addAll(topLayout, settingsLayout, hboxForGridAndHotCellLayout);
        scene = new Scene(mainLayout, WIDTH, HEIGHT);
    }

    // création de button 
    private Button createButton(String text, int width, int height, Background background) {
        Button button = new Button(text);
        button.setPrefWidth(width); // Largeur préférée
        button.setPrefHeight(height); // Hauteur préférée
        button.setBackground(background);
        return button;
    }
    // j'appelle lors d'un message d'erreur 
    public static Alert ErrorMessage(String title,String headerText,String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.showAndWait();  
        return alert;
    } 
    // création de bouton avec icone
    private Button createIconButton(String iconFileName) {
        try {
            // je charge l'icône à partir du fichier image
            InputStream inputStream = getClass().getResourceAsStream(iconFileName);
            if (inputStream != null) {
                Image icon = new Image(inputStream);
                ImageView iconView = new ImageView(icon);
                iconView.setFitWidth(ICON_BUTTON_SIZE); 
                iconView.setFitHeight(ICON_BUTTON_SIZE); 
                Button button = new Button();
                button.setGraphic(iconView); 
                button.setPrefWidth(cellSize); 
                button.setPrefHeight(cellSize); 
                return button;
            } 
            return new Button(); 
        } catch (Exception e) {
            return new Button(); 
        }
    }
    // initialisation de la gridpane 
    public void initializeGridPane(ThermoController controller) {
        CellThermo[][] thermoMatrix = controller.getThermoMatrix();
        cellGridPane = new Button[thermoMatrix.length][thermoMatrix[0].length];
        int maxRow = thermoMatrix.length - 1; // Indice maximal de ligne
        int maxColumn = thermoMatrix[0].length - 1; // Indice maximal de colonne
        if (maxRow + 1 > controller.getMaxColRow() / 2 || maxColumn + 1 > controller.getMaxColRow() / 2 )cellSize = 75;
        else cellSize = 100;
        Button button;
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                button = createThermoButton(thermoMatrix[i][j]);
                cellGridPane[i][j] = button;
                gridPane.add(button, j, i); // Ajoute le bouton à la grille
            }
        }
    } 
    // création de button de chaleur 
    private Button createThermoButton(CellThermo cell) { 
        String temperatureText = String.valueOf(cell.getTemperature());
        Button button = createButton(temperatureText, cellSize, cellSize,calculateColorForTemperature(cell.getTemperature()));
        if (cell.getIsDeathCell()) button.setBackground(blackBackground); 
        return button;
    }
    // permet de garder a jour la vue entre le button de chaleur de la gridpane et du layout source de chaleur a gauche pas opti mais marche
    public void updateHotCellView(HashMap<String, Button[]> hotCells, CellThermo[][] thermoMatrix) {
        clearHotCell();
        int cpt = 0;
        for (int i = 0; i < thermoMatrix.length; i++) {
            for (int j = 0; j < thermoMatrix[0].length; j++) {
                if (thermoMatrix[i][j].getIsSourceOfHeat()) {
                    cpt++;
                    Button buttonGridPane = getButtonOfGridPane(i, j);
                    String textButton = "S" + cpt + ": \n" + String.format(DECIMAL_FORMAT, thermoMatrix[i][j].getTemperature());
                    Button hotCellInLayoutHot;
                    if (thermoMatrix[i][j].isActivatedHeat()) {
                        hotCellInLayoutHot = createButton(textButton, cellSize, cellSize,calculateColorForTemperature(thermoMatrix[i][j].getTemperature()));
                        buttonGridPane.setBackground(calculateColorForTemperature(thermoMatrix[i][j].getTemperature()));
                    } else {
                        hotCellInLayoutHot = createButton(textButton, cellSize, cellSize, grayBackround);
                        buttonGridPane.setBackground(grayBackround);
                    }
                    buttonGridPane.setText(textButton);
                    hotCellLayout.getChildren().add(hotCellInLayoutHot);
                    hotCells.put(i + ";" + j, new Button[] { hotCellInLayoutHot, buttonGridPane });
                }
            }
        }
    }
    // met a jour à chaque changement d'état de la cellule 
    @Override
    public void updateThermo(double temperature, int row, int col, CellThermo cell) {
        Button button = getButtonOfGridPane(row, col);
        button.setText(String.format(DECIMAL_FORMAT, temperature));
        if (!cell.isActivatedHeat() && cell.getIsSourceOfHeat()) {
            button.setBackground(grayBackround);
        }else
            button.setBackground(calculateColorForTemperature(temperature));
    }
    // création d'un background avec les rgb modifiés en fonction de la température  
    public static Background calculateColorForTemperature(double temperature) {
        double minTemp = ThermoController.getMinTemperature();
        double maxTemp = ThermoController.getMaxTemperature();
        // je calcule la proportion pour avoir le pourcentage 
        double proportion = (temperature - minTemp) / (maxTemp - minTemp);
        // 255 - 255 - 255 
        // 255 - 0 - 0
        // je calcule la proportion de chaque composant du rgb 
        double red = COLD_COLOR.getRed() + proportion * (HOT_COLOR.getRed() - COLD_COLOR.getRed());
        double green = COLD_COLOR.getGreen() + proportion * (HOT_COLOR.getGreen() - COLD_COLOR.getGreen());
        double blue = COLD_COLOR.getBlue() + proportion * (HOT_COLOR.getBlue() - COLD_COLOR.getBlue());
    
        // je normalise les valeurs pour les rendre flexible et modulaire 
        red = Math.max(0, Math.min(1, red));
        green = Math.max(0, Math.min(1, green));
        blue = Math.max(0, Math.min(1, blue));
    
        Color color = new Color(red, green, blue, 1.0);
        Background background = createBackground(color);
        return background; 
    }
    // création de background
    private static Background createBackground(Color color) {return new Background(new BackgroundFill(color, null, null));}
    // recupere le bouton de la cellule à la position souhaité
    public Button getButtonOfGridPane(int row, int col) {return cellGridPane[row][col];}
    public void clearHotCell() {hotCellLayout.getChildren().clear();}
    public GridPane getGridPane() {return gridPane;}
    public void updatePriceHotCell(double price) {buttonPrice.setText(statInfo[1]+ String.format(DECIMAL_FORMAT, price) + EURO);}
    public void updateTempMoy(double temperatureMoy) {buttonTempMoy.setText(statInfo[3] +String.format(DECIMAL_FORMAT, temperatureMoy) + DEGRE);}
    public void updateElapsedTime(int elapsedTimeInSeconds) {buttonTemp.setText(statInfo[0]+ elapsedTimeInSeconds);}
    public void updateTempExt(int temperature) {buttonTempExt.setText(statInfo[2]+temperature + DEGRE);}
    public Button[] getControlButtons() {return controlButtons;}
    public ComboBox<String> getHeatingModeComboBox() {return heatingModeComboBox;}
    public String[] getStrategy() {return strategy;}
    public HashMap<String, Button[]> getLinkHotCells() {return linkHotCells;}
    public Stage getPrimaryStage() {return primaryStage;}
}
