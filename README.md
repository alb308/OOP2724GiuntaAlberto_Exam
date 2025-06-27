# Sistema di Gestione Concessionaria

## Overview del Progetto

Sistema di gestione per una concessionaria di veicoli sviluppato in Java SE, che permette di gestire un inventario di automobili, motociclette e furgoni. L'applicazione implementa i principali pattern di design e segue le best practice di programmazione sicura.

## Funzionalità Principali

- **Gestione Inventario**: Aggiunta, rimozione e ricerca di veicoli
- **Categorizzazione**: Organizzazione per tipo di veicolo (Auto, Moto, Furgone)
- **Persistenza Dati**: Salvataggio/caricamento inventario su file CSV
- **Ricerca Avanzata**: Filtri per tipo e fascia di prezzo
- **Validazione Input**: Controllo rigoroso di tutti i dati inseriti
- **Gestione Sicura**: Exception handling e logging completo

## Architettura e Design Patterns

### Pattern Implementati

#### 1. Factory Pattern
- **Classe**: `VeicoloFactory`
- **Motivazione**: Centralizza la creazione di veicoli, permettendo di aggiungere nuovi tipi senza modificare il codice client
- **Benefici**: Riduce l'accoppiamento e facilita l'estensibilità

#### 2. Composite Pattern
- **Classi**: `ComponenteCatalogo`, `CategoriaVeicoli`, `VeicoloFoglia`
- **Motivazione**: Gestisce la struttura gerarchica del catalogo veicoli
- **Benefici**: Permette di trattare uniformemente singoli veicoli e categorie

#### 3. Iterator Pattern
- **Classi**: `IteratoreInventario`, `IteratoreVeicoliConcreto`, `IteratoreFiltrato`
- **Motivazione**: Fornisce accesso sequenziale agli elementi senza esporre la struttura interna
- **Benefici**: Supporta diversi tipi di iterazione (completa, per tipo, per prezzo)

#### 4. Exception Shielding
- **Classe**: `ConcessionariaException`
- **Motivazione**: Protegge l'applicazione da eccezioni non gestite
- **Benefici**: Migliora la sicurezza e l'esperienza utente

## Tecnologie Utilizzate

### Core Java Technologies
- **Collections Framework**: ArrayList, HashMap, HashSet per gestione dati
- **Generics**: Type safety nelle collezioni e iteratori
- **Java I/O**: FileWriter/FileReader per persistenza dati
- **Logging**: java.util.logging per tracciamento operazioni
- **JUnit**: Framework di testing per validazione componenti

### Sicurezza
- **Input Sanitization**: Validazione rigorosa di tutti gli input utente
- **No Hardcoded Secrets**: Nessuna credenziale nel codice
- **Controlled Exception Propagation**: Gestione appropriata delle eccezioni

## Diagramma delle Classi

```
┌─────────────────┐
│    Veicolo      │ (abstract)
├─────────────────┤
│ -marca          │
│ -modello        │
│ -anno           │
│ -prezzo         │
│ -targa          │
└─────────────────┘
         ▲
         │
    ┌────┴────┬─────────┐
    │         │         │
┌───▼──┐  ┌──▼──┐  ┌───▼────┐
│ Auto │  │Moto │  │Furgone │
└──────┘  └─────┘  └────────┘

┌──────────────────────┐      ┌─────────────────┐
│  VeicoloFactory      │      │ ValidatoreInput │
├──────────────────────┤      ├─────────────────┤
│ +creaVeicolo()       │      │ +validaTarga()  │
└──────────────────────┘      │ +validaAnno()   │
                              │ +validaPrezzo() │
                              └─────────────────┘

┌──────────────────────┐
│ GestioneInventario   │
├──────────────────────┤
│ -veicoli: List       │◆────── IteratoreInventario
│ -catalogo: Composite │
├──────────────────────┤
│ +aggiungiVeicolo()   │
│ +rimuoviVeicolo()    │
│ +cercaPerTarga()     │
└──────────────────────┘
```

## Architettura del Sistema

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│    Main     │────▶│  Controller  │────▶│   Service   │
│   (View)    │     │   (Logic)    │     │   (Data)    │
└─────────────┘     └──────────────┘     └─────────────┘
                            │                     │
                            ▼                     ▼
                    ┌──────────────┐     ┌─────────────┐
                    │  Validator   │     │ FileManager │
                    └──────────────┘     └─────────────┘
```

## Setup e Esecuzione

### Prerequisiti
- Java 8 o superiore
- Maven 3.6+

### Compilazione
```bash
mvn clean compile
```

### Esecuzione
```bash
mvn exec:java
```

### Testing
```bash
mvn test
```

## Struttura del Progetto

```
concessionaria/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/concessionaria/
│   │           ├── Main.java
│   │           ├── model/
│   │           ├── factory/
│   │           ├── composite/
│   │           ├── iterator/
│   │           ├── service/
│   │           ├── exception/
│   │           ├── util/
│   │           └── io/
│   └── test/
│       └── java/
│           └── com/concessionaria/
│               └── test/
└── inventario.csv (generato automaticamente)
```

## Limitazioni e Sviluppi Futuri

### Limitazioni Attuali
- Interfaccia solo testuale (CLI)
- Persistenza solo su file CSV
- Gestione mono-utente
- Ricerca limitata a targa e filtri base

### Possibili Miglioramenti
- Interfaccia grafica (JavaFX/Swing)
- Database relazionale per persistenza
- Sistema multi-utente con autenticazione
- Report e statistiche avanzate
- API REST per integrazione con altri sistemi
- Implementazione pattern Observer per notifiche
- Strategy pattern per calcolo prezzi/sconti

## Decisioni di Design

### Perché Factory Pattern?
Scelto per centralizzare la logica di creazione dei veicoli e facilitare l'aggiunta di nuovi tipi senza modificare il codice esistente.

### Perché Composite Pattern?
Ideale per rappresentare la struttura gerarchica del catalogo, permettendo operazioni uniformi su singoli veicoli e categorie.

### Perché Iterator Pattern?
Fornisce un modo standard per attraversare le collezioni, con la flessibilità di implementare diversi tipi di iterazione.

### Perché CSV per la persistenza?
Semplice da implementare e sufficiente per un progetto di questa scala. Facilmente estendibile a database in futuro.

## Testing

I test coprono:
- Creazione veicoli tramite Factory
- Validazione input
- Operazioni CRUD sull'inventario
- Funzionamento del Composite pattern
- Iteratori e filtri

## Sicurezza

- **Input Validation**: Tutti gli input sono validati prima dell'uso
- **Exception Handling**: Nessuna eccezione non gestita raggiunge l'utente
- **Logging**: Tutte le operazioni critiche sono loggate
- **No SQL Injection**: Uso di file CSV previene injection attacks
- **Sanitizzazione**: Caratteri speciali rimossi dagli input

## Autore

Alberto Giunta - OOP2724 Exam Project

---

Per domande o chiarimenti, contattare tramite GitHub Issues.