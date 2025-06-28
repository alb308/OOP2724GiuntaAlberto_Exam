package com.concessionaria.io;

import com.concessionaria.exception.ConcessionariaException;
import com.concessionaria.model.*;
import java.io.*;
import java.util.*;

/**
 * Gestisce salvataggio e caricamento veicoli su file CSV
 */
public class FileManager {
    private static final String FILE_NAME = "inventario.csv";
    
    /**
     * Salva lista veicoli su file
     */
    public static void salvaInventario(List<Veicolo> veicoli) throws ConcessionariaException {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            // Header
            writer.println("Tipo,Marca,Modello,Anno,Prezzo,Targa,Extra1,Extra2");
            
            // Ogni veicolo
            for (Veicolo v : veicoli) {
                writer.println(veicoloToString(v));
            }
            
        } catch (IOException e) {
            throw new ConcessionariaException("Errore salvataggio: " + e.getMessage());
        }
    }
    
    /**
     * Carica veicoli da file
     */
    public static List<Veicolo> caricaInventario() throws ConcessionariaException {
        List<Veicolo> veicoli = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            // Skip header
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            // Leggi righe
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    veicoli.add(stringToVeicolo(line));
                }
            }
            
        } catch (FileNotFoundException e) {
            // File non esiste = inventario vuoto
            return veicoli;
        } catch (Exception e) {
            throw new ConcessionariaException("Errore caricamento: " + e.getMessage());
        }
        
        return veicoli;
    }
    
    /**
     * Converte veicolo in riga CSV
     */
    private static String veicoloToString(Veicolo v) {
        String tipo = v.getClass().getSimpleName();
        String base = String.format("%s,%s,%s,%d,%.2f,%s,", 
            tipo, v.getMarca(), v.getModello(), 
            v.getAnno(), v.getPrezzo(), v.getTarga());
        
        if (v instanceof Auto) {
            Auto a = (Auto) v;
            return base + a.getNumeroPorte() + "," + a.getTipoCambio();
        } else if (v instanceof Moto) {
            Moto m = (Moto) v;
            return base + m.getCilindrata() + "," + m.getTipoMoto();
        } else if (v instanceof Furgone) {
            Furgone f = (Furgone) v;
            return base + f.getCapacitaCarico() + "," + f.isCassoneChiuso();
        }
        
        return base + ",";
    }
    
    /**
     * Converte riga CSV in veicolo
     */
    private static Veicolo stringToVeicolo(String line) throws Exception {
        String[] parts = line.split(",");
        
        // Dati comuni
        String tipo = parts[0];
        String marca = parts[1];
        String modello = parts[2];
        int anno = Integer.parseInt(parts[3]);
        double prezzo = Double.parseDouble(parts[4]);
        String targa = parts[5];
        
        // Crea veicolo giusto
        Veicolo v;
        
        if (tipo.equals("Auto")) {
            Auto a = new Auto(marca, modello, anno, prezzo);
            a.setTarga(targa);
            a.setNumeroPorte(Integer.parseInt(parts[6]));
            a.setTipoCambio(parts[7]);
            v = a;
            
        } else if (tipo.equals("Moto")) {
            Moto m = new Moto(marca, modello, anno, prezzo);
            m.setTarga(targa);
            m.setCilindrata(Integer.parseInt(parts[6]));
            m.setTipoMoto(parts[7]);
            v = m;
            
        } else if (tipo.equals("Furgone")) {
            Furgone f = new Furgone(marca, modello, anno, prezzo);
            f.setTarga(targa);
            f.setCapacitaCarico(Double.parseDouble(parts[6]));
            f.setCassoneChiuso(Boolean.parseBoolean(parts[7]));
            v = f;
            
        } else {
            throw new Exception("Tipo sconosciuto: " + tipo);
        }
        
        return v;
    }
}