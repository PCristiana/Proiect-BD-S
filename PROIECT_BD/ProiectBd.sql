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

-- Tabelul Utilizator
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

-- Tabelul Programări
DROP TABLE IF EXISTS Programari;
CREATE TABLE Programari (
    id_programare INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    id_serviciu INT NOT NULL,
    data_programare DATE NOT NULL,
    ora_programare TIME NOT NULL,
    status ENUM('planificata', 'realizata', 'anulata') DEFAULT 'planificata',
    FOREIGN KEY (id_client) REFERENCES Clienti(id_client) ,
    FOREIGN KEY (id_serviciu) REFERENCES ServiciiMedicale(id_serviciu) 
);

-- Tabelul Bon
DROP TABLE IF EXISTS Bon;
CREATE TABLE Bon (
    id_bon INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    id_serviciu INT NOT NULL,
    data_programare DATE NOT NULL,
    ora_programare TIME NOT NULL,
    pret DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_client) REFERENCES Clienti(id_client) ,
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

DROP TABLE IF EXISTS RaportMedical;

CREATE TABLE RaportMedical (
    id_raport INT AUTO_INCREMENT PRIMARY KEY,  
    id_client INT NOT NULL,                                  
    id_medic INT NOT NULL,                    
    id_asistent INT,                         -- Adăugarea câmpului pentru id_asistent
    tip_raport ENUM('numeric', 'binarn') NOT NULL,  
    istoric VARCHAR(255),
    simptome VARCHAR(255),
    investigatii VARCHAR(255),
    diagnostic VARCHAR(255),
    recomandari VARCHAR(255),
    data_inregistrare TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    data_consultatie DATE NOT NULL,
    validat BOOLEAN DEFAULT FALSE,          
    data_validare TIMESTAMP,                  
    FOREIGN KEY (id_client) REFERENCES Clienti(id_client), 
    FOREIGN KEY (id_medic) REFERENCES Medic(id_medic),  -- Cheie externă către tabelul Medic
    FOREIGN KEY (id_asistent) REFERENCES AsistentiMedicali(id_asistent)  -- Cheie externă către tabelul AsistentMedical
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

DELIMITER $$
CREATE TRIGGER after_programare_insert
AFTER INSERT ON Programari
FOR EACH ROW
BEGIN
    DECLARE pret_serviciu DECIMAL(10, 2);
    -- Obține prețul serviciului
    SELECT pret INTO pret_serviciu
    FROM serviciimedicale
    WHERE id_serviciu = NEW.id_serviciu;

    -- Inserează bonul
    INSERT INTO Bon (id_serviciu, data_programare, ora_programare, pret, id_client)
    VALUES (NEW.id_serviciu, NEW.data_programare, NEW.ora_programare, pret_serviciu, NEW.id_client);
END$$
DELIMITER ;


DELIMITER $$

DELIMITER $$

CREATE TRIGGER insert_plati_after_programare_insert
AFTER INSERT ON Programari
FOR EACH ROW
BEGIN

    DECLARE pret_serviciu DECIMAL(10, 2);

    -- Dacă statusul programării este 'planificata' sau 'realizata'
    IF NEW.status IN ('planificata', 'realizata') THEN
        -- Obținem prețul serviciului asociat programării
        SELECT sm.pret
        INTO pret_serviciu
        FROM ServiciiMedicale sm
        WHERE sm.id_serviciu = NEW.id_serviciu;

        -- Adăugăm o plată pentru programarea nou introdusă, cu suma egală cu prețul serviciului
        INSERT INTO Plati (id_programare, suma, data_plata, metoda_plata)
        VALUES (NEW.id_programare, pret_serviciu, CURRENT_DATE, 'numerar'); -- Exemplu de metodă de plată
    END IF;
END$$

DELIMITER ;






-- View pentru calculul profitului operațional(Diferenta dintre plati si salariu angajati
CREATE VIEW ProfitOperational AS
SELECT 
    SUM(Plati.suma) AS Venituri,
    SUM(Angajati.salariu_negociat) AS Cheltuieli,
    (SUM(Plati.suma) - SUM(Angajati.salariu_negociat)) AS Profit
FROM Plati
LEFT JOIN Angajati ON 1=1;


-- profit pe unitate medicala
CREATE VIEW ProfitPeUnitate AS
SELECT 
    u.id_unitate,
    u.nume_unitate,
    SUM(p.suma) AS Venituri,
    SUM(a.salariu_negociat * (a.ore_lucrate / 160)) AS Cheltuieli,
    (SUM(p.suma) - SUM(a.salariu_negociat * (a.ore_lucrate / 160))) AS Profit
FROM Plati p
JOIN Programari pr ON pr.id_programare = p.id_programare
JOIN ServiciiMedicale sm ON sm.id_serviciu = pr.id_serviciu
JOIN Medic m ON m.id_medic = sm.id_medic
JOIN Angajati a ON a.id_angajat = m.id_angajat  -- legătura între Medic și Angajat
JOIN Utilizator ut ON ut.CNP = a.CNP  -- legătura între Angajat și Utilizator pe baza CNP
JOIN UnitatiMedicale u ON u.id_unitate = ut.id_unitate  -- legătura între Utilizator și UnitatiMedicale
GROUP BY u.id_unitate, u.nume_unitate;

-- PROFIT PER MEDIC
CREATE OR REPLACE VIEW ProfitPeMedic AS
SELECT 
    m.id_medic,
    CONCAT(u.Nume, ' ', u.Prenume) AS NumeMedic,
    SUM(sm.pret) AS VenituriGenerale,  -- Veniturile generate din serviciile realizate
    SUM(sm.pret * (m.procent_servicii / 100)) AS ComisionNegociat,  -- Procentul negociat din serviciile realizate
    SUM(a.salariu_negociat * (a.ore_lucrate / 160)) AS SalariuCalculat,  -- Salariul calculat pe baza orelor lucrate
    (SUM(sm.pret * (m.procent_servicii / 100))) - (SUM(a.salariu_negociat * (a.ore_lucrate / 160))) AS Profit -- 160h/luna am convenit ca e contractual decis pentru toti angajatii
FROM 
    Medic m
JOIN 
    Angajati a ON m.id_angajat = a.id_angajat
JOIN 
    ServiciiMedicale sm ON sm.id_medic = m.id_medic
JOIN 
    Programari p ON p.id_serviciu = sm.id_serviciu
JOIN 
    Utilizator u ON u.CNP = a.CNP
WHERE 
    p.status IN ('planificata', 'realizata')  -- Luăm în calcul atât serviciile planificate cât și realizate
GROUP BY 
    m.id_medic, u.Nume, u.Prenume;



SELECT 
    m.specialitate AS Specialitate,
    SUM(sm.pret) AS VenituriGenerale,
    SUM(sm.pret * (m.procent_servicii / 100)) AS ComisionNegociat,
    SUM(a.salariu_negociat * (a.ore_lucrate / 160)) AS SalariuCalculat,
    (SUM(sm.pret * (m.procent_servicii / 100))) - (SUM(a.salariu_negociat * (a.ore_lucrate / 160))) AS Profit
FROM 
    Medic m
JOIN 
    Angajati a ON m.id_angajat = a.id_angajat
JOIN 
    ServiciiMedicale sm ON sm.id_medic = m.id_medic
JOIN 
    Programari p ON p.id_serviciu = sm.id_serviciu
WHERE 
    p.status IN ('planificata', 'realizata')
GROUP BY 
    m.specialitate





