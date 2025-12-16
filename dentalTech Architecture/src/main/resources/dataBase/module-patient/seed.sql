
-- Jeu de données de test
INSERT INTO Antecedents (nom, categorie, niveauRisque) VALUES
-- Allergies
('Allergie à la pénicilline', 'ALLERGIE', 'CRITIQUE'),
('Allergie au latex', 'ALLERGIE', 'ELEVE'),
('Allergie aux anesthésiques locaux', 'ALLERGIE', 'CRITIQUE'),

-- Maladies chroniques
('Diabète de type 2', 'MALADIE_CHRONIQUE', 'MODERE'),
('Hypertension artérielle', 'MALADIE_CHRONIQUE', 'MODERE'),
('Asthme', 'MALADIE_CHRONIQUE', 'ELEVE'),

-- Contre-indications ou traitements
('Sous traitement anticoagulant', 'CONTRE_INDICATION', 'ELEVE'),
('Grossesse', 'CONTRE_INDICATION', 'MODERE'),

-- Antécédents chirurgicaux ou infectieux
('Prothèse valvulaire cardiaque', 'ANTECEDENT_CHIRURGICAL', 'ELEVE'),
('Hépatite B ancienne', 'ANTECEDENT_INFECTIEUX', 'MODERE'),

-- Habitudes de vie
('Tabagisme chronique', 'HABITUDE_DE_VIE', 'MODERE'),
('Alcoolisme', 'HABITUDE_DE_VIE', 'ELEVE');


-- Jeu de données de test (IDs fixés pour faciliter les liaisons Many-to-Many)
INSERT INTO Patients
(id, nom, prenom, adresse, telephone, email, dateNaissance, dateCreation, sexe, assurance)
VALUES
    (1, 'Amal',    'Zahra',   'Rabat',      '0611111111', 'amal@example.com',   '1995-05-12', '2025-10-25 21:35:39', 'Femme', 'CNSS'),
    (2, 'Omar',    'Badr',    'Salé',       '0622222222', 'omar@example.com',   '1989-09-23', '2025-10-25 20:40:39', 'Homme', 'CNOPS'),
    (3, 'Nour',    'Chafi',   'Témara',     '0633333333', 'nour@example.com',   '2000-02-02', '2025-10-24 21:10:39', 'Femme', 'Autre'),
    (4, 'Youssef', 'Dari',    'Kénitra',    '0644444444', 'youssef@example.com','1992-11-01', '2025-10-23 21:40:39', 'Homme', 'Aucune'),
    (5, 'Hiba',    'Zerouali','Rabat',      '0655555555', 'hiba@example.com',   '2001-03-14', '2025-10-26 10:00:00', 'Femme', 'CNSS'),
    (6, 'Mahdi',   'ElMidaoui','Casablanca','0666666666', 'mahdi@example.com',  '1990-07-18', '2025-10-26 10:05:00', 'Homme', 'Autre');

-- Patient 1 : Amal (diabétique et allergique au latex)
INSERT INTO Patient_Antecedents VALUES (1, 2), (1, 4);

-- Patient 2 : Omar (hypertension, tabagisme, diabétique)
INSERT INTO Patient_Antecedents VALUES (2, 5), (2, 11) , (2, 2);

-- Patient 3 : Nour (grossesse, allergie pénicilline)Patients
INSERT INTO Patient_Antecedents VALUES (3, 1), (3, 8);

-- Patient 4 : Youssef (prothèse valvulaire cardiaque)
INSERT INTO Patient_Antecedents VALUES (4, 9);

