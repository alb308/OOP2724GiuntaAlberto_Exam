package com.concessionaria.model;

import java.io.Serializable;

public abstract class Veicolo implements Serializable {
    private String marca;
    private String modello;
    private int anno;
    private double prezzo;
    private String targa;
    
    public Veicolo(String marca, String modello, int anno, double prezzo) {
        this.marca = marca;
        this.modello = modello;
        this.anno = anno;
        this.prezzo = prezzo;
    }
    
    public String getMarca() {
        return marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    public String getModello() {
        return modello;
    }
    
    public void setModello(String modello) {
        this.modello = modello;
    }
    
    public int getAnno() {
        return anno;
    }
    
    public void setAnno(int anno) {
        this.anno = anno;
    }
    
    public double getPrezzo() {
        return prezzo;
    }
    
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }
    
    public String getTarga() {
        return targa;
    }
    
    public void setTarga(String targa) {
        this.targa = targa;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s (%d) - €%.2f - Targa: %s",
            getClass().getSimpleName(), marca, modello, anno, prezzo, 
            targa != null ? targa : "Non assegnata");
    }
}