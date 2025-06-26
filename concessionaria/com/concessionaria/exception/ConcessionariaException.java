package com.concessionaria.exception;

public class ConcessionariaException extends Exception {
    public ConcessionariaException(String messaggio) {
        super(messaggio);
    }
    
    public ConcessionariaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}