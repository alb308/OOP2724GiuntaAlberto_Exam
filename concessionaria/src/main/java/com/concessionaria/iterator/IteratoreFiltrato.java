package com.concessionaria.iterator;

import com.concessionaria.model.Veicolo;
import java.util.*;
import java.util.function.Predicate;

public class IteratoreFiltrato implements IteratoreInventario {
    private List<Veicolo> veicoli;
    private Predicate<Veicolo> filtro;
    private List<Veicolo> veicoliFiltrati;
    private int posizioneCorrente;
    
    public IteratoreFiltrato(List<Veicolo> veicoli, Predicate<Veicolo> filtro) {
        this.veicoli = new ArrayList<>(veicoli);
        this.filtro = filtro;
        this.veicoliFiltrati = new ArrayList<>();
        this.posizioneCorrente = 0;
        applicaFiltro();
    }
    
    private void applicaFiltro() {
        for (Veicolo v : veicoli) {
            if (filtro.test(v)) {
                veicoliFiltrati.add(v);
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return posizioneCorrente < veicoliFiltrati.size();
    }
    
    @Override
    public Veicolo next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Non ci sono più elementi");
        }
        return veicoliFiltrati.get(posizioneCorrente++);
    }
    
    @Override
    public void reset() {
        posizioneCorrente = 0;
    }
}