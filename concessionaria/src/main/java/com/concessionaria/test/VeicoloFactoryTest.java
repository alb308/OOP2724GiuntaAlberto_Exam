package com.concessionaria.test;

import com.concessionaria.factory.VeicoloFactory;
import com.concessionaria.model.*;
import com.concessionaria.exception.ConcessionariaException;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class VeicoloFactoryTest {
    
    @Test
    public void testCreaAuto() throws ConcessionariaException {
        Veicolo v = VeicoloFactory.creaVeicolo("AUTO", "Fiat", "Panda", 2023, 15000);
        assertNotNull(v);
        assertTrue(v instanceof Auto);
        assertEquals("Fiat", v.getMarca());
    }
    
    @Test
    public void testCreaMoto() throws ConcessionariaException {
        Veicolo v = VeicoloFactory.creaVeicolo("MOTO", "Yamaha", "R1", 2023, 18000);
        assertNotNull(v);
        assertTrue(v instanceof Moto);
    }
    
    @Test
    public void testCreaFurgone() throws ConcessionariaException {
        Veicolo v = VeicoloFactory.creaVeicolo("FURGONE", "Iveco", "Daily", 2023, 35000);
        assertNotNull(v);
        assertTrue(v instanceof Furgone);
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testTipoNonValido() throws ConcessionariaException {
        VeicoloFactory.creaVeicolo("AEREO", "Boeing", "747", 2023, 1000000);
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testTipoNull() throws ConcessionariaException {
        VeicoloFactory.creaVeicolo(null, "Marca", "Modello", 2023, 10000);
    }
}