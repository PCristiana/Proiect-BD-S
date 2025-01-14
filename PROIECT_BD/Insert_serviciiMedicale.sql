use MedicalCenterBD;


INSERT INTO ServiciiMedicale (id_serviciu, nume_serviciu, id_medic, pret, durata_minute)
VALUES
(13, 'Consultatie cardiologie', 1, 250.00, 30),
(14, 'Echocardiografie', 1, 300.00, 40),
(15, 'Test de efort', 4, 400.00, 60),
(16, 'Monitorizare holter EKG', 4, 350.00, 24 * 60); -- Monitorizare 24 de ore


INSERT INTO ServiciiMedicale (id_serviciu, nume_serviciu, id_medic, pret, durata_minute)
VALUES
(17, 'Consultatie pediatrie', 2, 200.00, 30),
(18, 'Vaccinare copil', 2, 150.00, 20),
(19, 'Control preventiv copil', 5, 180.00, 30),
(20, 'Evaluare dezvoltare copil', 5, 250.00, 40);


INSERT INTO ServiciiMedicale (id_serviciu, nume_serviciu, id_medic, pret, durata_minute)
VALUES
(21, 'Consultatie dermatologie', 3, 220.00, 30),
(22, 'Dermatoscopie', 3, 270.00, 40),
(23, 'Excizie leziune cutanata', 6, 400.00, 60),
(24, 'Tratament laser pentru acnee', 6, 350.00, 50),
(25, 'Consultatie ortopedie', 4, 240.00, 30),
(26, 'Tratament fractura', 4, 500.00, 60),
(27, 'Consultatie neurologie', 5, 260.00, 30),
(28, 'Electroencefalografie (EEG)', 5, 320.00, 50),
(29, 'Consultatie oftalmologie', 6, 230.00, 30),
(30, 'Corectie laser miopie', 6, 800.00, 90);