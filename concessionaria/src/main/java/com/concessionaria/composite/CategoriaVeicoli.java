package com.concessionaria.composite;
import java.util.*;

public class CategoriaVeicoli implements ComponenteCatalogo {
    private String nome;
    private List<ComponenteCatalogo> componenti;
    
    public CategoriaVeicoli(String nome) {
        this.nome = nome;
        this.componenti = new ArrayList<>();
    }
    
    public void aggiungi(ComponenteCatalogo componente) {
        componenti.add(componente);
    }
    
    public void rimuovi(ComponenteCatalogo componente) {
        componenti.remove(componente);
    }
    
    @Override
    public String getNome() {
        return nome;
    }
    
    @Override
    public double getPrezzoTotale() {
        double totale = 0;
        for (ComponenteCatalogo comp : componenti) {
            totale += comp.getPrezzoTotale();
        }
        return totale;
    }
    
    @Override
    public void visualizza() {
        System.out.println("\n=== Categoria: " + nome + " ===");
        System.out.println("Valore totale inventario: €" + String.format("%.2f", getPrezzoTotale()));
        for (ComponenteCatalogo comp : componenti) {
            comp.visualizza();
        }
    }
    
    public List<ComponenteCatalogo> getComponenti() {
        return new ArrayList<>(componenti);
    }
}