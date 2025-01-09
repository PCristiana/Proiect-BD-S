use MedicalCenterBD;

INSERT INTO Departamente (id_departament, nume_departament)
VALUES
(1, 'Resurse Umane'),
(2, 'Contabilitate'),
(3, 'Personal Medical');

INSERT INTO Angajati (id_angajat, CNP, id_departament, salariu_negociat, ore_lucrate)
VALUES
(1, '2234567890001', 1, 8500.00, 160),
(2, '2234567890002', 2, 4000.00, 140),
(3, '2234567890003', 3, 3000.00, 150),
(4, '2234567890004', 1, 9500.00, 170),
(5, '2234567890005', 2, 4200.00, 160),
(6, '2234567890006', 1, 8700.00, 150),
(7, '2234567890007', 2, 3100.00, 160),
(8, '2234567890008', 3, 4000.00, 140),
(9, '2234567890009', 2, 9600.00, 170),
(10, '2234567890010', 1, 3300.00, 160),
(11, '2234567890011', 1, 8900.00, 150),
(12, '2234567890012', 2, 4300.00, 140);


INSERT INTO AsistentiMedicali (id_asistent, id_angajat, tip_asistent, grad)
VALUES
(1, 2, 'generalist', 'principal'), -- Asistent generalist, principal
(2, 5, 'laborator', 'secundar'), -- Asistent laborator, secundar
(3, 8, 'radiologie', 'principal'); -- Asistent radiologie, principal


INSERT INTO Medic (id_medic, id_angajat, specialitate, grad, cod_parafa, competente, titlu_stiintific, post_didactic, procent_servicii)
VALUES
(1, 1, 'Cardiologie', 'specialist', 'PARAFA001', 'Echocardiografie, Teste de efort', 'doctor', 'lector', 25.00), -- Medic specialist cardiolog
(2, 4, 'Pediatrie', 'primar', 'PARAFA002', 'Consultatii copii, Urgente pediatrice', 'doctor', 'profesor', 30.00), -- Medic primar pediatru
(3, 6, 'Dermatologie', 'specialist', 'PARAFA003', 'Dermatoscopie, Biopsii cutanate', 'doctorand', 'asistent', 20.00); -- Medic specialist dermatolog


