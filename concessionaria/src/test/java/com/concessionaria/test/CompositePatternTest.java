package com.concessionaria.test;

import com.concessionaria.composite.*;
import com.concessionaria.model.Auto;
import com.concessionaria.model.Moto;
import com.concessionaria.model.Furgone;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class CompositePatternTest {
    
    @Test
    public void testVeicoloFogliaCreazione() {
        Auto auto = new Auto("Fiat", "Panda", 2023, 15000);
        auto.setTarga("AB123CD");
        
        VeicoloFoglia foglia = new VeicoloFoglia(auto);
        
        assertNotNull(foglia);
        assertEquals("Fiat Panda", foglia.getNome());
        assertEquals(15000, foglia.getPrezzoTotale(), 0.01);
        assertEquals(auto, foglia.getVeicolo());
    }
    
    @Test
    public void testVeicoloFogliaVisualizza() {
        Moto moto = new Moto("Honda", "CBR", 2023, 12000);
        moto.setTarga("EF456GH");
        
        VeicoloFoglia foglia = new VeicoloFoglia(moto);
        
        try {
            foglia.visualizza();
        } catch (Exception e) {
            fail("visualizza() non dovrebbe lanciare eccezioni");
        }
    }
    
    @Test
    public void testVeicoloFogliaConDiversiTipi() {
        Auto auto = new Auto("Toyota", "Yaris", 2023, 20000);
        auto.setTarga("AA111BB");
        VeicoloFoglia fogliaAuto = new VeicoloFoglia(auto);
        
        assertEquals("Toyota Yaris", fogliaAuto.getNome());
        assertEquals(20000, fogliaAuto.getPrezzoTotale(), 0.01);
        
        Moto moto = new Moto("Yamaha", "R1", 2023, 18000);
        moto.setTarga("CC222DD");
        VeicoloFoglia fogliaMoto = new VeicoloFoglia(moto);
        
        assertEquals("Yamaha R1", fogliaMoto.getNome());
        assertEquals(18000, fogliaMoto.getPrezzoTotale(), 0.01);
        
        Furgone furgone = new Furgone("Iveco", "Daily", 2023, 35000);
        furgone.setTarga("EE333FF");
        VeicoloFoglia fogliaFurgone = new VeicoloFoglia(furgone);
        
        assertEquals("Iveco Daily", fogliaFurgone.getNome());
        assertEquals(35000, fogliaFurgone.getPrezzoTotale(), 0.01);
    }
    
    @Test
    public void testComponenteCatalogoInterface() {
        Auto auto = new Auto("Ford", "Fiesta", 2023, 22000);
        auto.setTarga("GG444HH");
        
        ComponenteCatalogo componente = new VeicoloFoglia(auto);
        
        assertNotNull(componente.getNome());
        assertTrue(componente.getPrezzoTotale() > 0);
        
        try {
            componente.visualizza();
        } catch (Exception e) {
            fail("visualizza() attraverso l'interfaccia non dovrebbe lanciare eccezioni");
        }
    }
    
    @Test
    public void testVeicoloFogliaConVeicoloModificato() {
        Auto auto = new Auto("Renault", "Clio", 2023, 17000);
        auto.setTarga("II555JJ");
        
        VeicoloFoglia foglia = new VeicoloFoglia(auto);
        
        assertEquals(17000, foglia.getPrezzoTotale(), 0.01);
        
        auto.setPrezzo(19000);
        
        assertEquals(19000, foglia.getPrezzoTotale(), 0.01);
    }
}