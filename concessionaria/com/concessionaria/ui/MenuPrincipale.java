// File: MenuPrincipale.java
package com.concessionaria.ui;

import com.concessionaria.service.GestioneInventario;
import com.concessionaria.model.*;
import com.concessionaria.factory.VeicoloFactory;
import com.concessionaria.iterator.IteratoreInventario;
import com.concessionaria.exception.*;
import com.concessionaria.io.FileManager;
import com.concessionaria.util.ValidatoreInput;
import java.util.*;
import java.util.logging.Logger;

public class MenuPrincipale {
    private static final Logger LOGGER = Logger.getLogger(MenuPrincipale.class.getName());
    private GestioneInventario inventario;
    private Scanner scanner;
    
    public MenuPrincipale() {
        this.inventario = new GestioneInventario();
        this.scanner = new Scanner(System.in);
    }
    
    public void avvia() {
        System.out.println("\n=== BENVENUTO NEL SISTEMA DI GESTIONE CONCESSIONARIA ===\n");
        
        // Carica dati esistenti
        caricaDati();
        
        boolean continua = true;
        while (continua) {
            try {
                mostraMenu();
                int scelta = leggiScelta();
                
                switch (scelta) {
                    case 1:
                        aggiungiVeicolo();
                        break;
                    case 2:
                        visualizzaInventario();
                        break;
                    case 3:
                        ricercaVeicoli();
                        break;
                    case 4:
                        rimuoviVeicolo();
                        break;
                    case 5:
                        visualizzaCatalogo();
                        break;
                    case 6:
                        salvaDati();
                        break;
                    case 0:
                        salvaDati();
                        continua = false;
                        System.out.println("\nArrivederci!");
                        break;
                    default:
                        System.out.println("\n❌ Scelta non valida!");
                }
            } catch (Exception e) {
                ExceptionHandler.gestisciEccezione(e, "Menu principale");
            }
            
            if (continua) {
                System.out.println("\nPremi INVIO per continuare...");
                scanner.nextLine();
            }
        }
    }
    
    private void mostraMenu() {
        System.out.println("\n=== MENU PRINCIPALE ===");
        System.out.println("1. Aggiungi nuovo veicolo");
        System.out.println("2. Visualizza inventario completo");
        System.out.println("3. Ricerca veicoli");
        System.out.println("4. Rimuovi veicolo");
        System.out.println("5. Visualizza catalogo per categorie");
        System.out.println("6. Salva dati");
        System.out.println("0. Esci");
        System.out.print("\nScelta: ");
    }
    
    private int leggiScelta() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void aggiungiVeicolo() {
        System.out.println("\n=== AGGIUNGI NUOVO VEICOLO ===");
        
        try {
            // Tipo veicolo
            System.out.println("\nTipo veicolo:");
            System.out.println("1. Auto");
            System.out.println("2. Moto");
            System.out.println("3. Furgone");
            System.out.print("Scelta: ");
            
            int tipoScelta = leggiScelta();
            String tipo;
            switch (tipoScelta) {
                case 1: tipo = "AUTO"; break;
                case 2: tipo = "MOTO"; break;
                case 3: tipo = "FURGONE"; break;
                default:
                    System.out.println("❌ Tipo non valido");
                    return;
            }
            
            // Dati comuni
            System.out.print("\nMarca: ");
            String marca = ValidatoreInput.sanitizzaStringa(scanner.nextLine());
            
            System.out.print("Modello: ");
            String modello = ValidatoreInput.sanitizzaStringa(scanner.nextLine());
            
            System.out.print("Anno: ");
            int anno = Integer.parseInt(scanner.nextLine());
            if (!ValidatoreInput.validaAnno(anno)) {
                throw new ConcessionariaException("Anno non valido (1900-2025)");
            }
            
            System.out.print("Prezzo (€): ");
            double prezzo = Double.parseDouble(scanner.nextLine());
            if (!ValidatoreInput.validaPrezzo(prezzo)) {
                throw new ConcessionariaException("Prezzo non valido");
            }
            
            System.out.print("Targa (es. AB123CD): ");
            String targa = scanner.nextLine().toUpperCase();
            if (!ValidatoreInput.validaTarga(targa)) {
                throw new ConcessionariaException("Formato targa non valido");
            }
            
            // Crea veicolo
            Veicolo veicolo = VeicoloFactory.creaVeicolo(tipo, marca, modello, anno, prezzo);
            veicolo.setTarga(targa);
            
            // Dati specifici per tipo
            if (veicolo instanceof Auto) {
                Auto auto = (Auto) veicolo;
                System.out.print("Numero porte (3/5): ");
                auto.setNumeroPorte(Integer.parseInt(scanner.nextLine()));
                
                System.out.print("Tipo cambio (Manuale/Automatico): ");
                auto.setTipoCambio(ValidatoreInput.sanitizzaStringa(scanner.nextLine()));
                
            } else if (veicolo instanceof Moto) {
                Moto moto = (Moto) veicolo;
                System.out.print("Cilindrata (cc): ");
                moto.setCilindrata(Integer.parseInt(scanner.nextLine()));
                
                System.out.print("Tipo moto (Sport/Naked/Touring): ");
                moto.setTipoMoto(ValidatoreInput.sanitizzaStringa(scanner.nextLine()));
                
            } else if (veicolo instanceof Furgone) {
                Furgone furgone = (Furgone) veicolo;
                System.out.print("Capacità carico (m³): ");
                furgone.setCapacitaCarico(Double.parseDouble(scanner.nextLine()));
                
                System.out.print("Cassone chiuso (S/N): ");
                String risposta = scanner.nextLine().toUpperCase();
                furgone.setCassoneChiuso(risposta.startsWith("S"));
            }
            
            inventario.aggiungiVeicolo(veicolo);
            System.out.println("\n✅ Veicolo aggiunto con successo!");
            
        } catch (Exception e) {
            ExceptionHandler.gestisciEccezione(e, "Aggiunta veicolo");
        }
    }
    
