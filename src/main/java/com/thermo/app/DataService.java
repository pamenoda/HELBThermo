package com.thermo.app;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class DataService {
    private final static String DATA_FILE_PATH ="src/main/resources/data/simul.data"; 
    private final static String ONLY_NUMBER_EXPRESSION = "^(?:[0-3]?[0-9]|40)$";

    // je renvoie une liste d'entier avec les lignes traitées
    public List<Integer> readTemperatureDataFromFile() {
        List<Integer> temperature = new ArrayList<>();
        try {Scanner scanner = new Scanner(new File(DATA_FILE_PATH));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (isNumber(line)) {
                    int value = Integer.parseInt(line.trim()); 
                    temperature.add(value);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return temperature;
    }
    // je vérifie si la ligne est correcte
    public boolean isNumber(String line){return line.matches(ONLY_NUMBER_EXPRESSION);}

    
}
