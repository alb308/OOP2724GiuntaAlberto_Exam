package com.concessionaria.io;

import com.concessionaria.exception.ConcessionariaException;
import com.concessionaria.model.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.logging.Logger;
import java.security.SecureRandom;

public class FileManager {
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());
    private static final String DEFAULT_FILE_NAME = "inventario.csv";
    private static final String BACKUP_SUFFIX = ".backup";
    private static final String TEMP_SUFFIX = ".tmp";
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB max
    private static final Set<String> ALLOWED_EXTENSIONS =
    new HashSet<>(Arrays.asList(".csv", ".txt"));

    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static void salvaInventario(List<Veicolo> veicoli) throws ConcessionariaException {
        FileManager fm = new FileManager();
        try {
            fm.salvaInventarioInterno(veicoli);
        } catch (IOException e) {
            throw new ConcessionariaException("Errore durante il salvataggio", e);
        }
    }
    
    public static List<Veicolo> caricaInventario() throws ConcessionariaException {
        FileManager fm = new FileManager();
        try {
            return fm.caricaInventarioInterno();
        } catch (IOException e) {
            throw new ConcessionariaException("Errore durante il caricamento", e);
        }
    }
    
    private final String fileName;
    private final Path dataDirectory;
    
    public FileManager() {
        this(DEFAULT_FILE_NAME);
    }
    
    public FileManager(String fileName) {
        this.fileName = sanitizeFileName(fileName);
        this.dataDirectory = initializeSecureDataDirectory();
        logger.info("FileManager inizializzato con file: " + this.fileName);
    }
    
    private Path initializeSecureDataDirectory() {
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "data");
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
                
                if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
                    Set<PosixFilePermission> perms = new HashSet<>();
                    perms.add(PosixFilePermission.OWNER_READ);
                    perms.add(PosixFilePermission.OWNER_WRITE);
                    perms.add(PosixFilePermission.OWNER_EXECUTE);
                    Files.setPosixFilePermissions(dir, perms);
                }
                
                logger.info("Directory dati creata in modo sicuro: " + dir.toAbsolutePath());
            }
            return dir;
        } catch (IOException e) {
            logger.severe("Impossibile creare directory sicura: " + e.getMessage());
            throw new RuntimeException("Errore nell'inizializzazione del sistema di storage", e);
        }
    }
    
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException("Nome file non valido");
        }
        
        String sanitized = fileName.trim()
            .replaceAll("\\.\\./", "")
            .replaceAll("[\\\\/:]", "_")
            .replaceAll("[^a-zA-Z0-9._-]", "");
        
        boolean hasValidExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (sanitized.toLowerCase().endsWith(ext)) {
                hasValidExtension = true;
                break;
            }
        }
        
        if (!hasValidExtension) {
            sanitized += ".csv";
        }
        
        if (isReservedFileName(sanitized)) {
            sanitized = "safe_" + sanitized;
        }
        
        logger.info("Nome file sanitizzato: " + fileName + " -> " + sanitized);
        return sanitized;
    }
    
    private boolean isReservedFileName(String name) {
        String[] reserved = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4",
                           "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", 
                           "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        
        String nameUpper = name.toUpperCase();
        for (String r : reserved) {
            if (nameUpper.startsWith(r + ".") || nameUpper.equals(r)) {
                return true;
            }
        }
        return false;
    }
    
    private void salvaInventarioInterno(List<Veicolo> veicoli) throws IOException, ConcessionariaException {
        if (veicoli == null) {
            throw new ConcessionariaException("Lista veicoli non può essere null");
        }
        
        Path filePath = dataDirectory.resolve(fileName);
        Path tempFile = dataDirectory.resolve(fileName + TEMP_SUFFIX + secureRandom.nextInt(10000));
        Path backupFile = dataDirectory.resolve(fileName + BACKUP_SUFFIX);
        
        try {
            if (Files.exists(filePath)) {
                Files.copy(filePath, backupFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Backup creato: " + backupFile);
            }
            
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.WRITE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                
                writer.write("Tipo,Marca,Modello,Anno,Prezzo,Targa,Dettagli");
                writer.newLine();
                
                for (Veicolo veicolo : veicoli) {
                    String csvLine = veicoloToCsv(veicolo);
                    writer.write(csvLine);
                    writer.newLine();
                }
                
                writer.flush();
            }
            
            if (!verificaIntegritaFile(tempFile, veicoli.size() + 1)) {
                throw new ConcessionariaException("File temporaneo corrotto");
            }
            
            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            
            logger.info("Inventario salvato con successo. Veicoli: " + veicoli.size());
            
        } catch (IOException e) {
            if (Files.exists(backupFile)) {
                try {
                    Files.copy(backupFile, filePath, StandardCopyOption.REPLACE_EXISTING);
                    logger.warning("Ripristinato da backup dopo errore");
                } catch (IOException backupError) {
                    logger.severe("Impossibile ripristinare dal backup: " + backupError.getMessage());
                }
            }

            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {}
            
            logger.severe("Errore durante il salvataggio: " + e.getMessage());
            throw new ConcessionariaException("Errore durante il salvataggio dell'inventario", e);
        }
    }
    

    private List<Veicolo> caricaInventarioInterno() throws IOException, ConcessionariaException {
        Path filePath = dataDirectory.resolve(fileName);
        
        if (!Files.exists(filePath)) {
            logger.info("File inventario non trovato, creazione nuovo inventario");
            return new ArrayList<>();
        }
        

        long fileSize = Files.size(filePath);
        if (fileSize > MAX_FILE_SIZE) {
            logger.severe("File troppo grande: " + fileSize + " bytes");
            throw new ConcessionariaException("File inventario troppo grande");
        }
        
        if (fileSize == 0) {
            logger.warning("File inventario vuoto");
            return new ArrayList<>();
        }
        
        List<Veicolo> veicoli = new ArrayList<>();
        Set<String> targheCaricate = new HashSet<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line = reader.readLine(); 
            
            if (line == null || !isValidHeader(line)) {
                throw new ConcessionariaException("Header del file non valido");
            }
            
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    line = sanitizeCsvLine(line);
                    
                    Veicolo veicolo = csvToVeicolo(line);
                    
                    if (targheCaricate.contains(veicolo.getTarga())) {
                        logger.warning("Targa duplicata ignorata: " + veicolo.getTarga() + " alla linea " + lineNumber);
                        continue;
                    }
                    
                    veicoli.add(veicolo);
                    targheCaricate.add(veicolo.getTarga());
                    
                } catch (ConcessionariaException e) {
                    logger.warning("Errore alla linea " + lineNumber + ": " + e.getMessage());
                  
                } catch (Exception e) {
                    logger.warning("Errore generico alla linea " + lineNumber + ": " + e.getMessage());
                
            }
            
            logger.info("Inventario caricato con successo. Veicoli: " + veicoli.size());
            return veicoli;
            
        }} catch (IOException e) {
            logger.severe("Errore durante la lettura del file: " + e.getMessage());
            throw new ConcessionariaException("Errore durante il caricamento dell'inventario", e);
        }
        return veicoli;
    }
    
    private boolean isValidHeader(String header) {
        String expectedHeader = "Tipo,Marca,Modello,Anno,Prezzo,Targa,Dettagli";
        return header.trim().equalsIgnoreCase(expectedHeader);
    }
    
    private String sanitizeCsvLine(String line) {
        return line.replaceAll("[\\p{Cntrl}&&[^\t]]", "")
                  .replaceAll("[^\\x20-\\x7E,]", "")
                  .trim();
    }
    
    private boolean verificaIntegritaFile(Path file, int expectedLines) {
        try {
            long lines = Files.lines(file).count();
            return lines == expectedLines;
        } catch (IOException e) {
            logger.severe("Errore nella verifica integrità: " + e.getMessage());
            return false;
        }
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
        
        return String.format("%s,%s,%s,%d,%.2f,%s,%s",
            escapeCsv(tipo),
            escapeCsv(veicolo.getMarca()),
            escapeCsv(veicolo.getModello()),
            veicolo.getAnno(),
            veicolo.getPrezzo(),
            escapeCsv(veicolo.getTarga()),
            escapeCsv(dettagli)
        );
    }
    
    private String escapeCsv(String value) {
        if (value == null) return "";
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }
    
    private Veicolo csvToVeicolo(String line) throws ConcessionariaException {
        String[] parts = parseCsvLine(line);
        
        if (parts.length < 7) {
            throw new ConcessionariaException("Formato CSV non valido: campi insufficienti");
        }
        
        String tipo = parts[0].trim();
        String marca = parts[1].trim();
        String modello = parts[2].trim();
        int anno = Integer.parseInt(parts[3].trim());
        double prezzo = Double.parseDouble(parts[4].trim());
        String targa = parts[5].trim();
        String dettagli = parts[6].trim();
        
        try {
            switch (tipo.toLowerCase()) {
                case "auto":
                    return parseAuto(marca, modello, anno, prezzo, targa, dettagli);
                case "moto":
                    return parseMoto(marca, modello, anno, prezzo, targa, dettagli);
                case "furgone":
                    return parseFurgone(marca, modello, anno, prezzo, targa, dettagli);
                default:
                    throw new ConcessionariaException("Tipo veicolo non riconosciuto: " + tipo);
            }
        } catch (Exception e) {
            throw new ConcessionariaException("Errore nella creazione del veicolo: " + e.getMessage(), e);
        }
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
    
    private Auto parseAuto(String marca, String modello, int anno, double prezzo, String targa, String dettagli) {
        String[] dettagliParts = dettagli.split(";");
        int numeroPorte = 5; // default
        String tipoCambio = "Manuale"; // default
        
        if (dettagliParts.length >= 2) {
            try {
                numeroPorte = Integer.parseInt(dettagliParts[0].replaceAll("[^0-9]", ""));
                tipoCambio = dettagliParts[1].trim();
            } catch (NumberFormatException e) {
                logger.warning("Dettagli auto non validi, uso valori default: " + dettagli);
            }
        }
        
        Auto auto = new Auto(marca, modello, anno, prezzo);
        auto.setTarga(targa);
        auto.setNumeroPorte(numeroPorte);
        auto.setTipoCambio(tipoCambio);
        return auto;
    }
    
    private Moto parseMoto(String marca, String modello, int anno, double prezzo, String targa, String dettagli) {
        String[] dettagliParts = dettagli.split(";");
        int cilindrata = 600; // default
        String tipoMoto = "Naked"; // default
        
        if (dettagliParts.length >= 2) {
            try {
                cilindrata = Integer.parseInt(dettagliParts[0].replaceAll("[^0-9]", ""));
                tipoMoto = dettagliParts[1].trim();
            } catch (NumberFormatException e) {
                logger.warning("Dettagli moto non validi, uso valori default: " + dettagli);
            }
        }
        
        Moto moto = new Moto(marca, modello, anno, prezzo);
        moto.setTarga(targa);
        moto.setCilindrata(cilindrata);
        moto.setTipoMoto(tipoMoto);
        return moto;
    }
    
    private Furgone parseFurgone(String marca, String modello, int anno, double prezzo, String targa, String dettagli) {
        String[] dettagliParts = dettagli.split(";");
        double capacitaCarico = 10.0; // default
        boolean cassoneChiuso = true; // default
        
        if (dettagliParts.length >= 2) {
            try {
                capacitaCarico = Double.parseDouble(dettagliParts[0].replaceAll("[^0-9.]", ""));
                String tipoCarico = dettagliParts[1].trim();
                cassoneChiuso = tipoCarico.equalsIgnoreCase("Chiuso");
            } catch (NumberFormatException e) {
                logger.warning("Dettagli furgone non validi, uso valori default: " + dettagli);
            }
        }
        
        Furgone furgone = new Furgone(marca, modello, anno, prezzo);
        furgone.setTarga(targa);
        furgone.setCapacitaCarico(capacitaCarico);
        furgone.setCassoneChiuso(cassoneChiuso);
        return furgone;
    }
}
