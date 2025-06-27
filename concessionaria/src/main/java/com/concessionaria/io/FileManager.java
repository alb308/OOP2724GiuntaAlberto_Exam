package com.concessionaria.io;

import com.concessionaria.model.*;
import com.concessionaria.exception.ConcessionariaException;
import com.concessionaria.factory.VeicoloFactory;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FileManager {
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());
    private static final String FILE_INVENTARIO = "inventario.csv";
    private static final String SEPARATORE = ";";
    
    public static void salvaInventario(List<Veicolo> veicoli) throws ConcessionariaException {
        LOGGER.info("Salvataggio inventario su file");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_INVENTARIO))) {
            // Intestazione
            writer.println("TIPO;MARCA;MODELLO;ANNO;PREZZO;TARGA;INFO_EXTRA");
            
            for (Veicolo v : veicoli) {
                StringBuilder sb = new StringBuilder();
                sb.append(v.getClass().getSimpleName().toUpperCase()).append(SEPARATORE);
                sb.append(sanitizza(v.getMarca())).append(SEPARATORE);
                sb.append(sanitizza(v.getModello())).append(SEPARATORE);
                sb.append(v.getAnno()).append(SEPARATORE);
                sb.append(v.getPrezzo()).append(SEPARATORE);
                sb.append(sanitizza(v.getTarga())).append(SEPARATORE);
                
                // Info specifiche per tipo
                if (v instanceof Auto) {
                    Auto a = (Auto) v;
                    sb.append("Porte:").append(a.getNumeroPorte());
                    sb.append(",Cambio:").append(sanitizza(a.getTipoCambio()));
                } else if (v instanceof Moto) {
                    Moto m = (Moto) v;
                    sb.append("Cilindrata:").append(m.getCilindrata());
                    sb.append(",Tipo:").append(sanitizza(m.getTipoMoto()));
                } else if (v instanceof Furgone) {
                    Furgone f = (Furgone) v;
                    sb.append("Capacità:").append(f.getCapacitaCarico());
                    sb.append(",Cassone:").append(f.isCassoneChiuso() ? "Chiuso" : "Aperto");
                }
                
                writer.println(sb.toString());
            }
            
            LOGGER.info("Salvati " + veicoli.size() + " veicoli");
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio", e);
            throw new ConcessionariaException("Impossibile salvare l'inventario", e);
        }
    }
    
    public static List<Veicolo> caricaInventario() throws ConcessionariaException {
        List<Veicolo> veicoli = new ArrayList<>();
        File file = new File(FILE_INVENTARIO);
        
        if (!file.exists()) {
            LOGGER.info("File inventario non trovato, creato nuovo inventario");
            return veicoli;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            boolean primaLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primaLinea) {
                    primaLinea = false;
                    continue; // Salta intestazione
                }
                
                if (linea.trim().isEmpty()) continue;
                
                try {
                    Veicolo v = parsaLinea(linea);
                    if (v != null) {
                        veicoli.add(v);
                    }
                } catch (Exception e) {
                    LOGGER.warning("Errore nel parsing della linea: " + linea);
                }
            }
            
            LOGGER.info("Caricati " + veicoli.size() + " veicoli");
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento", e);
            throw new ConcessionariaException("Impossibile caricare l'inventario", e);
        }
        
        return veicoli;
    }
    
    private static Veicolo parsaLinea(String linea) throws ConcessionariaException {
        String[] parti = linea.split(SEPARATORE);
        if (parti.length < 6) return null;
        
        String tipo = parti[0];
        String marca = parti[1];
        String modello = parti[2];
        int anno = Integer.parseInt(parti[3]);
        double prezzo = Double.parseDouble(parti[4]);
        String targa = parti[5];
        
        Veicolo veicolo = VeicoloFactory.creaVeicolo(tipo, marca, modello, anno, prezzo);
        veicolo.setTarga(targa);
        
        // Parsing info extra se presenti
        if (parti.length > 6 && !parti[6].isEmpty()) {
            String[] infoExtra = parti[6].split(",");
            
            if (veicolo instanceof Auto && infoExtra.length >= 2) {
                Auto auto = (Auto) veicolo;
                String porte = infoExtra[0].split(":")[1];
                auto.setNumeroPorte(Integer.parseInt(porte));
                String cambio = infoExtra[1].split(":")[1];
                auto.setTipoCambio(cambio);
            } else if (veicolo instanceof Moto && infoExtra.length >= 2) {
                Moto moto = (Moto) veicolo;
                String cilindrata = infoExtra[0].split(":")[1];
                moto.setCilindrata(Integer.parseInt(cilindrata));
                String tipoMoto = infoExtra[1].split(":")[1];  // CAMBIATO: era 'tipo', ora è 'tipoMoto'
                moto.setTipoMoto(tipoMoto);
            } else if (veicolo instanceof Furgone && infoExtra.length >= 2) {
                Furgone furgone = (Furgone) veicolo;
                String capacita = infoExtra[0].split(":")[1];
                furgone.setCapacitaCarico(Double.parseDouble(capacita));
                String cassone = infoExtra[1].split(":")[1];
                furgone.setCassoneChiuso("Chiuso".equals(cassone));
            }
        }
        
        return veicolo;
    }
    
    private static String sanitizza(String input) {
        if (input == null) return "";
        // Rimuovi caratteri che potrebbero creare problemi nel CSV
        return input.replace(SEPARATORE, " ").replace("\n", " ").replace("\r", " ");
    }
}