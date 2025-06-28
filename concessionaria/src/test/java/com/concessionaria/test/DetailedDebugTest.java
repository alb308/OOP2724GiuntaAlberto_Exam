package com.concessionaria.test;

import com.concessionaria.model.Auto;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DetailedDebugTest {
    public static void main(String[] args) {
        try {
            System.out.println("=== DEBUG TEST DETTAGLIATO ===");
            
            System.out.println("\n1. Test creazione Auto:");
            Auto auto = new Auto("Fiat", "Panda", 2023, 15000);
            System.out.println("Auto dopo costruttore:");
            System.out.println("  Marca: '" + auto.getMarca() + "'");
            System.out.println("  Modello: '" + auto.getModello() + "'");
            System.out.println("  Targa: '" + auto.getTarga() + "'");
            System.out.println("  Targa == null: " + (auto.getTarga() == null));
            
            System.out.println("\n2. Test setTarga:");
            auto.setTarga("AB123CD");
            System.out.println("Auto dopo setTarga('AB123CD'):");
            System.out.println("  Targa: '" + auto.getTarga() + "'");
            System.out.println("  Targa.length(): " + (auto.getTarga() != null ? auto.getTarga().length() : "null"));
            
            System.out.println("\n3. Test salvataggio CSV:");
            Path testFile = Paths.get("debug_test.csv");
            List<Auto> autos = new ArrayList<>();
            autos.add(auto);
            
            try (BufferedWriter writer = Files.newBufferedWriter(testFile)) {
                writer.write("Tipo,Marca,Modello,Anno,Prezzo,Targa,Dettagli");
                writer.newLine();
                
                String csvLine = veicoloToCsv(auto);
                System.out.println("Linea CSV generata: '" + csvLine + "'");
                writer.write(csvLine);
                writer.newLine();
            }
            
            System.out.println("\n4. Test lettura CSV:");
            String fileContent = new String(Files.readAllBytes(testFile));
            System.out.println("Contenuto file:");
            System.out.println(fileContent);
            
            System.out.println("\n5. Test parsing CSV:");
            try (BufferedReader reader = Files.newBufferedReader(testFile)) {
                String header = reader.readLine();
                System.out.println("Header: '" + header + "'");
                
                String line = reader.readLine();
                System.out.println("Linea dati: '" + line + "'");
                
                if (line != null) {
                    String[] parts = line.split(",");
                    System.out.println("Parti dopo split(','):");
                    for (int i = 0; i < parts.length; i++) {
                        System.out.println("  parts[" + i + "] = '" + parts[i] + "'");
                    }
                    
                    if (parts.length >= 6) {
                        String targa = parts[5].trim();
                        System.out.println("Targa estratta: '" + targa + "'");
                        System.out.println("Targa.length(): " + targa.length());
                        
                        Auto autoFromCsv = new Auto(parts[1].trim(), parts[2].trim(), 
                            Integer.parseInt(parts[3].trim()), Double.parseDouble(parts[4].trim()));
                        
                        System.out.println("Auto da CSV prima di setTarga:");
                        System.out.println("  Targa: '" + autoFromCsv.getTarga() + "'");
                        
                        autoFromCsv.setTarga(targa);
                        
                        System.out.println("Auto da CSV dopo setTarga:");
                        System.out.println("  Targa: '" + autoFromCsv.getTarga() + "'");
                        System.out.println("  Targa equals 'AB123CD': " + "AB123CD".equals(autoFromCsv.getTarga()));
                    }
                }
            }
            
            Files.deleteIfExists(testFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String veicoloToCsv(Auto auto) {
        String tipo = auto.getClass().getSimpleName();
        String dettagli = String.format("%d porte;%s", auto.getNumeroPorte(), auto.getTipoCambio());
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#0.00", symbols);
        String prezzoFormatted = df.format(auto.getPrezzo());
        
        System.out.println("DEBUG - Prezzo originale: " + auto.getPrezzo());
        System.out.println("DEBUG - Prezzo formattato: '" + prezzoFormatted + "'");
        System.out.println("DEBUG - Targa auto: '" + auto.getTarga() + "'");
        
        return String.format("%s,%s,%s,%d,%s,%s,%s",
            tipo, auto.getMarca(), auto.getModello(),
            auto.getAnno(), prezzoFormatted, 
            auto.getTarga() != null ? auto.getTarga() : "", dettagli);
    }
}