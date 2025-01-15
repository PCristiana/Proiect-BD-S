
USE MedicalCenterBD;

-- Inserarea manuală a datelor în tabela Plati
INSERT INTO Plati (id_programare, suma, data_plata, metoda_plata)
VALUES
    (1, 200.00, '2024-12-15', 'numerar'), -- Programare cu status planificată
    (2, 150.00, '2024-12-15', 'card'), -- Programare cu status planificată
    (3, 250.00, '2024-12-16', 'transfer'), -- Programare cu status planificată
    (4, 300.00, '2024-12-16', 'numerar'), -- Programare cu status planificată
    (5, 180.00, '2024-12-17', 'card'), -- Programare cu status planificată
    (6, 220.00, '2024-12-17', 'transfer'), -- Programare cu status planificată
    (7, 210.00, '2024-12-18', 'numerar'), -- Programare cu status realizată
    (8, 190.00, '2024-12-18', 'card'), -- Programare cu status realizată
    (9, 240.00, '2024-12-19', 'transfer'), -- Programare cu status planificată
    (10, 260.00, '2024-12-19', 'numerar'), -- Programare cu status planificată
    (12, 230.00, '2024-12-20', 'card'), -- Programare cu status planificată
    (13, 210.00, '2024-12-21', 'transfer'), -- Programare cu status realizată
    (16, 300.00, '2024-12-22', 'numerar'), -- Programare cu status realizată
    (18, 250.00, '2024-12-23', 'card'), -- Programare cu status planificată
    (19, 280.00, '2024-12-24', 'transfer'); -- Programare cu status planificată
