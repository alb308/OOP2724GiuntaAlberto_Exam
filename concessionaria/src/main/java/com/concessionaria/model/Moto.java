package com.concessionaria.model;

public class Moto extends Veicolo {
    private int cilindrata;
    private String tipoMoto;
    
    public Moto(String marca, String modello, int anno, double prezzo) {
        super(marca, modello, anno, prezzo);
        this.cilindrata = 600; // default
        this.tipoMoto = "Naked"; // default
    }
    
    public int getCilindrata() {
        return cilindrata;
    }
    
    public void setCilindrata(int cilindrata) {
        this.cilindrata = cilindrata;
    }
    
    public String getTipoMoto() {
        return tipoMoto;
    }
    
    public void setTipoMoto(String tipoMoto) {
        this.tipoMoto = tipoMoto;
    }
}