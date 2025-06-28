# Gestione Concessionaria

Progetto d'esame di OOP - Alberto Giunta

## Cosa fa

Un programma per gestire i veicoli di una concessionaria (auto, moto, furgoni).
Ho scelto questo progetto perchè avendo da poco comprato una macchina, e appassionato di motori, mi sarebbe venuto più semplice inziare a fare tutto, e avendo fatto oop in java alle superiori, molti esempi ed esercizi erano sulle auto

## Come si usa

1. Compilare:
```bash
cd concessionaria
mvn compile
```

2. Eseguire:
```bash
mvn exec:java
```

3. Test:
```bash
mvn test
```

## Funzioni principali

- Aggiungi veicoli
- Rimuovi veicoli  
- Visualizza inventario
- Cerca per tipo o prezzo
- Salva/carica da file

## Pattern usati

- **Factory**: per creare i veicoli
- **Composite**: per organizzare in categorie
- **Iterator**: per scorrere l'inventario
- **Exception Shielding**: per gestire gli errori

## Tecnologie

- Java 8
- Maven
- JUnit per i test
- File CSV per salvare i dati

## Struttura

```
src/
├── main/java/com/concessionaria/
│   ├── model/          # Veicoli
│   ├── factory/        # Creazione veicoli
│   ├── composite/      # Categorie
│   ├── iterator/       # Iteratori
│   ├── service/        # Logica principale
│   └── io/             # File
└── test/               # Test
```

## Diagramma delle classi

```
                    <<abstract>>
                     Veicolo
                 ________________
                |    -marca      |
                |    -modello    |
                |    -anno       |
                |    -prezzo     |
                |    -targa      |
                |________________|
                        ▲
                        |
        ┌───────────────┼───────────────┐
        |               |               |
     Auto            Moto           Furgone
   __________      __________      __________
  | -porte   |    | -cilindrata|  | -capacita|
  | -cambio  |    | -tipo      |  | -chiuso  |
  |__________|    |___________|  |__________|


    VeicoloFactory              GestioneInventario
   ________________            ____________________
  | +creaVeicolo() |◄─────────| -veicoli: List     |
  |________________|          | -catalogo: Composite|
                              | +aggiungi()         |
                              | +rimuovi()          |
                              |____________________|
                                        |
                                        ▼
                              ComponenteCatalogo
                              ________________
                              | +mostra()      |
                              |________________|
                                   ▲        ▲
                                   |        |
                        CategoriaVeicoli  VeicoloFoglia
```

## Note

- Solo interfaccia testuale
- I dati si salvano in inventario.csv
- Tutti i 59 test passano