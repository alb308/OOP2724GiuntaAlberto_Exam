package com.concessionaria.exception;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());
    
    public static void gestisciEccezione(Exception e, String contesto) {
        // Log dettagliato per debugging interno
        LOGGER.log(Level.SEVERE, "Errore in " + contesto, e);
        
        // Messaggio generico per l'utente (exception shielding)
        if (e instanceof ConcessionariaException) {
            System.err.println("\n❌ Errore: " + e.getMessage());
        } else if (e instanceof NumberFormatException) {
            System.err.println("\n❌ Errore: Inserire un numero valido");
        } else if (e instanceof IllegalArgumentException) {
            System.err.println("\n❌ Errore: Dati non validi");
        } else {
            System.err.println("\n❌ Si è verificato un errore. Riprova.");
        }
    }
    
    public static String ottieniMessaggioSicuro(Exception e) {
        if (e instanceof ConcessionariaException) {
            return e.getMessage();
        } else if (e instanceof NumberFormatException) {
            return "Formato numero non valido";
        } else if (e instanceof IllegalArgumentException) {
            return "Argomento non valido";
        } else {
            return "Errore generico del sistema";
        }
    }
}