package com.concessionaria;

import com.concessionaria.service.GestioneInventario;
import com.concessionaria.model.*;
import com.concessionaria.factory.VeicoloFactory;
import com.concessionaria.util.ValidatoreInput;
import com.concessionaria.io.FileManager;
import com.concessionaria.exception.ConcessionariaException;
import com.concessionaria.iterator.IteratoreInventario;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static GestioneInventario gestioneInventario = new GestioneInventario();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== SISTEMA GESTIONE CONCESSIONARIA ===");
        
        // Carica inventario esistente
        try {
            List<Veicolo> veicoliCaricati = FileManager.caricaInventario();
            for (Veicolo v : veicoliCaricati) {
                try {
                    gestioneInventario.aggiungiVeicolo(v);
                } catch (ConcessionariaException e) {
                    System.err.println("Errore caricamento veicolo: " + e.getMessage());
                }
            }
            System.out.println("Caricati " + veicoliCaricati.size() + " veicoli dall'inventario.");
        } catch (ConcessionariaException e) {
            System.out.println("Nessun inventario esistente trovato. Inizio con inventario vuoto.");
        }
        
        boolean esci = false;
        
        while (!esci) {
            mostraMenu();
            int scelta = leggiScelta();
            
            try {
                switch (scelta) {
                    case 1:
                        aggiungiVeicolo();
                        break;
                    case 2:
                        visualizzaInventario();
                        break;
                    case 3:
                        cercaVeicolo();
                        break;
                    case 4:
                        rimuoviVeicolo();
                        break;
                    case 5:
                        visualizzaPerTipo();
                        break;
                    case 6:
                        visualizzaPerPrezzo();
                        break;
                    case 7:
                        salvaInventario();
                        break;
                    case 8:
                        esci = true;
                        salvaInventario(); // Salva automaticamente all'uscita
                        System.out.println("Arrivederci!");
                        break;
                    default:
                        System.out.println("Scelta non valida!");
                }
            } catch (Exception e) {
                System.err.println("Errore: " + e.getMessage());
            }
            
            if (!esci) {
                System.out.println("\nPremi INVIO per continuare...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }

    private static void mostraMenu() {
        System.out.println("\n=== MENU PRINCIPALE ===");
        System.out.println("1. Aggiungi veicolo");
        System.out.println("2. Visualizza inventario completo");
        System.out.println("3. Cerca veicolo per targa");
        System.out.println("4. Rimuovi veicolo");
        System.out.println("5. Visualizza veicoli per tipo");
        System.out.println("6. Visualizza veicoli per fascia di prezzo");
        System.out.println("7. Salva inventario");
        System.out.println("8. Esci");
        System.out.print("Scelta: ");
    }

    private static int leggiScelta() {
        try {
            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline
            return scelta;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Pulisci il buffer
            return -1;
        }
    }

    private static void aggiungiVeicolo() {
        System.out.println("\n=== AGGIUNGI VEICOLO ===");
        
        // Tipo veicolo
        String tipo = "";
        while (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
            System.out.print("Tipo (AUTO/MOTO/FURGONE): ");
            tipo = scanner.nextLine().toUpperCase().trim();
            if (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
                System.out.println("ERRORE: Tipo non valido! Inserire AUTO, MOTO o FURGONE");
            }
        }
        
        // Marca
        String marca = "";
        while (marca.isEmpty()) {
            System.out.print("Marca: ");
            marca = scanner.nextLine().trim();
            if (marca.isEmpty()) {
                System.out.println("ERRORE: La marca non può essere vuota!");
            }
        }
        
        // Modello
        String modello = "";
        while (modello.isEmpty()) {
            System.out.print("Modello: ");
            modello = scanner.nextLine().trim();
            if (modello.isEmpty()) {
                System.out.println("ERRORE: Il modello non può essere vuoto!");
            }
        }
        
        // Anno
        int anno = 0;
        boolean annoValido = false;
        while (!annoValido) {
            System.out.print("Anno di immatricolazione: ");
            try {
                anno = scanner.nextInt();
                scanner.nextLine(); // Consuma newline
                
                if (!ValidatoreInput.validaAnno(anno)) {
                    System.out.println("ERRORE: Anno non valido! Deve essere tra 1900 e l'anno corrente");
                } else {
                    annoValido = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("ERRORE: Inserire un numero valido!");
                scanner.nextLine(); // Pulisci buffer
            }
        }
        
        // Prezzo
        double prezzo = 0;
        boolean prezzoValido = false;
        while (!prezzoValido) {
            System.out.print("Prezzo: ");
            try {
                prezzo = scanner.nextDouble();
                scanner.nextLine(); // Consuma newline
                
                if (!ValidatoreInput.validaPrezzo(prezzo)) {
                    System.out.println("ERRORE: Il prezzo deve essere maggiore di 0!");
                } else {
                    prezzoValido = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("ERRORE: Inserire un numero valido!");
                scanner.nextLine(); // Pulisci buffer
            }
        }
        
        try {
            // Crea il veicolo base
            Veicolo veicolo = VeicoloFactory.creaVeicolo(tipo, marca, modello, anno, prezzo);
            
            // Richiedi informazioni aggiuntive in base al tipo
            if (veicolo instanceof Auto) {
                Auto auto = (Auto) veicolo;
                
                System.out.print("Numero di porte (2/3/4/5): ");
                int porte = scanner.nextInt();
                scanner.nextLine();
                auto.setNumeroPorte(porte);
                
                System.out.print("Tipo cambio (Manuale/Automatico): ");
                String cambio = scanner.nextLine().trim();
                auto.setTipoCambio(cambio);
                
            } else if (veicolo instanceof Moto) {
                Moto moto = (Moto) veicolo;
                
                System.out.print("Cilindrata: ");
                int cilindrata = scanner.nextInt();
                scanner.nextLine();
                moto.setCilindrata(cilindrata);
                
                System.out.print("Tipo moto (Sport/Touring/Naked/Enduro): ");
                String tipoMoto = scanner.nextLine().trim();
                moto.setTipoMoto(tipoMoto);
                
            } else if (veicolo instanceof Furgone) {
                Furgone furgone = (Furgone) veicolo;
                
                System.out.print("Capacità di carico (kg): ");
                double capacita = scanner.nextDouble();
                scanner.nextLine();
                furgone.setCapacitaCarico(capacita);
                
                System.out.print("Cassone chiuso? (S/N): ");
                String cassone = scanner.nextLine().trim().toUpperCase();
                furgone.setCassoneChiuso(cassone.equals("S"));
            }
            
            // Targa - controllata per ultima per evitare duplicati
            String targa = "";
            boolean targaValida = false;
            while (!targaValida) {
                System.out.print("Targa: ");
                targa = scanner.nextLine().toUpperCase().trim();
                
                if (targa.isEmpty()) {
                    System.out.println("ERRORE: La targa non può essere vuota!");
                } else if (!ValidatoreInput.validaTarga(targa)) {
                    System.out.println("ERRORE: Formato targa non valido! Deve essere nel formato XX123XX");
                } else if (gestioneInventario.cercaPerTarga(targa) != null) {
                    System.out.println("ERRORE: La targa " + targa + " è già presente nell'inventario!");
                } else {
                    targaValida = true;
                    veicolo.setTarga(targa);
                }
            }
            
            // Aggiungi il veicolo
            gestioneInventario.aggiungiVeicolo(veicolo);
            System.out.println("\nVeicolo aggiunto con successo!");
            System.out.println(veicolo);
            
        } catch (ConcessionariaException e) {
            System.err.println("Errore nell'aggiunta del veicolo: " + e.getMessage());
        }
    }

    private static void visualizzaInventario() {
        System.out.println("\n=== INVENTARIO COMPLETO ===");
        
        if (gestioneInventario.getNumeroVeicoli() == 0) {
            System.out.println("L'inventario è vuoto.");
            return;
        }
        
        IteratoreInventario iteratore = gestioneInventario.creaIteratore();
        int count = 1;
        
        while (iteratore.hasNext()) {
            Veicolo v = iteratore.next();
            System.out.println("\n" + count + ". " + v);
            count++;
        }
        
        System.out.println("\nTotale veicoli: " + gestioneInventario.getNumeroVeicoli());
        System.out.println("Valore totale catalogo: €" + 
            String.format("%.2f", gestioneInventario.getCatalogoPrincipale().getPrezzoTotale()));
    }

    private static void cercaVeicolo() {
        System.out.println("\n=== CERCA VEICOLO ===");
        
        System.out.print("Inserisci la targa da cercare: ");
        String targa = scanner.nextLine().trim();
        
        if (targa.isEmpty()) {
            System.out.println("ERRORE: La targa non può essere vuota!");
            return;
        }
        
        Veicolo veicolo = gestioneInventario.cercaPerTarga(targa);
        
        if (veicolo != null) {
            System.out.println("\nVeicolo trovato:");
            System.out.println(veicolo);
        } else {
            System.out.println("\nNessun veicolo trovato con targa: " + targa);
        }
    }

    private static void rimuoviVeicolo() {
        System.out.println("\n=== RIMUOVI VEICOLO ===");
        
        System.out.print("Inserisci la targa del veicolo da rimuovere: ");
        String targa = scanner.nextLine().trim();
        
        if (targa.isEmpty()) {
            System.out.println("ERRORE: La targa non può essere vuota!");
            return;
        }
        
        // Mostra prima il veicolo che si sta per rimuovere
        Veicolo veicolo = gestioneInventario.cercaPerTarga(targa);
        if (veicolo != null) {
            System.out.println("\nVeicolo da rimuovere:");
            System.out.println(veicolo);
            
            System.out.print("\nConfermi la rimozione? (S/N): ");
            String conferma = scanner.nextLine().trim().toUpperCase();
            
            if (conferma.equals("S")) {
                try {
                    gestioneInventario.rimuoviVeicolo(targa);
                    System.out.println("Veicolo rimosso con successo!");
                } catch (ConcessionariaException e) {
                    System.err.println("Errore nella rimozione: " + e.getMessage());
                }
            } else {
                System.out.println("Rimozione annullata.");
            }
        } else {
            System.out.println("Nessun veicolo trovato con targa: " + targa);
        }
    }

    private static void visualizzaPerTipo() {
        System.out.println("\n=== VISUALIZZA PER TIPO ===");
        
        System.out.print("Inserisci il tipo (AUTO/MOTO/FURGONE): ");
        String tipo = scanner.nextLine().toUpperCase().trim();
        
        if (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
            System.out.println("Tipo non valido!");
            return;
        }
        
        IteratoreInventario iteratore = gestioneInventario.creaIteratorePerTipo(tipo);
        int count = 0;
        
        System.out.println("\n--- " + tipo + " ---");
        while (iteratore.hasNext()) {
            Veicolo v = iteratore.next();
            System.out.println(v);
            count++;
        }
        
        if (count == 0) {
            System.out.println("Nessun veicolo di tipo " + tipo + " trovato.");
        } else {
            System.out.println("\nTotale " + tipo + ": " + count);
        }
    }

    private static void visualizzaPerPrezzo() {
        System.out.println("\n=== VISUALIZZA PER FASCIA DI PREZZO ===");
        
        double prezzoMin = 0;
        double prezzoMax = 0;
        
        try {
            System.out.print("Prezzo minimo: ");
            prezzoMin = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Prezzo massimo: ");
            prezzoMax = scanner.nextDouble();
            scanner.nextLine();
            
            if (prezzoMin < 0 || prezzoMax < prezzoMin) {
                System.out.println("Range di prezzo non valido!");
                return;
            }
            
        } catch (InputMismatchException e) {
            System.out.println("ERRORE: Inserire un numero valido!");
            scanner.nextLine();
            return;
        }
        
        IteratoreInventario iteratore = gestioneInventario.creaIteratorePerPrezzo(prezzoMin, prezzoMax);
        int count = 0;
        
        System.out.println("\n--- Veicoli tra €" + prezzoMin + " e €" + prezzoMax + " ---");
        while (iteratore.hasNext()) {
            Veicolo v = iteratore.next();
            System.out.println(v);
            count++;
        }
        
        if (count == 0) {
            System.out.println("Nessun veicolo in questa fascia di prezzo.");
        } else {
            System.out.println("\nTotale veicoli trovati: " + count);
        }
    }

    private static void salvaInventario() {
        try {
            FileManager.salvaInventario(gestioneInventario.getVeicoli());
            System.out.println("Inventario salvato con successo!");
        } catch (ConcessionariaException e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }
}