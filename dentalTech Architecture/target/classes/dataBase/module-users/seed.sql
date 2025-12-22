-- ==========================================================
-- ROLES
-- ==========================================================
INSERT INTO Roles (id, libelle, type, dateCreation, creePar)
VALUES
    (1, 'ROLE_ADMIN',      'ADMIN',      '2026-01-01', 'SYSTEM'),
    (2, 'ROLE_MEDECIN',    'MEDECIN',    '2026-01-01', 'SYSTEM'),
    (3, 'ROLE_SECRETAIRE', 'SECRETAIRE', '2026-01-01', 'SYSTEM');

INSERT INTO Role_Privileges VALUES
                                (1, 'GESTION_UTILISATEURS'),
                                (2, 'GESTION_PATIENTS'),
                                (1, 'GESTION_CABINET'),

                                (2, 'CONSULTER_PATIENTS'),
                                (2, 'GERER_DOSSIERS'),
                                (2, 'GERER_RDV'),

                                (3, 'GERER_RDV');

INSERT INTO Utilisateurs
(id, nom, email, adresse, cin, tel, sexe, login, motDePasse, dateNaissance, dateCreation)
VALUES
    (1, 'Omar El Midaoui', 'admin.omar@dentaltech.ma',
     'Rabat Agdal', 'X1234567', '0612345678', 'Femme',
     'admin.omar', 'hashedpassword', '1994-04-10', '2026-01-01');

INSERT INTO Utilisateurs
(id, nom, email, adresse, cin, tel, sexe, login, motDePasse, dateNaissance, dateCreation)
VALUES
    (2, 'Dr Oumaima El Midaoui', 'med.oumaima@dentaltech.ma',
     'Rabat Hassan', 'X7654321', '0678901234', 'Femme',
     'dr.oumaima', 'hashedpassword', '1994-04-10', '2026-01-01');

INSERT INTO Utilisateurs
(id, nom, email, adresse, cin, tel, sexe, login, motDePasse, dateNaissance, dateCreation)
VALUES
    (3, 'Fatima Zahra Ben Ali', 'fz.secretariat@dentaltech.ma',
     'Salé Tabriquet', 'C998877', '0654321098', 'Femme',
     'fz.sec', 'pwd', '1998-02-20', '2026-01-01');

INSERT INTO Staffs VALUES
                       (1, 20000.00, 3000.00, '2020-01-10', 15),
                       (2, 25000.00, 2500.00, '2021-02-15', 12),
                       (3, 8000.00, 300.00,  '2022-10-01', 20);

INSERT INTO Admins VALUES (1);

INSERT INTO Medecins (id, specialite) VALUES
    (2, 'Chirurgie dentaire – Implantologie');

INSERT INTO Secretaires (id, numCNSS, commission) VALUES
    (3, 'CNSS009988', 5.0);

INSERT INTO Utilisateur_Roles VALUES
                                  (1, 1),
                                  (2, 2),
                                  (3, 3); -- Le user standard peut avoir un rôle médecin fictif pour tests
