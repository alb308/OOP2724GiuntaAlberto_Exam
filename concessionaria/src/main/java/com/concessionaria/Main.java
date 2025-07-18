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
                        salvaInventario(); 
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
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("ERRORE: Inserire una scelta!");
                    System.out.print("Scelta: ");
                    continue;
                }
                int scelta = Integer.parseInt(input);
                return scelta;
            } catch (NumberFormatException e) {
                System.out.println("ERRORE: Inserire un numero valido!");
                System.out.print("Scelta: ");
            }
        }
    }

    private static void aggiungiVeicolo() {
        System.out.println("\n=== AGGIUNGI VEICOLO ===");
        
        String tipo = "";
        while (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
            System.out.print("Tipo (AUTO/MOTO/FURGONE): ");
            tipo = scanner.nextLine().toUpperCase().trim();
            if (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
                System.out.println("ERRORE: Tipo non valido! Inserire AUTO, MOTO o FURGONE");
            }
        }
        
        String marca = "";
        while (marca.isEmpty()) {
            System.out.print("Marca: ");
            marca = scanner.nextLine().trim();
            if (marca.isEmpty()) {
                System.out.println("ERRORE: La marca non può essere vuota!");
            }
        }
        
        String modello = "";
        while (modello.isEmpty()) {
            System.out.print("Modello: ");
            modello = scanner.nextLine().trim();
            if (modello.isEmpty()) {
                System.out.println("ERRORE: Il modello non può essere vuoto!");
            }
        }
        
        
        int anno = 0;
        boolean annoValido = false;
        while (!annoValido) {
            System.out.print("Anno di immatricolazione: ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("ERRORE: L'anno non può essere vuoto!");
                    continue;
                }
                anno = Integer.parseInt(input);
                
                if (!ValidatoreInput.validaAnno(anno)) {
                    System.out.println("ERRORE: Anno non valido! Deve essere tra 1900 e l'anno corrente");
                } else {
                    annoValido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRORE: Inserire un numero valido per l'anno!");
            }
        }
        
        double prezzo = 0;
        boolean prezzoValido = false;
        while (!prezzoValido) {
            System.out.print("Prezzo: ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("ERRORE: Il prezzo non può essere vuoto!");
                    continue;
                }
                prezzo = Double.parseDouble(input);
                
                if (!ValidatoreInput.validaPrezzo(prezzo)) {
                    System.out.println("ERRORE: Il prezzo deve essere maggiore di 0!");
                } else {
                    prezzoValido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRORE: Inserire un numero valido per il prezzo!");
            }
        }
        
        try {
            Veicolo veicolo = VeicoloFactory.creaVeicolo(tipo, marca, modello, anno, prezzo);
            
            if (veicolo instanceof Auto) {
                Auto auto = (Auto) veicolo;
                
                int porte = 0;
                boolean porteValide = false;
                while (!porteValide) {
                    System.out.print("Numero di porte (3/5): ");
                    try {
                        String input = scanner.nextLine().trim();
                        if (input.isEmpty()) {
                            System.out.println("ERRORE: Il numero di porte non può essere vuoto!");
                            continue;
                        }
                        porte = Integer.parseInt(input);
                        
                        if (porte == 3 || porte == 5) {
                            auto.setNumeroPorte(porte);
                            porteValide = true;
                        } else {
                            System.out.println("ERRORE: Il numero di porte deve essere 3 o 5!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ERRORE: Inserire un numero valido (3 o 5)!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("ERRORE: " + e.getMessage());
                    }
                }
                
                String cambio = "";
                boolean cambioValido = false;
                while (!cambioValido) {
                    System.out.print("Tipo cambio (Manuale/Automatico): ");
                    cambio = scanner.nextLine().trim();
                    
                    if (cambio.isEmpty()) {
                        System.out.println("ERRORE: Il tipo di cambio non può essere vuoto!");
                    } else if (cambio.equalsIgnoreCase("Manuale") || cambio.equalsIgnoreCase("Automatico")) {
                        cambio = cambio.substring(0, 1).toUpperCase() + cambio.substring(1).toLowerCase();
                        auto.setTipoCambio(cambio);
                        cambioValido = true;
                    } else {
                        System.out.println("ERRORE: Il tipo di cambio deve essere 'Manuale' o 'Automatico'!");
                    }
                }
                
            } else if (veicolo instanceof Moto) {
                Moto moto = (Moto) veicolo;
                
                int cilindrata = 0;
                boolean cilindrataValida = false;
                while (!cilindrataValida) {
                    System.out.print("Cilindrata: ");
                    try {
                        String input = scanner.nextLine().trim();
                        if (input.isEmpty()) {
                            System.out.println("ERRORE: La cilindrata non può essere vuota!");
                            continue;
                        }
                        cilindrata = Integer.parseInt(input);
                        
                        if (cilindrata > 0) {
                            moto.setCilindrata(cilindrata);
                            cilindrataValida = true;
                        } else {
                            System.out.println("ERRORE: La cilindrata deve essere maggiore di 0!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ERRORE: Inserire un numero valido per la cilindrata!");
                    }
                }
                
                String tipoMoto = "";
                boolean tipoMotoValido = false;
                String[] tipiMotoValidi = {"Sport", "Touring", "Naked", "Enduro"};
                
                while (!tipoMotoValido) {
                    System.out.print("Tipo moto (Sport/Touring/Naked/Enduro): ");
                    tipoMoto = scanner.nextLine().trim();
                    
                    if (tipoMoto.isEmpty()) {
                        System.out.println("ERRORE: Il tipo di moto non può essere vuoto!");
                    } else {
                        for (String tipoValido : tipiMotoValidi) {
                            if (tipoMoto.equalsIgnoreCase(tipoValido)) {
                                tipoMoto = tipoValido;
                                moto.setTipoMoto(tipoMoto);
                                tipoMotoValido = true;
                                break;
                            }
                        }
                        
                        if (!tipoMotoValido) {
                            System.out.println("ERRORE: Il tipo di moto deve essere uno tra: Sport, Touring, Naked, Enduro!");
                        }
                    }
                }
                
            } else if (veicolo instanceof Furgone) {
                Furgone furgone = (Furgone) veicolo;
                
                double capacita = 0;
                boolean capacitaValida = false;
                while (!capacitaValida) {
                    System.out.print("Capacità di carico (m³): ");
                    try {
                        String input = scanner.nextLine().trim();
                        if (input.isEmpty()) {
                            System.out.println("ERRORE: La capacità di carico non può essere vuota!");
                            continue;
                        }
                        capacita = Double.parseDouble(input);
                        
                        if (capacita > 0) {
                            furgone.setCapacitaCarico(capacita);
                            capacitaValida = true;
                        } else {
                            System.out.println("ERRORE: La capacità di carico deve essere maggiore di 0!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ERRORE: Inserire un numero valido per la capacità!");
                    }
                }
                
                boolean cassoneValido = false;
                while (!cassoneValido) {
                    System.out.print("Cassone chiuso? (S/N): ");
                    String cassone = scanner.nextLine().trim().toLowerCase();
                    if (cassone.equals("s") || cassone.equals("si") || cassone.equals("sì")) {
                        furgone.setCassoneChiuso(true);
                        cassoneValido = true;
                    } else if (cassone.equals("n") || cassone.equals("no")) {
                        furgone.setCassoneChiuso(false);
                        cassoneValido = true;
                    } else {
                        System.out.println("ERRORE: Rispondere S (sì) o N (no)!");
                    }
                }
            }
            
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
        
        String targa = "";
        while (targa.isEmpty()) {
            System.out.print("Inserisci la targa da cercare: ");
            targa = scanner.nextLine().trim().toUpperCase();
            
            if (targa.isEmpty()) {
                System.out.println("ERRORE: La targa non può essere vuota!");
            }
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
        
        String targa = "";
        while (targa.isEmpty()) {
            System.out.print("Inserisci la targa del veicolo da rimuovere: ");
            targa = scanner.nextLine().trim().toUpperCase();
            
            if (targa.isEmpty()) {
                System.out.println("ERRORE: La targa non può essere vuota!");
            }
        }
        
        Veicolo veicolo = gestioneInventario.cercaPerTarga(targa);
        if (veicolo != null) {
            System.out.println("\nVeicolo da rimuovere:");
            System.out.println(veicolo);
            
            String conferma = "";
            boolean confermaValida = false;
            while (!confermaValida) {
                System.out.print("\nConfermi la rimozione? (S/N): ");
                conferma = scanner.nextLine().trim().toUpperCase();
                
                if (conferma.isEmpty()) {
                    System.out.println("ERRORE: Inserire S per confermare o N per annullare!");
                } else if (conferma.equals("S") || conferma.equals("SI") || conferma.equals("SÌ")) {
                    try {
                        gestioneInventario.rimuoviVeicolo(targa);
                        System.out.println("Veicolo rimosso con successo!");
                        confermaValida = true;
                    } catch (ConcessionariaException e) {
                        System.err.println("Errore nella rimozione: " + e.getMessage());
                        confermaValida = true;
                    }
                } else if (conferma.equals("N") || conferma.equals("NO")) {
                    System.out.println("Rimozione annullata.");
                    confermaValida = true;
                } else {
                    System.out.println("ERRORE: Rispondere S (sì) o N (no)!");
                }
            }
        } else {
            System.out.println("Nessun veicolo trovato con targa: " + targa);
        }
    }

    private static void visualizzaPerTipo() {
        System.out.println("\n=== VISUALIZZA PER TIPO ===");
        
        String tipo = "";
        while (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
            System.out.print("Inserisci il tipo (AUTO/MOTO/FURGONE): ");
            tipo = scanner.nextLine().toUpperCase().trim();
            
            if (tipo.isEmpty()) {
                System.out.println("ERRORE: Il tipo non può essere vuoto!");
            } else if (!tipo.equals("AUTO") && !tipo.equals("MOTO") && !tipo.equals("FURGONE")) {
                System.out.println("ERRORE: Tipo non valido! Inserire AUTO, MOTO o FURGONE");
            }
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
        
        boolean prezzoMinValido = false;
        while (!prezzoMinValido) {
            System.out.print("Prezzo minimo: ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("ERRORE: Il prezzo minimo non può essere vuoto!");
                    continue;
                }
                prezzoMin = Double.parseDouble(input);
                
                if (prezzoMin < 0) {
                    System.out.println("ERRORE: Il prezzo minimo non può essere negativo!");
                } else {
                    prezzoMinValido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRORE: Inserire un numero valido per il prezzo minimo!");
            }
        }
                boolean prezzoMaxValido = false;
        while (!prezzoMaxValido) {
            System.out.print("Prezzo massimo: ");
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("ERRORE: Il prezzo massimo non può essere vuoto!");
                    continue;
                }
                prezzoMax = Double.parseDouble(input);
                
                if (prezzoMax < 0) {
                    System.out.println("ERRORE: Il prezzo massimo non può essere negativo!");
                } else if (prezzoMax < prezzoMin) {
                    System.out.println("ERRORE: Il prezzo massimo deve essere maggiore o uguale al prezzo minimo!");
                } else {
                    prezzoMaxValido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRORE: Inserire un numero valido per il prezzo massimo!");
            }
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