    private void visualizzaInventario() {
        System.out.println("\n=== INVENTARIO COMPLETO ===");
        
        if (inventario.getNumeroVeicoli() == 0) {
            System.out.println("Nessun veicolo presente in inventario.");
            return;
        }
        
        IteratoreInventario it = inventario.creaIteratore();
        int count = 1;
        while (it.hasNext()) {
            System.out.println(count++ + ". " + it.next());
        }
        
        System.out.println("\nTotale veicoli: " + inventario.getNumeroVeicoli());
    }
    
    private void ricercaVeicoli() {
        System.out.println("\n=== RICERCA VEICOLI ===");
        System.out.println("1. Ricerca per tipo");
        System.out.println("2. Ricerca per fascia di prezzo");
        System.out.println("3. Ricerca per targa");
        System.out.print("Scelta: ");
        
        int scelta = leggiScelta();
        
        try {
            switch (scelta) {
                case 1:
                    ricercaPerTipo();
                    break;
                case 2:
                    ricercaPerPrezzo();
                    break;
                case 3:
                    ricercaPerTarga();
                    break;
                default:
                    System.out.println("❌ Scelta non valida");
            }
        } catch (Exception e) {
            ExceptionHandler.gestisciEccezione(e, "Ricerca veicoli");
        }
    }
    
    private void ricercaPerTipo() {
        System.out.println("\nTipo veicolo (Auto/Moto/Furgone): ");
        String tipo = scanner.nextLine();
        
        IteratoreInventario it = inventario.creaIteratorePerTipo(tipo);
        int count = 0;
        
        System.out.println("\nRisultati ricerca:");
        while (it.hasNext()) {
            System.out.println("- " + it.next());
            count++;
        }
        
        if (count == 0) {
            System.out.println("Nessun veicolo trovato.");
        } else {
            System.out.println("\nTrovati " + count + " veicoli.");
        }
    }
    
    private void ricercaPerPrezzo() {
        System.out.print("\nPrezzo minimo (€): ");
        double min = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Prezzo massimo (€): ");
        double max = Double.parseDouble(scanner.nextLine());
        
        IteratoreInventario it = inventario.creaIteratorePerPrezzo(min, max);
        int count = 0;
        
        System.out.println("\nRisultati ricerca:");
        while (it.hasNext()) {
            System.out.println("- " + it.next());
            count++;
        }
        
        if (count == 0) {
            System.out.println("Nessun veicolo trovato nella fascia di prezzo.");
        } else {
            System.out.println("\nTrovati " + count + " veicoli.");
        }
    }
    
    private void ricercaPerTarga() {
        System.out.print("\nInserisci targa: ");
        String targa = scanner.nextLine().toUpperCase();
        
        Veicolo v = inventario.cercaPerTarga(targa);
        if (v != null) {
            System.out.println("\nVeicolo trovato:");
            System.out.println(v);
        } else {
            System.out.println("\n❌ Nessun veicolo trovato con targa " + targa);
        }
    }
    
    private void rimuoviVeicolo() {
        System.out.println("\n=== RIMUOVI VEICOLO ===");
        System.out.print("Inserisci targa del veicolo da rimuovere: ");
        String targa = scanner.nextLine().toUpperCase();
        
        try {
            inventario.rimuoviVeicolo(targa);
            System.out.println("\n✅ Veicolo rimosso con successo!");
        } catch (Exception e) {
            ExceptionHandler.gestisciEccezione(e, "Rimozione veicolo");
        }
    }
    
    private void visualizzaCatalogo() {
        System.out.println("\n=== CATALOGO PER CATEGORIE ===");
        inventario.getCatalogoPrincipale().visualizza();
    }
    
    private void caricaDati() {
        try {
            List<Veicolo> veicoli = FileManager.caricaInventario();
            for (Veicolo v : veicoli) {
                inventario.aggiungiVeicolo(v);
            }
            if (veicoli.size() > 0) {
                System.out.println("✅ Caricati " + veicoli.size() + " veicoli da file.");
            }
        } catch (Exception e) {
            LOGGER.warning("Impossibile caricare i dati: " + e.getMessage());
        }
    }
    
    private void salvaDati() {
        try {
            FileManager.salvaInventario(inventario.getVeicoli());
            System.out.println("\n✅ Dati salvati con successo!");
        } catch (Exception e) {
            ExceptionHandler.gestisciEccezione(e, "Salvataggio dati");
        }
    }
}