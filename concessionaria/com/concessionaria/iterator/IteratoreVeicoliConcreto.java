package com.concessionaria.iterator;

import com.concessionaria.model.Veicolo;
import java.util.*;

public class IteratoreVeicoliConcreto implements IteratoreInventario {
    private List<Veicolo> veicoli;
    private int posizioneCorrente;
    
    public IteratoreVeicoliConcreto(List<Veicolo> veicoli) {
        this.veicoli = new ArrayList<>(veicoli);
        this.posizioneCorrente = 0;
    }
    
    @Override
    public boolean hasNext() {
        return posizioneCorrente < veicoli.size();
    }
    
    @Override
    public Veicolo next() {
        if (!hasNext()) {
            throw new NoSuch
