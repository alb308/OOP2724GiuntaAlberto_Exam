package com.concessionaria.model;

public class Furgone extends Veicolo {
    private double capacitaCarico; 
    private boolean cassoneChiuso;
    
    public Furgone(String marca, String modello, int anno, double prezzo) {
        super(marca, modello, anno, prezzo);
        this.capacitaCarico = 10.0; 
        this.cassoneChiuso = true; 
    }
    
    public double getCapacitaCarico() {
        return capacitaCarico;
    }
    
    public void setCapacitaCarico(double capacitaCarico) {
        this.capacitaCarico = capacitaCarico;
    }
    
    public boolean isCassoneChiuso() {
        return cassoneChiuso;
    }
    
    public void setCassoneChiuso(boolean cassoneChiuso) {
        this.cassoneChiuso = cassoneChiuso;
    }
}