use MedicalCenterBD;

-- Servicii pentru medicul 1 (Cardiologie)
INSERT INTO ServiciiMedicale (id_serviciu, nume_serviciu, id_medic, pret, durata_minute)
VALUES
(1, 'Consultatie cardiologie', 1, 250.00, 30),
(2, 'Echocardiografie', 1, 300.00, 40),
(3, 'Test de efort', 1, 400.00, 60),
(4, 'Monitorizare holter EKG', 1, 350.00, 24 * 60); -- Monitorizare 24 de ore

-- Servicii pentru medicul 2 (Pediatrie)
INSERT INTO ServiciiMedicale (id_serviciu, nume_serviciu, id_medic, pret, durata_minute)
VALUES
(5, 'Consultatie pediatrie', 2, 200.00, 30),
(6, 'Vaccinare copil', 2, 150.00, 20),
(7, 'Control preventiv copil', 2, 180.00, 30),
(8, 'Evaluare dezvoltare copil', 2, 250.00, 40);

-- Servicii pentru medicul 3 (Dermatologie)
INSERT INTO ServiciiMedicale (id_serviciu, nume_serviciu, id_medic, pret, durata_minute)
VALUES
(9, 'Consultatie dermatologie', 3, 220.00, 30),
(10, 'Dermatoscopie', 3, 270.00, 40),
(11, 'Excizie leziune cutanata', 3, 400.00, 60),
(12, 'Tratament laser pentru acnee', 3, 350.00, 50);