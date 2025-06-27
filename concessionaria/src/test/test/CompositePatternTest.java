package com.concessionaria.test;

import com.concessionaria.composite.*;
import com.concessionaria.model.Auto;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class CompositePatternTest {
    private CategoriaVeicoli catalogoPrincipale;
    
    @Before
    public void setUp() {
        catalogoPrincipale = new CategoriaVeicoli("Catalogo");
    }
    
    @Test
    public void testAggiungiCategoria() {
        CategoriaVeicoli auto = new CategoriaVeicoli("Auto");
        catalogoPrincipale.aggiungi(auto);
        
        assertEquals(1, catalogoPrincipale.getComponenti().size());
    }
    
    @Test
    public void testCalcoloPrezzoTotale() {
        CategoriaVeicoli auto = new CategoriaVeicoli("Auto");
        
        Auto a1 = new Auto("Fiat", "Panda", 2023, 15000);
        Auto a2 = new Auto("Ford", "Fiesta", 2023, 20000);
        
        auto.aggiungi(new VeicoloFoglia(a1));
        auto.aggiungi(new VeicoloFoglia(a2));
        catalogoPrincipale.aggiungi(auto);
        
        assertEquals(35000, catalogoPrincipale.getPrezzoTotale(), 0.01);
    }
}