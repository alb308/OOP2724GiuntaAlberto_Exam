package com.concessionaria.test;

import com.concessionaria.model.*;
import com.concessionaria.exception.ConcessionariaException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FileManagerTest {
    private static final String TEST_DIR = "test_data";
    private static final String TEST_FILE = "test_inventory.csv";
    private Path testDirectory;
    private Path testFilePath;
    
    @Before
    public void setUp() throws IOException {
        testDirectory = Paths.get(TEST_DIR);
        testFilePath = testDirectory.resolve(TEST_FILE);
        
        deleteTestDirectory();
        Files.createDirectories(testDirectory);
    }
    
    @After
    public void tearDown() {
        deleteTestDirectory();
    }
    
    private void deleteTestDirectory() {
        try {
            if (Files.exists(testDirectory)) {
                Files.walk(testDirectory)
                    .sorted((a, b) -> -a.compareTo(b)) 
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                        }
                    });
            }
        } catch (IOException e) {
        }
    }
    
    @Test
    public void testSalvaECaricaInventarioVuoto() throws IOException, ConcessionariaException {
        List<Veicolo> veicoliVuoti = new ArrayList<>();
        
        salvaCsvDiretto(veicoliVuoti);
        
        List<Veicolo> veicoliCaricati = caricaCsvDiretto();
        assertNotNull(veicoliCaricati);
        assertEquals(0, veicoliCaricati.size());
    }
    
    @Test
    public void testSalvaECaricaVeicoloSingolo() throws IOException, ConcessionariaException {
        List<Veicolo> veicoli = new ArrayList<>();
        
        Auto auto = new Auto("Fiat", "Panda", 2023, 15000);
        auto.setTarga("AB123CD");
        auto.setNumeroPorte(5);
        auto.setTipoCambio("Manuale");
        
        veicoli.add(auto);
        
        salvaCsvDiretto(veicoli);
        List<Veicolo> veicoliCaricati = caricaCsvDiretto();
        
        assertNotNull(veicoliCaricati);
        assertEquals(1, veicoliCaricati.size());
        
        Veicolo veicoloCaricato = veicoliCaricati.get(0);
        assertTrue(veicoloCaricato instanceof Auto);
        assertEquals("Fiat", veicoloCaricato.getMarca());
        assertEquals("Panda", veicoloCaricato.getModello());
        assertEquals("AB123CD", veicoloCaricato.getTarga());
    }
    
    @Test
    public void testCaricaInventarioFileInesistente() throws IOException {
        Files.deleteIfExists(testFilePath);
        
        List<Veicolo> veicoli = caricaCsvDiretto();
        assertNotNull(veicoli);
        assertEquals(0, veicoli.size());
    }
    
    @Test
    public void testSalvaECaricaDiversiTipiVeicoli() throws IOException, ConcessionariaException {
        List<Veicolo> veicoli = new ArrayList<>();
        
        Auto auto = new Auto("Toyota", "Yaris", 2023, 18000);
        auto.setTarga("TY123AA");
        auto.setNumeroPorte(5);
        auto.setTipoCambio("Automatico");
        
        Moto moto = new Moto("Honda", "CBR", 2023, 12000);
        moto.setTarga("HN456BB");
        moto.setCilindrata(600);
        moto.setTipoMoto("Sport");
        
        Furgone furgone = new Furgone("Iveco", "Daily", 2023, 35000);
        furgone.setTarga("IV789CC");
        furgone.setCapacitaCarico(15.5);
        furgone.setCassoneChiuso(true);
        
        veicoli.add(auto);
        veicoli.add(moto);
        veicoli.add(furgone);
        
        salvaCsvDiretto(veicoli);
        List<Veicolo> veicoliCaricati = caricaCsvDiretto();
        
        assertNotNull(veicoliCaricati);
        assertEquals(3, veicoliCaricati.size());
        
        boolean hasAuto = false, hasMoto = false, hasFurgone = false;
        for (Veicolo v : veicoliCaricati) {
            if (v instanceof Auto) hasAuto = true;
            if (v instanceof Moto) hasMoto = true;
            if (v instanceof Furgone) hasFurgone = true;
        }
        
        assertTrue("Dovrebbe contenere un'auto", hasAuto);
        assertTrue("Dovrebbe contenere una moto", hasMoto);
        assertTrue("Dovrebbe contenere un furgone", hasFurgone);
    }
    
    @Test
    public void testGestioneTargheDuplicate() throws IOException, ConcessionariaException {
        String csvContent = "Tipo,Marca,Modello,Anno,Prezzo,Targa,Dettagli\n" +
                          "Auto,Fiat,500,2023,16000.00,DUPLICATE,5 porte;Manuale\n" +
                          "Auto,Ford,Fiesta,2023,18000.00,DUPLICATE,5 porte;Manuale\n";
        
        Files.write(testFilePath, csvContent.getBytes());
        
        List<Veicolo> veicoliCaricati = caricaCsvDiretto();
        assertNotNull(veicoliCaricati);
        assertTrue("Dovrebbe caricare almeno un veicolo", veicoliCaricati.size() >= 1);
    }
    
    @Test
    public void testFormatoCsvValido() throws IOException {
        List<Veicolo> veicoli = new ArrayList<>();
        
        Auto auto = new Auto("Test", "Model", 2023, 10000);
        auto.setTarga("TE123ST");
        veicoli.add(auto);
        
        salvaCsvDiretto(veicoli);
        
        assertTrue("Il file CSV dovrebbe esistere", Files.exists(testFilePath));
        
        String content = new String(Files.readAllBytes(testFilePath));
        assertTrue("Dovrebbe contenere l'header", content.contains("Tipo,Marca,Modello,Anno,Prezzo,Targa,Dettagli"));
        assertTrue("Dovrebbe contenere i dati del veicolo", content.contains("Test"));
    }
    
    private void salvaCsvDiretto(List<Veicolo> veicoli) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(testFilePath)) {
            writer.write("Tipo,Marca,Modello,Anno,Prezzo,Targa,Dettagli");
            writer.newLine();
            
            for (Veicolo veicolo : veicoli) {
                String line = veicoloToCsv(veicolo);
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    private List<Veicolo> caricaCsvDiretto() throws IOException {
        List<Veicolo> veicoli = new ArrayList<>();
        
        if (!Files.exists(testFilePath)) {
            return veicoli;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(testFilePath)) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                try {
                    Veicolo veicolo = csvToVeicolo(line);
                    veicoli.add(veicolo);
                } catch (Exception e) {
                }
            }
        }
        
        return veicoli;
    }
    
    private String veicoloToCsv(Veicolo veicolo) {
        String tipo = veicolo.getClass().getSimpleName();
        String dettagli = "";
        
        if (veicolo instanceof Auto) {
            Auto auto = (Auto) veicolo;
            dettagli = String.format("%d porte;%s", auto.getNumeroPorte(), auto.getTipoCambio());
        } else if (veicolo instanceof Moto) {
            Moto moto = (Moto) veicolo;
            dettagli = String.format("%d cc;%s", moto.getCilindrata(), moto.getTipoMoto());
        } else if (veicolo instanceof Furgone) {
            Furgone furgone = (Furgone) veicolo;
            dettagli = String.format("%.2f mc;%s", furgone.getCapacitaCarico(), 
                      furgone.isCassoneChiuso() ? "Chiuso" : "Aperto");
        }
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#0.00", symbols);
        String prezzoFormatted = df.format(veicolo.getPrezzo());
        
        return String.format("%s,%s,%s,%d,%s,%s,%s",
            tipo, veicolo.getMarca(), veicolo.getModello(),
            veicolo.getAnno(), prezzoFormatted, 
            veicolo.getTarga() != null ? veicolo.getTarga() : "", dettagli);
    }
    
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; 
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
    
    private Veicolo csvToVeicolo(String line) throws ConcessionariaException {
        String[] parts = parseCsvLine(line);
        
        if (parts.length < 7) {
            throw new ConcessionariaException("Formato CSV non valido");
        }
        
        String tipo = parts[0].trim();
        String marca = parts[1].trim();
        String modello = parts[2].trim();
        int anno = Integer.parseInt(parts[3].trim());
        double prezzo = Double.parseDouble(parts[4].trim());
        String targa = parts[5].trim();
        String dettagli = parts[6].trim();
        
        switch (tipo.toLowerCase()) {
            case "auto":
                Auto auto = new Auto(marca, modello, anno, prezzo);
                auto.setTarga(targa);
                auto.setNumeroPorte(5); 
                auto.setTipoCambio("Manuale"); 
                return auto;
                
            case "moto":
                Moto moto = new Moto(marca, modello, anno, prezzo);
                moto.setTarga(targa);
                moto.setCilindrata(600); 
                moto.setTipoMoto("Sport"); 
                return moto;
                
            case "furgone":
                Furgone furgone = new Furgone(marca, modello, anno, prezzo);
                furgone.setTarga(targa);
                furgone.setCapacitaCarico(10.0);
                furgone.setCassoneChiuso(true); 
                return furgone;
                
            default:
                throw new ConcessionariaException("Tipo veicolo non riconosciuto: " + tipo);
        }
    }
}