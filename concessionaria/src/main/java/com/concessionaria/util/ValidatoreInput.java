package com.concessionaria.util;

import com.concessionaria.model.Veicolo;
import com.concessionaria.exception.ConcessionariaException;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ValidatoreInput {
    private static final Logger LOGGER = Logger.getLogger(ValidatoreInput.class.getName());
    
    private static final Pattern PATTERN_TARGA = Pattern.compile("^[A-Z]{2}[0-9]{3}[A-Z]{2}$");
    
    public static String sanitizzaStringa(String input) {
        if (input == null) return "";
        
        String original = input;
        String sanitized = input.replaceAll("[<>\"'&;]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
        
        if (!sanitized.equals(original)) {
            LOGGER.warning("Input sanitizzato da: '" + original + "' a: '" + sanitized + "'");
        }
        
        return sanitized;
    }
    
    public static boolean validaTarga(String targa) {
        if (targa == null || targa.isEmpty()) {
            LOGGER.warning("Tentativo di validare targa null o vuota");
            return false;
        }
        boolean valida = PATTERN_TARGA.matcher(targa).matches();
        if (!valida) {
            LOGGER.warning("Targa non valida: " + targa);
        }
        return valida;
    }
    
    public static boolean validaAnno(int anno) {
        int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
        boolean valido = anno >= 1900 && anno <= annoCorrente + 1;
        if (!valido) {
            LOGGER.warning("Anno non valido: " + anno);
        }
        return valido;
    }
    
    public static boolean validaPrezzo(double prezzo) {
        boolean valido = prezzo > 0 && prezzo < 10000000;
        if (!valido) {
            LOGGER.warning("Prezzo non valido: " + prezzo);
        }
        return valido;
    }
    
    public static void validaVeicolo(Veicolo veicolo) throws ConcessionariaException {
        if (veicolo == null) {
            LOGGER.severe("Tentativo di validare veicolo null");
            throw new ConcessionariaException("Il veicolo non può essere null");
        }
        
        if (veicolo.getMarca() == null || veicolo.getMarca().trim().isEmpty()) {
            LOGGER.warning("Marca vuota o null");
            throw new ConcessionariaException("La marca non può essere vuota");
        }
        
        if (veicolo.getModello() == null || veicolo.getModello().trim().isEmpty()) {
            LOGGER.warning("Modello vuoto o null");
            throw new ConcessionariaException("Il modello non può essere vuoto");
        }
        
        if (!validaAnno(veicolo.getAnno())) {
            throw new ConcessionariaException("Anno non valido: " + veicolo.getAnno());
        }
        
        if (!validaPrezzo(veicolo.getPrezzo())) {
            throw new ConcessionariaException("Prezzo non valido: " + veicolo.getPrezzo());
        }
        
        if (veicolo.getTarga() != null && !veicolo.getTarga().isEmpty() 
            && !validaTarga(veicolo.getTarga())) {
            throw new ConcessionariaException("Formato targa non valido: " + veicolo.getTarga());
        }
        
        LOGGER.info("Veicolo validato con successo: " + veicolo.getTarga());
    }
    
    public static boolean validaNumeroIntero(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            LOGGER.warning("Input non numerico: " + input);
            return false;
        }
    }
    
    public static boolean validaNumeroDecimale(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            LOGGER.warning("Input decimale non valido: " + input);
            return false;
        }
    }
}