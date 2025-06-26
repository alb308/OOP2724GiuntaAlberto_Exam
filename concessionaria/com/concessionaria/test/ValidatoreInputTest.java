package com.concessionaria.test;

import com.concessionaria.util.ValidatoreInput;
import com.concessionaria.model.Auto;
import com.concessionaria.exception.ConcessionariaException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidatoreInputTest {
    
    @Test
    public void testValidaTarga() {
        assertTrue(ValidatoreInput.validaTarga("AB123CD"));
        assertTrue(ValidatoreInput.validaTarga("ZZ999XX"));
        assertFalse(ValidatoreInput.validaTarga("123456"));
        assertFalse(ValidatoreInput.validaTarga("ABCDEFG"));
        assertFalse(ValidatoreInput.validaTarga(""));
        assertFalse(ValidatoreInput.validaTarga(null));
    }
    
    @Test
    public void testValidaAnno() {
        assertTrue(ValidatoreInput.validaAnno(2023));
        assertTrue(ValidatoreInput.validaAnno(2000));
        assertFalse(ValidatoreInput.validaAnno(1899));
        assertFalse(ValidatoreInput.validaAnno(2030));
    }
    
    @Test
    public void testValidaPrezzo() {
        assertTrue(ValidatoreInput.validaPrezzo(10000));
        assertTrue(ValidatoreInput.validaPrezzo(1));
        assertFalse(ValidatoreInput.validaPrezzo(0));
        assertFalse(ValidatoreInput.validaPrezzo(-100));
    }
    
    @Test
    public void testSanitizzaStringa() {
        assertEquals("test", ValidatoreInput.sanitizzaStringa("test"));
        assertEquals("test123", ValidatoreInput.sanitizzaStringa("test<script>123"));
        assertEquals("hello world", ValidatoreInput.sanitizzaStringa("hello' OR '1'='1 world"));
    }
}