package com.concessionaria.test;

import com.concessionaria.service.GestioneInventario;
import com.concessionaria.model.*;
import com.concessionaria.exception.ConcessionariaException;
import com.concessionaria.iterator.IteratoreInventario;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class GestioneInventarioTest {
    private GestioneInventario inventario;
    
    @Before
    public void setUp() {
        inventario = new GestioneInventario();
    }
    
    @Test
    public void testAggiungiVeicolo() throws ConcessionariaException {
        Auto auto = new Auto("Toyota", "Yaris", 2023, 20000);
        auto.setTarga("AB123CD");
        inventario.aggiungiVeicolo(auto);
        
        assertEquals(1, inventario.getNumeroVeicoli());
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testAggiungiVeicoloNull() throws ConcessionariaException {
        inventario.aggiungiVeicolo(null);
    }
    
    @Test
    public void testIteratore() throws ConcessionariaException {
        Auto auto1 = new Auto("Fiat", "500", 2023, 18000);
        auto1.setTarga("AA111BB");
        Auto auto2 = new Auto("Ford", "Focus", 2023, 25000);
        auto2.setTarga("CC222DD");
        
        inventario.aggiungiVeicolo(auto1);
        inventario.aggiungiVeicolo(auto2);
        
        IteratoreInventario it = inventario.creaIteratore();
        int count = 0;
        while (it.hasNext()) {
            assertNotNull(it.next());
            count++;
        }
        assertEquals(2, count);
    }
    
    @Test
    public void testRicercaPerTipo() throws ConcessionariaException {
        Auto auto = new Auto("Audi", "A3", 2023, 35000);
        auto.setTarga("EE333FF");
        Moto moto = new Moto("Honda", "CBR", 2023, 12000);
        moto.setTarga("GG444HH");
        
        inventario.aggiungiVeicolo(auto);
        inventario.aggiungiVeicolo(moto);
        
        IteratoreInventario it = inventario.creaIteratorePerTipo("AUTO");
        int count = 0;
        while (it.hasNext()) {
            Veicolo v = it.next();
            assertTrue(v instanceof Auto);
            count++;
        }
        assertEquals(1, count);
    }
}