// Aggiunte da fare al tuo GestioneInventario.java esistente

// Modifica il metodo aggiungiVeicolo per aggiungere più logging:
public void aggiungiVeicolo(Veicolo veicolo) throws ConcessionariaException {
    if (veicolo == null) {
        LOGGER.warning("Tentativo di aggiungere veicolo null");
        throw new ConcessionariaException("Il veicolo non può essere null");
    }
    
    // Controlla targa duplicata
    if (veicolo.getTarga() != null) {
        for (Veicolo v : veicoli) {
            if (v.getTarga() != null && v.getTarga().equalsIgnoreCase(veicolo.getTarga())) {
                LOGGER.warning("Tentativo di aggiungere veicolo con targa duplicata: " + veicolo.getTarga());
                throw new ConcessionariaException("Targa già esistente: " + veicolo.getTarga());
            }
        }
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
    
    // LOGGING DETTAGLIATO
    LOGGER.info("Veicolo aggiunto con successo - Targa: " + veicolo.getTarga() + 
                ", Tipo: " + tipoVeicolo + ", Marca: " + veicolo.getMarca() + 
                ", Modello: " + veicolo.getModello());
}

// Modifica il metodo rimuoviVeicolo per aggiungere più logging:
public void rimuoviVeicolo(String targa) throws ConcessionariaException {
    if (targa == null || targa.trim().isEmpty()) {
        LOGGER.warning("Tentativo di rimuovere veicolo con targa null o vuota");
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
        LOGGER.warning("Tentativo di rimuovere veicolo non esistente: " + targa);
        throw new ConcessionariaException("Veicolo con targa " + targa + " non trovato");
    }
    
    veicoli.remove(daRimuovere);
    
    // Rimuovi anche dal composite
    rimuoviDalCatalogo(daRimuovere);
    
    // LOGGING DETTAGLIATO
    LOGGER.info("Veicolo rimosso con successo - Targa: " + targa + 
                ", Tipo: " + daRimuovere.getClass().getSimpleName() + 
                ", Marca: " + daRimuovere.getMarca());
}

// Aggiungi metodo per statistiche (utile per sicurezza/monitoring)
public Map<String, Integer> getStatistiche() {
    Map<String, Integer> stats = new HashMap<>();
    stats.put("totale", veicoli.size());
    
    int auto = 0, moto = 0, furgoni = 0;
    for (Veicolo v : veicoli) {
        if (v instanceof Auto) auto++;
        else if (v instanceof Moto) moto++;
        else if (v instanceof Furgone) furgoni++;
    }
    
    stats.put("auto", auto);
    stats.put("moto", moto);  
    stats.put("furgoni", furgoni);
    
    LOGGER.info("Statistiche generate: " + stats);
    return stats;
}