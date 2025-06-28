package com.concessionaria.exception;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;

public class ExceptionHandler {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());
    
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
    
    public static String gestisciEccezione(Exception e, String contesto) {

        logger.log(Level.SEVERE, "Eccezione in " + contesto, e);
        
        logContextInfo(e, contesto);
        
        return getMessaggioSicuro(e);
    }
    
    public static void gestisciEMostra(Exception e, String contesto) {
        String messaggio = gestisciEccezione(e, contesto);
        System.err.println("\n⚠️  " + messaggio);
    }

    private static String getMessaggioSicuro(Exception e) {
        if (e instanceof ConcessionariaException) {
            return e.getMessage();
        }
        
        String messaggio = EXCEPTION_MESSAGES.get(e.getClass());
        if (messaggio != null) {
            return messaggio;
        }
        
        Class<?> clazz = e.getClass().getSuperclass();
        while (clazz != null && clazz != Object.class) {
            messaggio = EXCEPTION_MESSAGES.get(clazz);
            if (messaggio != null) {
                return messaggio;
            }
            clazz = clazz.getSuperclass();
        }
        

        return "Si è verificato un errore imprevisto. Riprova più tardi.";
    }
    
    private static void logContextInfo(Exception e, String contesto) {
        logger.severe("=== DETTAGLI ECCEZIONE ===");
        logger.severe("Contesto: " + contesto);
        logger.severe("Tipo eccezione: " + e.getClass().getName());
        logger.severe("Messaggio originale: " + e.getMessage());
        

        if (!(e instanceof ConcessionariaException)) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                logger.severe("Origine: " + stackTrace[0].toString());
            }
        }
        

        Throwable causa = e.getCause();
        if (causa != null) {
            logger.severe("Causa: " + causa.getClass().getName() + " - " + causa.getMessage());
        }
        
        logger.severe("========================");
    }
    

    public static ConcessionariaException wrapException(Exception e, String messaggioUtente) {
        logger.log(Level.WARNING, "Wrapping eccezione tecnica", e);
        return new ConcessionariaException(messaggioUtente, e);
    }


    public static void requireNonNull(Object obj, String nomeParametro) throws ConcessionariaException {
        if (obj == null) {
            throw new ConcessionariaException("Il parametro '" + nomeParametro + "' è obbligatorio");
        }
    }
    

    public static void requireNonEmpty(String str, String nomeParametro) throws ConcessionariaException {
        if (str == null || str.trim().isEmpty()) {
            throw new ConcessionariaException("Il parametro '" + nomeParametro + "' non può essere vuoto");
        }
    }

    public static void requirePositive(double number, String nomeParametro) throws ConcessionariaException {
        if (number <= 0) {
            throw new ConcessionariaException("Il parametro '" + nomeParametro + "' deve essere maggiore di zero");
        }
    }
    
    public static void requireInRange(double number, double min, double max, String nomeParametro) throws ConcessionariaException {
        if (number < min || number > max) {
            throw new ConcessionariaException(
                String.format("Il parametro '%s' deve essere tra %.2f e %.2f", nomeParametro, min, max)
            );
        }
    }
    
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
    
   public static <T> T eseguiConGestioneErrori(java.util.concurrent.Callable<T> operazione, String descrizione) throws ConcessionariaException {
        try {
            return operazione.call();
        } catch (ConcessionariaException e) {
            throw e;
        } catch (Exception e) {

            logger.log(Level.SEVERE, "Errore durante: " + descrizione, e);
            throw new ConcessionariaException("Errore durante l'operazione: " + descrizione, e);
        }
    }
}