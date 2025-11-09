-- seed.sql — Données d’exemple pour DentalSoft
-- Exécuter après avoir créé le schéma (tables déjà présentes).

SET NAMES utf8mb4;
SET time_zone = "+00:00";

START TRANSACTION;

-- 0) Base ENTITE (une seule entité utilisée partout)
INSERT INTO entite (date_creation, date_derniere_modification, modifie_par, cree_par)
VALUES (CURDATE(), NOW(), 'seed', 'seed');
SET @entite_id := LAST_INSERT_ID();

-- 1) ROLE (valeurs uniques)
INSERT INTO role (id_entite, libelle) VALUES
                                          (@entite_id, 'ADMIN'),
                                          (@entite_id, 'MEDECIN'),
                                          (@entite_id, 'SECRETAIRE'),
                                          (@entite_id, 'PATIENT');

-- Récupérer les ids des rôles par libellé
SELECT id_role INTO @role_admin   FROM role WHERE libelle='ADMIN'     LIMIT 1;
SELECT id_role INTO @role_medecin FROM role WHERE libelle='MEDECIN'   LIMIT 1;
SELECT id_role INTO @role_secr    FROM role WHERE libelle='SECRETAIRE'LIMIT 1;
SELECT id_role INTO @role_patient FROM role WHERE libelle='PATIENT'   LIMIT 1;

-- 2) UTILISATEURS (admin, medecin, secretaire)
INSERT INTO utilisateur
(id_entite, id_role, nom, email, adresse, cin, telephone, sexe, login, mot_de_passe, dernier_login, date_naissance)
VALUES
    (@entite_id, @role_admin,   'Admin Système',  'admin@clinic.test',   '1 Rue Centrale', 'CIN0001', '+212600000001', 'MASCULIN', 'admin',   'admin123', CURDATE(), '1985-01-01'),
    (@entite_id, @role_medecin, 'Dr. Amal Noor',  'amal.noor@clinic.test','2 Rue Santé',   'CIN0002', '+212600000002', 'FEMININ',  'anoor',   'passmed',  CURDATE(), '1986-06-12'),
    (@entite_id, @role_secr,    'Sanaa Berrada',  'sanaa@clinic.test',    '3 Rue Accueil', 'CIN0003', '+212600000003', 'FEMININ',  'sberrada','passsec',  CURDATE(), '1990-03-05');

-- Conserver l’id d’un médecin pour les RDV/Consultations
SELECT id_user INTO @medecin_id FROM utilisateur WHERE id_role=@role_medecin LIMIT 1;

-- 3) PATIENTS
INSERT INTO patient
(id_entite, nom, date_de_naissance, sexe, adresse, telephone, assurance)
VALUES
    (@entite_id, 'Youssef El Idrissi', '1992-04-10', 'MASCULIN', '10 Av. Atlas', '+212611111111', 'CNSS'),
    (@entite_id, 'Lina Zahra',         '1995-09-21', 'FEMININ',  '22 Bd. Atlas', '+212622222222', 'AUCUNE');

-- Récupérer les ids patients
SELECT id_patient INTO @p1 FROM patient WHERE nom='Youssef El Idrissi' LIMIT 1;
SELECT id_patient INTO @p2 FROM patient WHERE nom='Lina Zahra'         LIMIT 1;

-- 4) DOSSIERS MEDICAUX
INSERT INTO dossier_medicale (id_entite, id_patient, date_de_creation)
VALUES
    (@entite_id, @p1, CURDATE()),
    (@entite_id, @p2, CURDATE());

-- 5) SITUATION FINANCIERE
INSERT INTO situation_financiere
(id_entite, id_patient, totale_des_actes, totale_paye, credit, statut, en_promo)
VALUES
    (@entite_id, @p1, 1500.00, 1000.00, 500.00, 'EN_COURS',  'NON'),
    (@entite_id, @p2,  300.00,  300.00,   0.00, 'TERMINE',  'NON');

-- 6) FACTURES
INSERT INTO facture
(id_entite, id_patient, totale_facture, reste, statut, date_facture)
VALUES
    (@entite_id, @p1, 1000.00, 200.00, 'EN_COURS',  NOW()),
    (@entite_id, @p2,  300.00,   0.00, 'TERMINE',   NOW());

-- 7) RDV (avec le médecin)
INSERT INTO rdv
(id_entite, id_patient, id_medecin, date, heure, motif, statut, note_medecin)
VALUES
    (@entite_id, @p1, @medecin_id, CURDATE(), '10:30', 'Douleur molaire', 'EN_ATTENTE', NULL),
    (@entite_id, @p2, @medecin_id, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00', 'Contrôle', 'EN_COURS', 'Vérifier carie');

-- 8) CONSULTATIONS
INSERT INTO consultation
(id_entite, id_patient, id_medecin, date, statut, observation_medecin)
VALUES
    (@entite_id, @p1, @medecin_id, CURDATE(), 'EN_COURS', 'Carie dent 36, sensibilité.'),
    (@entite_id, @p2, @medecin_id, CURDATE(), 'TERMINE',  'Contrôle OK, hygiène à maintenir.');

-- Conserver ids consultations
SELECT id_consultation INTO @c1 FROM consultation WHERE id_patient=@p1 ORDER BY id_consultation DESC LIMIT 1;
SELECT id_consultation INTO @c2 FROM consultation WHERE id_patient=@p2 ORDER BY id_consultation DESC LIMIT 1;

-- 9) O
