package com.concessionaria.test;

import com.concessionaria.iterator.*;
import com.concessionaria.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class IteratorTest {
    private List<Veicolo> veicoli;
    
    @Before
    public void setUp() {
        veicoli = new ArrayList<>();
        
        Auto auto = new Auto("Fiat", "Panda", 2023, 15000);
        auto.setTarga("AB123CD");
        
        Moto moto = new Moto("Honda", "CBR", 2023, 12000);
        moto.setTarga("EF456GH");
        
        Furgone furgone = new Furgone("Iveco", "Daily", 2023, 35000);
        furgone.setTarga("IJ789KL");
        
        veicoli.add(auto);
        veicoli.add(moto);
        veicoli.add(furgone);
    }
    
    @Test
    public void testIteratoreVeicoliConcreto() {
        IteratoreInventario iterator = new IteratoreVeicoliConcreto(veicoli);
        
        assertTrue(iterator.hasNext());
        
        int count = 0;
        while (iterator.hasNext()) {
            Veicolo v = iterator.next();
            assertNotNull(v);
            count++;
        }
        
        assertEquals(3, count);
        assertFalse(iterator.hasNext());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testIteratoreNextSuVuoto() {
        List<Veicolo> veicoliVuoti = new ArrayList<>();
        IteratoreInventario iterator = new IteratoreVeicoliConcreto(veicoliVuoti);
        
        assertFalse(iterator.hasNext());
        iterator.next(); // Dovrebbe lanciare eccezione
    }
    
    @Test
    public void testIteratoreReset() {
        IteratoreInventario iterator = new IteratoreVeicoliConcreto(veicoli);
        
        // Prima iterazione
        int count1 = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count1++;
        }
        assertEquals(3, count1);
        assertFalse(iterator.hasNext());
        
        // Reset
        iterator.reset();
        assertTrue(iterator.hasNext());
        
        // Seconda iterazione
        int count2 = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count2++;
        }
        assertEquals(3, count2);
    }
    
    @Test
    public void testIteratoreFiltrato() {
        // Filtra solo le auto
        IteratoreInventario iterator = new IteratoreFiltrato(veicoli, 
            v -> v instanceof Auto);
        
        assertTrue(iterator.hasNext());
        
        int count = 0;
        while (iterator.hasNext()) {
            Veicolo v = iterator.next();
            assertTrue("Dovrebbe essere un'auto", v instanceof Auto);
            count++;
        }
        
        assertEquals(1, count); // Solo 1 auto nella lista
    }
    
    @Test
    public void testIteratoreFiltratoPerPrezzo() {
        // Filtra veicoli con prezzo > 20000
        IteratoreInventario iterator = new IteratoreFiltrato(veicoli, 
            v -> v.getPrezzo() > 20000);
        
        int count = 0;
        while (iterator.hasNext()) {
            Veicolo v = iterator.next();
            assertTrue("Il prezzo dovrebbe essere > 20000", v.getPrezzo() > 20000);
            count++;
        }
        
        assertEquals(1, count); // Solo il furgone (35000)
    }
    
    @Test
    public void testIteratoreFiltratoNessunRisultato() {
        // Filtra veicoli con prezzo > 100000 (nessuno)
        IteratoreInventario iterator = new IteratoreFiltrato(veicoli, 
            v -> v.getPrezzo() > 100000);
        
        assertFalse(iterator.hasNext());
        
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        
        assertEquals(0, count);
    }
    
    @Test
    public void testIteratoreFiltratoReset() {
        IteratoreInventario iterator = new IteratoreFiltrato(veicoli, 
            v -> v instanceof Auto || v instanceof Moto);
        
        // Prima iterazione
        int count1 = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count1++;
        }
        assertEquals(2, count1); // Auto + Moto
        
        // Reset
        iterator.reset();
        assertTrue(iterator.hasNext());
        
        // Seconda iterazione
        int count2 = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count2++;
        }
        assertEquals(2, count2);
    }
    
    @Test
    public void testIteratoreListaVuota() {
        List<Veicolo> veicoliVuoti = new ArrayList<>();
        IteratoreInventario iterator = new IteratoreVeicoliConcreto(veicoliVuoti);
        
        assertFalse(iterator.hasNext());
        
        // Reset su lista vuota
        iterator.reset();
        assertFalse(iterator.hasNext());
    }
}