package com.concessionaria;

import com.concessionaria.ui.MenuPrincipale;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        // Configurazione del sistema di logging
        configuraLogger();
        
        LOGGER.info("Avvio applicazione Concessionaria Auto");
        
        try {
            // Avvio dell'interfaccia utente
            MenuPrincipale menu = new MenuPrincipale();
            menu.avvia();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore critico nell'applicazione", e);
            System.err.println("Si è verificato un errore. Consultare il file di log.");
        }
        
        LOGGER.info("Chiusura applicazione Concessionaria Auto");
    }
    
    private static void configuraLogger() {
        try {
            FileHandler fileHandler = new FileHandler("concessionaria.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (Exception e) {
            System.err.println("Impossibile configurare il logger: " + e.getMessage());
        }
    }
}