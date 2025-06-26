// File: ValidatoreInput.java
package com.concessionaria.util;

import com.concessionaria.model.Veicolo;
import com.concessionaria.exception.ConcessionariaException;
import java.util.Calendar;
import java.util.regex.Pattern;

public class ValidatoreInput {
    
    // Pattern per targa italiana (es. AB123CD)
    private static final Pattern PATTERN_TARGA = Pattern.compile("^[A-Z]{2}[0-9]{3}[A-Z]{2}$");
    
    // Sanitizza stringa rimuovendo caratteri pericolosi
    public static String sanitizzaStringa(String input) {
        if (input == null) return "";
        
        // Rimuovi caratteri potenzialmente pericolosi
        return input.replaceAll("[<>\"'&;]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    // Valida formato targa
    public static boolean validaTarga(String targa) {
        if (targa == null || targa.isEmpty()) {
            return false;
        }
        return PATTERN_TARGA.matcher(targa).matches();
    }
    
    // Valida anno veicolo
    public static boolean validaAnno(int anno) {
        int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
        return anno >= 1900 && anno <= annoCorrente + 1;
    }
    
    // Valida prezzo
    public static boolean validaPrezzo(double prezzo) {
        return prezzo > 0 && prezzo < 10000000; // Max 10 milioni
    }
    
    // Valida veicolo completo
    public static void validaVeicolo(Veicolo veicolo) throws ConcessionariaException {
        if (veicolo == null) {
            throw new ConcessionariaException("Il veicolo non può essere null");
        }
        
        // Valida marca
        if (veicolo.getMarca() == null || veicolo.getMarca().trim().isEmpty()) {
            throw new ConcessionariaException("La marca non può essere vuota");
        }
        
        // Valida modello
        if (veicolo.getModello() == null || veicolo.getModello().trim().isEmpty()) {
            throw new ConcessionariaException("Il modello non può essere vuoto");
        }
        
        // Valida anno
        if (!validaAnno(veicolo.getAnno())) {
            throw new ConcessionariaException("Anno non valido: " + veicolo.getAnno());
        }
        
        // Valida prezzo
        if (!validaPrezzo(veicolo.getPrezzo())) {
            throw new ConcessionariaException("Prezzo non valido: " + veicolo.getPrezzo());
        }
        
        // Valida targa se presente
        if (veicolo.getTarga() != null && !veicolo.getTarga().isEmpty() 
            && !validaTarga(veicolo.getTarga())) {
            throw new ConcessionariaException("Formato targa non valido: " + veicolo.getTarga());
        }
    }
    
    // Valida input numerico
    public static boolean validaNumeroIntero(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Valida input decimale
    public static boolean validaNumeroDecimale(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}