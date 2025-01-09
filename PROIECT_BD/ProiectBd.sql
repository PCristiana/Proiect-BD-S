-- Șterge baza de date existentă și creează una nouă
DROP DATABASE IF EXISTS MedicalCenterBD;
CREATE DATABASE MedicalCenterBD;
USE MedicalCenterBD;

-- Tabelul Unități Medicale
DROP TABLE IF EXISTS UnitatiMedicale;
CREATE TABLE UnitatiMedicale (
    id_unitate INT AUTO_INCREMENT PRIMARY KEY,
    nume_unitate VARCHAR(100) NOT NULL,
    adresa VARCHAR(150),
    program_functionare TEXT,
    descriere_servicii TEXT
);

-- Tabelul Utilizatori
DROP TABLE IF EXISTS Utilizator;
CREATE TABLE Utilizator (
    CNP VARCHAR(50) PRIMARY KEY,
    Nume VARCHAR(50) NOT NULL,
    Prenume VARCHAR(50) NOT NULL,
    DataAngajare DATE NOT NULL,
    ContractNumar INT NOT NULL,
    Contact VARCHAR(15) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Adresa VARCHAR(255) NOT NULL,
    IBAN VARCHAR(50) NOT NULL,
    Pozitie VARCHAR(50) NOT NULL,
    id_unitate INT,
    tip_utilizator ENUM('admin', 'super-admin', 'angajat') NOT NULL,
    FOREIGN KEY (id_unitate) REFERENCES UnitatiMedicale(id_unitate)
);

-- Tabelul Departamente
DROP TABLE IF EXISTS Departamente;
CREATE TABLE Departamente (
    id_departament INT AUTO_INCREMENT PRIMARY KEY,
    nume_departament VARCHAR(50) NOT NULL
);

-- Tabelul Angajați
DROP TABLE IF EXISTS Angajati;
CREATE TABLE Angajati (
    id_angajat INT AUTO_INCREMENT PRIMARY KEY,
    CNP VARCHAR(50) NOT NULL,
    id_departament INT NOT NULL,
    salariu_negociat DECIMAL(10, 2),
    ore_lucrate INT,
    FOREIGN KEY (CNP) REFERENCES Utilizator(CNP),
    FOREIGN KEY (id_departament) REFERENCES Departamente(id_departament)
);

-- Tabelul Asistenți Medicali
DROP TABLE IF EXISTS AsistentiMedicali;
CREATE TABLE AsistentiMedicali (
    id_asistent INT AUTO_INCREMENT PRIMARY KEY,
    id_angajat INT NOT NULL,
    tip_asistent ENUM('generalist', 'laborator', 'radiologie'),
    grad ENUM('principal', 'secundar'),
    FOREIGN KEY (id_angajat) REFERENCES Angajati(id_angajat)
);

-- Tabelul Medici
DROP TABLE IF EXISTS Medic;
CREATE TABLE Medic (
    id_medic INT AUTO_INCREMENT PRIMARY KEY,
    id_angajat INT NOT NULL,
    specialitate VARCHAR(100) NOT NULL,
    grad ENUM('specialist', 'primar'),
    cod_parafa VARCHAR(20) UNIQUE,
    competente TEXT,
    titlu_stiintific ENUM('doctorand', 'doctor'),
    post_didactic ENUM('preparator', 'asistent', 'lector', 'conferentiar', 'profesor'),
    procent_servicii DECIMAL(5, 2),
    FOREIGN KEY (id_angajat) REFERENCES Angajati(id_angajat)
);

-- Tabelul Servicii Medicale
DROP TABLE IF EXISTS ServiciiMedicale;
CREATE TABLE ServiciiMedicale (
    id_serviciu INT AUTO_INCREMENT PRIMARY KEY,
    nume_serviciu VARCHAR(100) NOT NULL,
    id_medic INT NOT NULL,
    pret DECIMAL(10, 2),
    durata_minute INT,
    necesitate_competenta BOOLEAN,
    FOREIGN KEY (id_medic) REFERENCES Medic(id_medic)
);

-- Tabelul Pacienți
DROP TABLE IF EXISTS Clienti;
CREATE TABLE Clienti (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    nume_client VARCHAR(100) NOT NULL,
    prenume_client VARCHAR(100) NOT NULL,
    CNP VARCHAR(50) UNIQUE,
    contact VARCHAR(15),
    email VARCHAR(100),
    adresa TEXT
);

-- Tabelul Programări
DROP TABLE IF EXISTS Programari;
CREATE TABLE Programari (
    id_programare INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    id_serviciu INT NOT NULL,
    data_programare DATE NOT NULL,
    ora_programare TIME NOT NULL,
    status ENUM('planificata', 'realizata', 'anulata') DEFAULT 'planificata',
    FOREIGN KEY (id_client) REFERENCES Clienti(id_client),
    FOREIGN KEY (id_serviciu) REFERENCES ServiciiMedicale(id_serviciu)
);

-- Tabelul Plăți
DROP TABLE IF EXISTS Plati;
CREATE TABLE Plati (
    id_plata INT AUTO_INCREMENT PRIMARY KEY,
    id_programare INT NOT NULL,
    suma DECIMAL(10, 2) NOT NULL,
    data_plata DATE NOT NULL,
    metoda_plata ENUM('numerar', 'card', 'transfer'),
    FOREIGN KEY (id_programare) REFERENCES Programari(id_programare)
);

-- Tabelul Orar Angajați
DROP TABLE IF EXISTS OrarAngajati;
CREATE TABLE OrarAngajati (
    id_orar INT AUTO_INCREMENT PRIMARY KEY,
    id_angajat INT NOT NULL,
    zi_saptamana ENUM('Luni', 'Marti', 'Miercuri', 'Joi', 'Vineri', 'Sambata', 'Duminica'),
    data_specifica DATE,
    ora_inceput TIME NOT NULL,
    ora_sfarsit TIME NOT NULL,
    id_unitate INT NOT NULL,
    FOREIGN KEY (id_angajat) REFERENCES Angajati(id_angajat),
    FOREIGN KEY (id_unitate) REFERENCES UnitatiMedicale(id_unitate)
);

-- Tabelul Concedii Angajați
DROP TABLE IF EXISTS ConcediiAngajati;
CREATE TABLE ConcediiAngajati (
    id_concediu INT AUTO_INCREMENT PRIMARY KEY,
    id_angajat INT NOT NULL,
    data_inceput DATE NOT NULL,
    data_sfarsit DATE NOT NULL,
    motiv VARCHAR(255),
    FOREIGN KEY (id_angajat) REFERENCES Angajati(id_angajat)
);

-- Tabelul Autentificare
DROP TABLE IF EXISTS Autentificare;
CREATE TABLE Autentificare (
    id_autentificare INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    parola_hash VARCHAR(255) NOT NULL,
    id_utilizator VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_utilizator) REFERENCES Utilizator(CNP)
);

-- View pentru calculul profitului operațional
CREATE VIEW ProfitOperational AS
SELECT 
    SUM(Plati.suma) AS Venituri,
    SUM(Angajati.salariu_negociat) AS Cheltuieli,
    (SUM(Plati.suma) - SUM(Angajati.salariu_negociat)) AS Profit
FROM Plati
LEFT JOIN Angajati ON 1=1;
