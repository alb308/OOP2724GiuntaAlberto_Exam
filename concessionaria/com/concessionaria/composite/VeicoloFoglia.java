package com.concessionaria.composite;

import com.concessionaria.model.Veicolo;

public class VeicoloFoglia implements ComponenteCatalogo {
    private Veicolo veicolo;
    
    public VeicoloFoglia(Veicolo veicolo) {
        this.veicolo = veicolo;
    }
    
    @Override
    public String getNome() {
        return veicolo.getMarca() + " " + veicolo.getModello();
    }
    
    @Override
    public double getPrezzoTotale() {
        return veicolo.getPrezzo();
    }
    
    @Override
    public void visualizza() {
        System.out.println("  - " + veicolo);
    }
    
    public Veicolo getVeicolo() {
        return veicolo;
    }
}