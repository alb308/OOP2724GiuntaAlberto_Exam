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
        // Corretto: il sanitizer rimuove solo < > " ' & ; ma mantiene il testo
        assertEquals("testscript123", ValidatoreInput.sanitizzaStringa("test<script>123"));
        assertEquals("hello OR 1=1 world", ValidatoreInput.sanitizzaStringa("hello' OR '1'='1 world"));
        assertEquals("normal text", ValidatoreInput.sanitizzaStringa("normal text"));
        assertEquals("", ValidatoreInput.sanitizzaStringa(""));
        assertEquals("", ValidatoreInput.sanitizzaStringa(null));
    }
    
    @Test
    public void testValidaNumeroIntero() {
        assertTrue(ValidatoreInput.validaNumeroIntero("123"));
        assertTrue(ValidatoreInput.validaNumeroIntero("0"));
        assertTrue(ValidatoreInput.validaNumeroIntero("-456"));
        assertFalse(ValidatoreInput.validaNumeroIntero("abc"));
        assertFalse(ValidatoreInput.validaNumeroIntero("12.5"));
        assertFalse(ValidatoreInput.validaNumeroIntero(""));
        assertFalse(ValidatoreInput.validaNumeroIntero(null));
    }
    
    @Test
    public void testValidaNumeroDecimale() {
        assertTrue(ValidatoreInput.validaNumeroDecimale("123.45"));
        assertTrue(ValidatoreInput.validaNumeroDecimale("0"));
        assertTrue(ValidatoreInput.validaNumeroDecimale("-456.78"));
        assertTrue(ValidatoreInput.validaNumeroDecimale("100"));
        assertFalse(ValidatoreInput.validaNumeroDecimale("abc"));
        assertFalse(ValidatoreInput.validaNumeroDecimale(""));
        assertFalse(ValidatoreInput.validaNumeroDecimale(null));
    }
}