package com.concessionaria.iterator;

import com.concessionaria.model.Veicolo;

public interface IteratoreInventario {
    boolean hasNext();
    Veicolo next();
    void reset();
}