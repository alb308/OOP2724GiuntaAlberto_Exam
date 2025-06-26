package com.concessionaria.service;

import com.concessionaria.model.*;
import com.concessionaria.composite.*;
import com.concessionaria.iterator.*;
import com.concessionaria.exception.*;
import com.concessionaria.util.ValidatoreInput;
import java.util.*;
import java.util.logging.Logger;

public class GestioneInventario implements InventarioIterabile {
    private static final Logger LOGGER = Logger.getLogger(GestioneInventario.class.getName());
    private List<Veicolo> veicoli;
    private CategoriaVeicoli catalogoPrincipale;
    private Map<String, CategoriaVeicoli> categorie;
    
    public GestioneInventario() {
        this.veicoli = new ArrayList<>();
        this.catalogoPrincipale = new CategoriaVeicoli("Catalogo Completo");
        this.categorie = new HashMap<>();
        inizializzaCategorie();
    }
    
    private void inizializzaCategorie() {
        categorie.put("AUTO", new CategoriaVeicoli("Automobili"));
        categorie.put("MOTO", new CategoriaVeicoli("Motociclette"));
        categorie.put("FURGONE", new CategoriaVeicoli("Furgoni"));
        
        // Aggiungi tutte le categorie al catalogo principale
        for (CategoriaVeicoli cat : categorie.values()) {
            catalogoPrincipale.aggiungi(cat);
        }
    }
    
    public void aggiungiVeicolo(Veicolo veicolo) throws ConcessionariaException {
        if (veicolo == null) {
            throw new ConcessionariaException("Il veicolo non può essere null");
        }
        
        // Validazione del veicolo
        ValidatoreInput.validaVeicolo(veicolo);
        
        veicoli.add(veicolo);
        
        // Aggiungi al composite pattern
        VeicoloFoglia foglia = new VeicoloFoglia(veicolo);
        String tipoVeicolo = veicolo.getClass().getSimpleName().toUpperCase();
        
        CategoriaVeicoli categoria = categorie.get(tipoVeicolo);
        if (categoria != null) {
            categoria.aggiungi(foglia);
        }
        
        LOGGER.info("Aggiunto veicolo: " + veicolo);
    }
    
    public void rimuoviVeicolo(String targa) throws ConcessionariaException {
        if (targa == null || targa.trim().isEmpty()) {
            throw new ConcessionariaException("La targa non può essere vuota");
        }
        
        Veicolo daRimuovere = null;
        for (Veicolo v : veicoli) {
            if (targa.equalsIgnoreCase(v.getTarga())) {
                daRimuovere = v;
                break;
            }
        }
        
        if (daRimuovere == null) {
            throw new ConcessionariaException("Veicolo con targa " + targa + " non trovato");
        }
        
        veicoli.remove(daRimuovere);
        
        // Rimuovi anche dal composite
        rimuoviDalCatalogo(daRimuovere);
        
        LOGGER.info("Rimosso veicolo con targa: " + targa);
    }
    
    private void rimuoviDalCatalogo(Veicolo veicolo) {
        String tipoVeicolo = veicolo.getClass().getSimpleName().toUpperCase();
        CategoriaVeicoli categoria = categorie.get(tipoVeicolo);
        
        if (categoria != null) {
            List<ComponenteCatalogo> componenti = categoria.getComponenti();
            ComponenteCatalogo daRimuovere = null;
            
            for (ComponenteCatalogo comp : componenti) {
                if (comp instanceof VeicoloFoglia) {
                    VeicoloFoglia foglia = (VeicoloFoglia) comp;
                    if (foglia.getVeicolo().equals(veicolo)) {
                        daRimuovere = comp;
                        break;
                    }
                }
            }
            
            if (daRimuovere != null) {
                categoria.rimuovi(daRimuovere);
            }
        }
    }
    
    public List<Veicolo> getVeicoli() {
        return new ArrayList<>(veicoli);
    }
    
    public int getNumeroVeicoli() {
        return veicoli.size();
    }
    
    public CategoriaVeicoli getCatalogoPrincipale() {
        return catalogoPrincipale;
    }
    
    // Implementazione Iterator Pattern
    @Override
    public IteratoreInventario creaIteratore() {
        return new IteratoreVeicoliConcreto(veicoli);
    }
    
    @Override
    public IteratoreInventario creaIteratorePerTipo(String tipo) {
        return new IteratoreFiltrato(veicoli, v -> 
            v.getClass().getSimpleName().equalsIgnoreCase(tipo));
    }
    
    @Override
    public IteratoreInventario creaIteratorePerPrezzo(double prezzoMin, double prezzoMax) {
        return new IteratoreFiltrato(veicoli, v -> 
            v.getPrezzo() >= prezzoMin && v.getPrezzo() <= prezzoMax);
    }
    
    public Veicolo cercaPerTarga(String targa) {
        for (Veicolo v : veicoli) {
            if (v.getTarga() != null && v.getTarga().equalsIgnoreCase(targa)) {
                return v;
            }
        }
        return null;
    }
}// File: GestioneInventario.java
package com.concessionaria.service;

import com.concessionaria.model.*;
import com.concessionaria.composite.*;
import com.concessionaria.iterator.*;
import com.concessionaria.exception.*;
import com.concessionaria.util.ValidatoreInput;
import java.util.*;
import java.util.logging.Logger;

public class GestioneInventario implements InventarioIterabile {
    private static final Logger LOGGER = Logger.getLogger(GestioneInventario.class.getName());
    private List<Veicolo> veicoli;
    private CategoriaVeicoli catalogoPrincipale;
    private Map<String, CategoriaVeicoli> categorie;
    
    public GestioneInventario() {
        this.veicoli = new ArrayList<>();
        this.catalogoPrincipale = new CategoriaVeicoli("Catalogo Completo");
        this.categorie = new HashMap<>();
        inizializzaCategorie();
    }
    
    private void inizializzaCategorie() {
        categorie.put("AUTO", new CategoriaVeicoli("Automobili"));
        categorie.put("MOTO", new CategoriaVeicoli("Motociclette"));
        categorie.put("FURGONE", new CategoriaVeicoli("Furgoni"));
        
        // Aggiungi tutte le categorie al catalogo principale
        for (CategoriaVeicoli cat : categorie.values()) {
            catalogoPrincipale.aggiungi(cat);
        }
    }
    
    public void aggiungiVeicolo(Veicolo veicolo) throws ConcessionariaException {
        if (veicolo == null) {
            throw new ConcessionariaException("Il veicolo non può essere null");
        }
        
        // Validazione del veicolo
        ValidatoreInput.validaVeicolo(veicolo);
        
        veicoli.add(veicolo);
        
        // Aggiungi al composite pattern
        VeicoloF