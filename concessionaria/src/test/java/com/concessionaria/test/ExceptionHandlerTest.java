package com.concessionaria.test;

import com.concessionaria.exception.ExceptionHandler;
import com.concessionaria.exception.ConcessionariaException;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

public class ExceptionHandlerTest {
    
    @Test
    public void testGestisciEccezioneGenerica() {
        RuntimeException ex = new RuntimeException("Test exception");
        String messaggio = ExceptionHandler.gestisciEccezione(ex, "Test context");
        
        assertNotNull(messaggio);
        assertFalse(messaggio.isEmpty());
    }
    
    @Test
    public void testGestisciConcessionariaException() {
        ConcessionariaException ex = new ConcessionariaException("Messaggio custom");
        String messaggio = ExceptionHandler.gestisciEccezione(ex, "Test context");
        
        assertEquals("Messaggio custom", messaggio);
    }
    
    @Test
    public void testGestisciNumberFormatException() {
        NumberFormatException ex = new NumberFormatException("Invalid number");
        String messaggio = ExceptionHandler.gestisciEccezione(ex, "Parsing context");
        
        assertEquals("Formato numerico non valido", messaggio);
    }
    
    @Test
    public void testGestisciIOException() {
        IOException ex = new IOException("File not found");
        String messaggio = ExceptionHandler.gestisciEccezione(ex, "File context");
        
        assertEquals("Errore di accesso ai dati", messaggio);
    }
    
    @Test
    public void testRequireNonNull() {
        try {
            ExceptionHandler.requireNonNull("test", "parametro");
        } catch (ConcessionariaException e) {
            fail("Non dovrebbe lanciare eccezione per valore non null");
        }
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testRequireNonNullConNull() throws ConcessionariaException {
        ExceptionHandler.requireNonNull(null, "parametro");
    }
    
    @Test
    public void testRequireNonEmpty() {
        try {
            ExceptionHandler.requireNonEmpty("test", "stringa");
        } catch (ConcessionariaException e) {
            fail("Non dovrebbe lanciare eccezione per stringa non vuota");
        }
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testRequireNonEmptyConStringaVuota() throws ConcessionariaException {
        ExceptionHandler.requireNonEmpty("", "stringa");
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testRequireNonEmptyConNull() throws ConcessionariaException {
        ExceptionHandler.requireNonEmpty(null, "stringa");
    }
    
    @Test
    public void testRequirePositive() {
        try {
            ExceptionHandler.requirePositive(10.5, "numero");
        } catch (ConcessionariaException e) {
            fail("Non dovrebbe lanciare eccezione per numero positivo");
        }
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testRequirePositiveConZero() throws ConcessionariaException {
        ExceptionHandler.requirePositive(0, "numero");
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testRequirePositiveConNegativo() throws ConcessionariaException {
        ExceptionHandler.requirePositive(-5, "numero");
    }
    
    @Test
    public void testRequireInRange() {
        try {
            ExceptionHandler.requireInRange(15, 10, 20, "numero");
        } catch (ConcessionariaException e) {
            fail("Non dovrebbe lanciare eccezione per numero nel range");
        }
    }
    
    @Test(expected = ConcessionariaException.class)
    public void testRequireInRangeFuoriRange() throws ConcessionariaException {
        ExceptionHandler.requireInRange(25, 10, 20, "numero");
    }
    
    @Test
    public void testWrapException() {
        RuntimeException original = new RuntimeException("Original message");
        ConcessionariaException wrapped = ExceptionHandler.wrapException(original, "User message");
        
        assertEquals("User message", wrapped.getMessage());
        assertEquals(original, wrapped.getCause());
    }
}