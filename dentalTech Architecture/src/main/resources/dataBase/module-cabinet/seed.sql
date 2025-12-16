-- seed.sql (jeu de données de test – module Cabinet)
-- Pour Cabinet_Staff, je suppose que tu as déjà des Staffs avec id 1, 2, 3. Sinon, commente ou adapte la partie correspondante.

-- ==========================================================
--  Jeu de données de test - Module Cabinet
-- ==========================================================

-- -----------------------
--  Cabinets de test
-- -----------------------
INSERT INTO Cabinets
(id, nom, email, logo, adresse, cin, tel1, tel2, siteWeb, instagram, facebook, description,
 dateCreation, dateDerniereModification, creePar, modifiePar)
VALUES
    (1,
     'Cabinet Dentaire Al Amal',
     'contact@alamal.ma',
     NULL,
     'Avenue Hassan II, Agdal, Rabat',
     'X123456',
     '0611000001',
     '0611000002',
     'https://www.alamal-dentaire.ma',
     '@alamal_dentaire',
     'facebook.com/alamal.dentaire',
     'Cabinet dentaire familial moderne, orienté prévention et esthétique.',
     '2025-10-20',
     '2025-10-20 09:00:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (2,
     'Cabinet Sourire Plus',
     'info@sourireplus.ma',
     NULL,
     'Bd Mohammed V, Centre-ville, Salé',
     'Y654321',
     '0622000001',
     NULL,
     'https://sourireplus.ma',
     '@sourireplus',
     'facebook.com/sourireplus',
     'Cabinet spécialisé en implantologie et orthodontie.',
     '2025-10-21',
     '2025-10-21 10:30:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (3,
     'Cli-Dent Témara',
     'contact@cliddent.ma',
     NULL,
     'Lotissement Wifaq, Témara',
     'Z789456',
     '0633000001',
     '0633000002',
     NULL,
     NULL,
     NULL,
     'Cabinet de proximité axé sur les soins conservateurs.',
     '2025-10-22',
     '2025-10-22 11:15:00',
     'SYSTEM',
     'SYSTEM'
    );


-- -----------------------
--  Charges de test
-- -----------------------
INSERT INTO Charges
(id, titre, description, montant, date, cabinet_id,
 dateCreation, dateDerniereModification, creePar, modifiePar)
VALUES
    (1,
     'Loyer mensuel',
     'Loyer du local pour le mois d’octobre 2025',
     12000.00,
     '2025-10-01 09:00:00',
     1,
     '2025-10-01',
     '2025-10-01 09:05:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (2,
     'Facture électricité',
     'Électricité septembre 2025',
     1800.50,
     '2025-10-05 14:30:00',
     1,
     '2025-10-05',
     '2025-10-05 14:35:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (3,
     'Achat de consommables',
     'Gants, masques, seringues, etc.',
     3200.75,
     '2025-10-08 10:15:00',
     2,
     '2025-10-08',
     '2025-10-08 10:20:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (4,
     'Maintenance fauteuil',
     'Maintenance annuelle du fauteuil dentaire principal',
     2500.00,
     '2025-10-10 16:00:00',
     2,
     '2025-10-10',
     '2025-10-10 16:10:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (5,
     'Nettoyage / désinfection',
     'Contrat de désinfection mensuelle des locaux',
     900.00,
     '2025-10-12 18:30:00',
     3,
     '2025-10-12',
     '2025-10-12 18:35:00',
     'SYSTEM',
     'SYSTEM'
    );


-- -----------------------
--  Revenues de test
-- -----------------------
INSERT INTO Revenues
(id, titre, description, montant, date, cabinet_id,
 dateCreation, dateDerniereModification, creePar, modifiePar)
VALUES
    (1,
     'Consultations journalières',
     'Recettes des consultations du 15/10/2025',
     7500.00,
     '2025-10-15 19:30:00',
     1,
     '2025-10-15',
     '2025-10-15 19:35:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (2,
     'Actes de prothèse',
     'Recettes des prothèses posées semaine 42',
     14200.00,
     '2025-10-18 17:45:00',
     1,
     '2025-10-18',
     '2025-10-18 17:50:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (3,
     'Implantologie',
     'Recettes liées aux implants d’octobre',
     22000.00,
     '2025-10-20 16:10:00',
     2,
     '2025-10-20',
     '2025-10-20 16:15:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (4,
     'Soins conservateurs',
     'Recettes des soins caries / détartrage',
     6800.00,
     '2025-10-19 12:00:00',
     3,
     '2025-10-19',
     '2025-10-19 12:05:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (5,
     'Urgences week-end',
     'Consultations d’urgences (samedi-dimanche)',
     4300.00,
     '2025-10-26 22:00:00',
     3,
     '2025-10-26',
     '2025-10-26 22:05:00',
     'SYSTEM',
     'SYSTEM'
    );


-- -----------------------
--  Statistiques de test
-- -----------------------
-- Remarque : la colonne categorie doit contenir les noms des valeurs
-- de ton enum CategorieStatistique (ex : 'CHIFFRE_AFFAIRE_MENSUEL', etc.)
INSERT INTO Statistiques
(id, nom, categorie, chiffre, dateCalcul, cabinet_id,
 dateCreation, dateDerniereModification, creePar, modifiePar)
VALUES
    (1,
     'Chiffre d’affaire octobre 2025',
     'CHIFFRE_AFFAIRE_MENSUEL',
     43700.00,
     '2025-10-31',
     1,
     '2025-10-31',
     '2025-10-31 23:00:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (2,
     'Nombre de patients nouveaux - octobre 2025',
     'NOMBRE_NOUVEAUX_PATIENTS',
     35.00,
     '2025-10-31',
     1,
     '2025-10-31',
     '2025-10-31 23:10:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (3,
     'Chiffre d’affaire implantologie - octobre 2025',
     'CHIFFRE_AFFAIRE_IMPLANTS',
     22000.00,
     '2025-10-31',
     2,
     '2025-10-31',
     '2025-10-31 23:20:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (4,
     'Taux de rendez-vous honorés',
     'TAUX_RDV_HONORES',
     92.50,
     '2025-10-31',
     2,
     '2025-10-31',
     '2025-10-31 23:25:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (5,
     'Chiffre d’affaire soins conservateurs - octobre 2025',
     'CHIFFRE_AFFAIRE_SOIN_CONSERVATEUR',
     6800.00,
     '2025-10-31',
     3,
     '2025-10-31',
     '2025-10-31 23:30:00',
     'SYSTEM',
     'SYSTEM'
    ),
    (6,
     'Nombre total de consultations - octobre 2025',
     'NOMBRE_CONSULTATIONS_MENSUEL',
     120.00,
     '2025-10-31',
     3,
     '2025-10-31',
     '2025-10-31 23:35:00',
     'SYSTEM',
     'SYSTEM'
    );


-- -----------------------
--  Cabinet_Staff (Many-to-Many)
-- -----------------------
-- ⚠️ Nécessite que les Staffs avec id = 1,2,3 existent déjà.
-- Sinon, commente ou adapte ces lignes.
INSERT INTO Cabinet_Staff (cabinet_id, staff_id) VALUES
                                                     (1, 1),  -- Staff 1 travaille dans Al Amal
                                                     (1, 2),  -- Staff 2 aussi
                                                     (2, 2),  -- Staff 2 partagé avec Sourire Plus
                                                     (2, 3),  -- Staff 3 dans Sourire Plus
                                                     (3, 3);  -- Staff 3 intervient aussi à Cli-Dent Témara
