// File: VeicoloFactory.java
package com.concessionaria.factory;

import com.concessionaria.model.*;
import com.concessionaria.exception.ConcessionariaException;
import java.util.logging.Logger;

public class VeicoloFactory {
    private static final Logger LOGGER = Logger.getLogger(VeicoloFactory.class.getName());
    
    public static Veicolo creaVeicolo(String tipo, String marca, String modello, 
                                      int anno, double prezzo) throws ConcessionariaException {
        // Validazione input
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new ConcessionariaException("Il tipo di veicolo non può essere vuoto");
        }
        
        tipo = tipo.toUpperCase().trim();
        LOGGER.info("Creazione veicolo di tipo: " + tipo);
        
        switch (tipo) {
            case "AUTO":
                return new Auto(marca, modello, anno, prezzo);
            case "MOTO":
                return new Moto(marca, modello, anno, prezzo);
            case "FURGONE":
                return new Furgone(marca, modello, anno, prezzo);
            default:
                throw new ConcessionariaException("Tipo di veicolo non supportato: " + tipo);
        }
    }
}