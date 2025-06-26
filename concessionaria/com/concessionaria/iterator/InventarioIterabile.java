package com.concessionaria.iterator;

public interface InventarioIterabile {
    IteratoreInventario creaIteratore();
    IteratoreInventario creaIteratorePerTipo(String tipo);
    IteratoreInventario creaIteratorePerPrezzo(double prezzoMin, double prezzoMax);
}