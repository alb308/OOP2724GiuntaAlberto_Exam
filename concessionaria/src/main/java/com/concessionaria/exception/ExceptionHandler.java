package com.concessionaria.exception;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;

/**
 * Exception Handler con shielding completo per nascondere dettagli tecnici
 */
public class ExceptionHandler {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());
    
    // Messaggi user-friendly per diversi tipi di eccezione
    private static final Map<Class<?>, String> EXCEPTION_MESSAGES = new HashMap<>();
    
    static {
        EXCEPTION_MESSAGES.put(NullPointerException.class, "Dati mancanti o non validi");
        EXCEPTION_MESSAGES.put(NumberFormatException.class, "Formato numerico non valido");
        EXCEPTION_MESSAGES.put(IllegalArgumentException.class, "Parametri non validi");
        EXCEPTION_MESSAGES.put(ArrayIndexOutOfBoundsException.class, "Indice non valido");
        EXCEPTION_MESSAGES.put(ClassCastException.class, "Tipo di dato non compatibile");
        EXCEPTION_MESSAGES.put(UnsupportedOperationException.class, "Operazione non supportata");
        EXCEPTION_MESSAGES.put(SecurityException.class, "Operazione non autorizzata");
        EXCEPTION_MESSAGES.put(java.io.IOException.class, "Errore di accesso ai dati");
        EXCEPTION_MESSAGES.put(java.sql.SQLException.class, "Errore database");
        EXCEPTION_MESSAGES.put(java.net.ConnectException.class, "Errore di connessione");
        EXCEPTION_MESSAGES.put(java.util.concurrent.TimeoutException.class, "Timeout operazione");
    }
    
    /**
     * Gestisce un'eccezione loggando i dettagli e ritornando un messaggio sicuro
     */
    public static String gestisciEccezione(Exception e, String contesto) {
        // Log completo per debugging (solo nel file di log)
        logger.log(Level.SEVERE, "Eccezione in " + contesto, e);
        
        // Log delle informazioni di contesto
        logContextInfo(e, contesto);
        
        // Ritorna messaggio user-friendly
        return getMessaggioSicuro(e);
    }
    
    /**
     * Gestisce un'eccezione mostrando un messaggio all'utente
     */
    public static void gestisciEMostra(Exception e, String contesto) {
        String messaggio = gestisciEccezione(e, contesto);
        System.err.println("\n⚠️  " + messaggio);
    }
    
    /**
     * Ottiene un messaggio sicuro per l'utente basato sul tipo di eccezione
     */
    private static String getMessaggioSicuro(Exception e) {
        // Prima controlla se è una nostra eccezione custom
        if (e instanceof ConcessionariaException) {
            return e.getMessage();
        }
        
        // Poi controlla la mappa dei messaggi predefiniti
        String messaggio = EXCEPTION_MESSAGES.get(e.getClass());
        if (messaggio != null) {
            return messaggio;
        }
        
        // Controlla le superclassi
        Class<?> clazz = e.getClass().getSuperclass();
        while (clazz != null && clazz != Object.class) {
            messaggio = EXCEPTION_MESSAGES.get(clazz);
            if (messaggio != null) {
                return messaggio;
            }
            clazz = clazz.getSuperclass();
        }
        
        // Messaggio generico di fallback
        return "Si è verificato un errore imprevisto. Riprova più tardi.";
    }
    
    /**
     * Log informazioni di contesto utili per il debugging
     */
    private static void logContextInfo(Exception e, String contesto) {
        logger.severe("=== DETTAGLI ECCEZIONE ===");
        logger.severe("Contesto: " + contesto);
        logger.severe("Tipo eccezione: " + e.getClass().getName());
        logger.severe("Messaggio originale: " + e.getMessage());
        
        // Log dello stack trace solo per eccezioni non previste
        if (!(e instanceof ConcessionariaException)) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                logger.severe("Origine: " + stackTrace[0].toString());
            }
        }
        
        // Log della causa se presente
        Throwable causa = e.getCause();
        if (causa != null) {
            logger.severe("Causa: " + causa.getClass().getName() + " - " + causa.getMessage());
        }
        
        logger.severe("========================");
    }
    
    /**
     * Wrappa un'eccezione tecnica in una user-friendly
     */
    public static ConcessionariaException wrapException(Exception e, String messaggioUtente) {
        logger.log(Level.WARNING, "Wrapping eccezione tecnica", e);
        return new ConcessionariaException(messaggioUtente, e);
    }
    
    /**
     * Valida che un oggetto non sia null, altrimenti lancia eccezione user-friendly
     */
    public static void requireNonNull(Object obj, String nomeParametro) throws ConcessionariaException {
        if (obj == null) {
            throw new ConcessionariaException("Il parametro '" + nomeParametro + "' è obbligatorio");
        }
    }
    
    /**
     * Valida che una stringa non sia vuota
     */
    public static void requireNonEmpty(String str, String nomeParametro) throws ConcessionariaException {
        if (str == null || str.trim().isEmpty()) {
            throw new ConcessionariaException("Il parametro '" + nomeParametro + "' non può essere vuoto");
        }
    }
    
    /**
     * Valida che un numero sia positivo
     */
    public static void requirePositive(double number, String nomeParametro) throws ConcessionariaException {
        if (number <= 0) {
            throw new ConcessionariaException("Il parametro '" + nomeParametro + "' deve essere maggiore di zero");
        }
    }
    
    /**
     * Valida che un numero sia in un range
     */
    public static void requireInRange(double number, double min, double max, String nomeParametro) throws ConcessionariaException {
        if (number < min || number > max) {
            throw new ConcessionariaException(
                String.format("Il parametro '%s' deve essere tra %.2f e %.2f", nomeParametro, min, max)
            );
        }
    }
    
    /**
     * Gestisce eccezioni durante operazioni I/O
     */
    public static void gestisciIOException(java.io.IOException e, String operazione) throws ConcessionariaException {
        logger.log(Level.SEVERE, "Errore I/O durante: " + operazione, e);
        
        String messaggio;
        if (e instanceof java.io.FileNotFoundException) {
            messaggio = "File non trovato";
        } else if (e instanceof java.nio.file.AccessDeniedException) {
            messaggio = "Accesso negato al file";
        } else if (e instanceof java.io.EOFException) {
            messaggio = "Fine del file raggiunta prematuramente";
        } else {
            messaggio = "Errore durante l'accesso ai dati";
        }
        
        throw new ConcessionariaException(messaggio, e);
    }
    
    /**
     * Esegue un'operazione con gestione automatica delle eccezioni
     */
    public static <T> T eseguiConGestioneErrori(java.util.concurrent.Callable<T> operazione, String descrizione) throws ConcessionariaException {
        try {
            return operazione.call();
        } catch (ConcessionariaException e) {
            // Rilancia le nostre eccezioni così come sono
            throw e;
        } catch (Exception e) {
            // Wrappa tutte le altre
            logger.log(Level.SEVERE, "Errore durante: " + descrizione, e);
            throw new ConcessionariaException("Errore durante l'operazione: " + descrizione, e);
        }
    }
}