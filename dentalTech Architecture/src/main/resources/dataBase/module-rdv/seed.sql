-- Agenda du Dr Oumaima – JANVIER 2026
INSERT INTO AgendaMensuels (id, mois, annee, medecin_id)
VALUES (1, 'JANVIER', 2026, 2);

-- Génération automatique des WEEKENDS de Janvier 2026
-- Samedis & dimanches :
INSERT INTO Agenda_JoursNonDisponibles VALUES
                                           (1, '2026-01-03'),
                                           (1, '2026-01-04'),
                                           (1, '2026-01-10'),
                                           (1, '2026-01-11'),
                                           (1, '2026-01-17'),
                                           (1, '2026-01-18'),
                                           (1, '2026-01-24'),
                                           (1, '2026-01-25'),
                                           (1, '2026-01-31');

-- Fêtes nationales (Maroc – janvier)
INSERT INTO Agenda_JoursNonDisponibles VALUES
                                           (1, '2026-01-01'),  -- Nouvel an
                                           (1, '2026-01-11');  -- Manifeste de l’indépendance


-- RDV 1 – Amal – Détartrage – Confirmé
INSERT INTO RDV
(id, date, heure, motif, statut, noteMedecin, patient_id, medecin_id, dossier_id, agenda_id)
VALUES
    (1, '2026-01-06', '10:00:00', 'Détartrage et contrôle annuel',
     'CONFIRME', 'Prévoir analyse gingivale.', 1, 2, 1, 1);

-- RDV 1 – Amal – Détartrage – Confirmé
INSERT INTO RDV
(id, date, heure, motif, statut, noteMedecin,
 patient_id, medecin_id, dossier_id, agenda_id)
VALUES
    (1, '2026-01-12', '10:00:00',
     'Détartrage et contrôle annuel',
     'CONFIRME', 'Prévoir analyse gingivale.',
     1, 2, 1, 1);

-- RDV 2 – Omar – Douleur molaire – En attente

INSERT INTO RDV
(id, date, heure, motif, statut, noteMedecin, patient_id, medecin_id, dossier_id, agenda_id)
VALUES
    (2, '2026-01-08', '14:30:00', 'Douleur molaire - suspicion carie profonde',
     'EN_ATTENTE', 'Éventuelle radio panoramique.', 2, 2, 2,1);

-- RDV 3 – Nour – Contrôle orthodontie – Planifié
INSERT INTO RDV
(id, date, heure, motif, statut, noteMedecin, patient_id, medecin_id, dossier_id, agenda_id)
VALUES
    (3, '2026-01-14', '09:00:00', 'Contrôle orthodontique',
     'PLANIFIE', NULL, 3, 2, 3,1);

