package com.concessionaria.model;

public class Auto extends Veicolo {
    private int numeroPorte;
    private String tipoCambio;
    
    public Auto(String marca, String modello, int anno, double prezzo) {
        super(marca, modello, anno, prezzo);
        this.numeroPorte = 5; 
        this.tipoCambio = "Manuale"; 
    }
    
    public int getNumeroPorte() {
        return numeroPorte;
    }
    
    public void setNumeroPorte(int numeroPorte) {
        if (numeroPorte == 3 || numeroPorte == 5) {
            this.numeroPorte = numeroPorte;
        } else {
            throw new IllegalArgumentException("Numero porte deve essere 3 o 5");
        }
    }
    
    public String getTipoCambio() {
        return tipoCambio;
    }
    
    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    
    @Override
    public String toString() {
        return super.toString() + 
               String.format(" [Porte: %d, Cambio: %s]", numeroPorte, tipoCambio);
    }
}