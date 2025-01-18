INSERT INTO Utilizator (CNP, Nume, Prenume, DataAngajare, ContractNumar, Contact, Email, Adresa, IBAN, Pozitie, id_unitate, tip_utilizator)
VALUES
-- Admini
('3134567891001', 'Popescu', 'Ion', '2015-06-01', 2001, '0721000001', 'popescu.ion@admin.ro', 'Strada Libertatii 1, Bucuresti', 'RO49AAAA1B31007593841000', 'Expert contabil', 1, 'admin'),
('3134567891002', 'Ionescu', 'Maria', '2016-07-10', 2002, '0721000002', 'ionescu.maria@admin.ro', 'Strada Victoriei 2, Cluj-Napoca', 'RO49AAAA1B31007593841001', 'Inspector Resurse Umane', 2, 'super-admin'),
('3134567891003', 'Vasilescu', 'Andrei', '2017-05-20', 2003, '0721000003', 'vasilescu.andrei@admin.ro', 'Strada Independentei 3, Timisoara', 'RO49AAAA1B31007593841002', 'Expert Contabil', 3, 'admin'),
('3134567891004', 'Dumitrescu', 'Mihai', '2018-03-15', 2004, '0721000004', 'dumitrescu.mihai@admin.ro', 'Strada Pacii 4, Iasi', 'RO49AAAA1B31007593841003', 'Expert contabil', 4, 'admin'),
('3134567891005', 'Georgescu', 'Elena', '2019-04-25', 2005, '0721000005', 'georgescu.elena@admin.ro', 'Strada Industriei 5, Brasov', 'RO49AAAA1B31007593841004', 'Medic', 5, 'admin'),

('3134567891006', 'Stoica', 'Cristian', '2014-01-15', 3001, '0721000006', 'stoica.cristian@superadmin.ro', 'Strada Principala 6, Constanta', 'RO49AAAA1B31007593841005', 'Receptionier', 1, 'angajat'),

-- Angajați
('4134567892001', 'Marinescu', 'Raluca', '2020-05-15', 4001, '0722000001', ' ', 'Strada Pacii 10, Bucuresti', 'RO49AAAA1B31007593842000', 'Medic', 1, 'angajat'),
('4134567892002', 'Popa', 'Dragos', '2021-03-20', 4002, '0722000002', 'popa.dragos@gmail.com', 'Strada Eroilor 12, Cluj-Napoca', 'RO49AAAA1B31007593842001', 'Asistent Medical', 2, 'angajat'),
('4134567892003', 'Lungu', 'Simona', '2019-09-01', 4003, '0722000003', 'lungu.simona@gmail.com', 'Strada Independentei 22, Timisoara', 'RO49AAAA1B31007593842002', 'Inspector Resurse Umane', 3, 'angajat'),
('4134567892004', 'Grigorescu', 'Adrian', '2021-01-10', 4004, '0722000004', 'grigorescu.adrian@gmail.com', 'Strada Pacii 15, Iasi', 'RO49AAAA1B31007593842003', 'Medic', 4, 'angajat'),
('4134567892005', 'Radu', 'Mihai', '2020-12-10', 4005, '0722000005', 'radu.mihai@gmail.com', 'Strada Libertatii 18, Brasov', 'RO49AAAA1B31007593842004', 'Asistent Medical', 5, 'angajat'),
('4134567892006', 'Ciobanu', 'Ana', '2019-08-05', 4006, '0722000006', 'ciobanu.ana@gmail.com', 'Strada Pacii 30, Constanta', 'RO49AAAA1B31007593842005', 'Medic', 1, 'angajat'),
('4134567892007', 'Radulescu', 'Teodor', '2022-02-15', 4007, '0722000007', 'radulescu.teodor@gmail.com', 'Strada Pacii 50, Bucuresti', 'RO49AAAA1B31007593842006', 'Receptioner', 2, 'angajat'),
('4134567892008', 'Iliescu', 'Oana', '2022-06-20', 4008, '0722000008', 'iliescu.oana@gmail.com', 'Strada Pacii 80, Cluj-Napoca', 'RO49AAAA1B31007593842007', 'Asistent Medical', 3, 'angajat'),
('4134567892009', 'Enache', 'Mihail', '2021-03-30', 4009, '0722000009', 'enache.mihail@gmail.com', 'Strada Pacii 90, Timisoara', 'RO49AAAA1B31007593842008', 'Medic', 4, 'angajat'),
('4134567892010', 'Constantin', 'Roxana', '2018-10-05', 4010, '0722000010', 'constantin.roxana@gmail.com', 'Strada Pacii 100, Iasi', 'RO49AAAA1B31007593842009', 'Receptionier', 5, 'angajat'),
('4134567892011', 'Badea', 'Marius', '2021-12-10', 4011, '0722000011', 'badea.marius@gmail.com', 'Strada Pacii 110, Brasov', 'RO49AAAA1B31007593842010', 'Medic', 1, 'angajat'),
('4134567892012', 'Tudor', 'Alina', '2022-09-01', 4012, '0722000012', 'tudor.alina@gmail.com', 'Strada Pacii 120, Constanta', 'RO49AAAA1B31007593842011', 'Asistent Medical', 2, 'angajat');
