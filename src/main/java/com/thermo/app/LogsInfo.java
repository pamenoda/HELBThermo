package com.thermo.app;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogsInfo {
    
    private static final String LOGS_DIRECTORY = "src/main/resources/logs/";
    private static final String FORMAT_NAME_FILE = "ddMMyy_HHmmss";
    private static FileWriter writer;
    private static String logsBySeconde="";
    // crée le dossier logs si il existe pas
    public static void initializeLogFile() {
        File logsDirectory = new File(LOGS_DIRECTORY);
        if (!logsDirectory.exists()) {
            logsDirectory.mkdirs();
        }
    }
    // appeler chaque seconde dans le run 
    public static void logSystemState(int elapsedTimeInSeconds, double cost, double temperatureMoyenne, int temperatureExterne) 
    {
        logsBySeconde += elapsedTimeInSeconds + ";" + cost + ";" + temperatureMoyenne + ";" + temperatureExterne + "\n";
    }
    // appeler à la fermeture de l'application
    public static void closeLogFile()
    {
        if (writer == null) {
            try {
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_NAME_FILE);
                String dateString = dateFormat.format(currentDate);
                String logFileName = LOGS_DIRECTORY + dateString + ".log";
                writer = new FileWriter(logFileName, true);
                writer.write(logsBySeconde);
                writer.flush();
                logsBySeconde = "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

}
