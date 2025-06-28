package com.concessionaria.test;

import com.concessionaria.model.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ModelTest {
    
    @Test
    public void testAutoCreazione() {
        Auto auto = new Auto("Fiat", "Panda", 2023, 15000);
        
        assertEquals("Fiat", auto.getMarca());
        assertEquals("Panda", auto.getModello());
        assertEquals(2023, auto.getAnno());
        assertEquals(15000, auto.getPrezzo(), 0.01);
        assertEquals(5, auto.getNumeroPorte()); 
        assertEquals("Manuale", auto.getTipoCambio()); 
    }
    
    @Test
    public void testAutoSetters() {
        Auto auto = new Auto("Toyota", "Yaris", 2023, 18000);
        
        auto.setTarga("AB123CD");
        auto.setNumeroPorte(3);
        auto.setTipoCambio("Automatico");
        
        assertEquals("AB123CD", auto.getTarga());
        assertEquals(3, auto.getNumeroPorte());
        assertEquals("Automatico", auto.getTipoCambio());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAutoPorteNonValide() {
        Auto auto = new Auto("Fiat", "500", 2023, 16000);
        auto.setNumeroPorte(4); 
    }
    
    @Test
    public void testMotoCreazione() {
        Moto moto = new Moto("Honda", "CBR", 2023, 12000);
        
        assertEquals("Honda", moto.getMarca());
        assertEquals("CBR", moto.getModello());
        assertEquals(2023, moto.getAnno());
        assertEquals(12000, moto.getPrezzo(), 0.01);
        assertEquals(600, moto.getCilindrata()); 
        assertEquals("Naked", moto.getTipoMoto());
    }
    
    @Test
    public void testMotoSetters() {
        Moto moto = new Moto("Yamaha", "R1", 2023, 18000);
        
        moto.setTarga("EF456GH");
        moto.setCilindrata(1000);
        moto.setTipoMoto("Sport");
        
        assertEquals("EF456GH", moto.getTarga());
        assertEquals(1000, moto.getCilindrata());
        assertEquals("Sport", moto.getTipoMoto());
    }
    
    @Test
    public void testFurgoneCreazione() {
        Furgone furgone = new Furgone("Iveco", "Daily", 2023, 35000);
        
        assertEquals("Iveco", furgone.getMarca());
        assertEquals("Daily", furgone.getModello());
        assertEquals(2023, furgone.getAnno());
        assertEquals(35000, furgone.getPrezzo(), 0.01);
        assertEquals(10.0, furgone.getCapacitaCarico(), 0.01); 
        assertTrue(furgone.isCassoneChiuso()); 
    }
    
    @Test
    public void testFurgoneSetters() {
        Furgone furgone = new Furgone("Mercedes", "Sprinter", 2023, 45000);
        
        furgone.setTarga("IJ789KL");
        furgone.setCapacitaCarico(20.5);
        furgone.setCassoneChiuso(false);
        
        assertEquals("IJ789KL", furgone.getTarga());
        assertEquals(20.5, furgone.getCapacitaCarico(), 0.01);
        assertFalse(furgone.isCassoneChiuso());
    }
    
    @Test
    public void testVeicoloToString() {
        Auto auto = new Auto("Fiat", "Panda", 2023, 15000);
        auto.setTarga("AB123CD");
        
        String toString = auto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Auto"));
        assertTrue(toString.contains("Fiat"));
        assertTrue(toString.contains("Panda"));
        assertTrue(toString.contains("AB123CD"));
        assertTrue(toString.contains("15000"));
    }
    
    @Test
    public void testVeicoloSenzaTarga() {
        Auto auto = new Auto("Ford", "Fiesta", 2023, 20000);
        
        String toString = auto.toString();
        assertTrue(toString.contains("Non assegnata"));
    }
    
    @Test
    public void testVeicoloSettersBase() {
        Veicolo auto = new Auto("Renault", "Clio", 2023, 17000);
        
        auto.setMarca("Peugeot");
        auto.setModello("208");
        auto.setAnno(2024);
        auto.setPrezzo(19000);
        auto.setTarga("MN012OP");
        
        assertEquals("Peugeot", auto.getMarca());
        assertEquals("208", auto.getModello());
        assertEquals(2024, auto.getAnno());
        assertEquals(19000, auto.getPrezzo(), 0.01);
        assertEquals("MN012OP", auto.getTarga());
    }
}