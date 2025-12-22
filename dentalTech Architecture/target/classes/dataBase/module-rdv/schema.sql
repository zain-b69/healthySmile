-- ==========================================================
-- MODULE AGENDAS : AgendaMensuel + jours non disponibles
-- parfaitement aligné avec l'enum Java Mois
-- ==========================================================


-- ==========================================================
-- TABLE AgendaMensuels
-- ==========================================================
CREATE TABLE IF NOT EXISTS AgendaMensuels (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                              mois ENUM(
                                                  'JANVIER','FEVRIER','MARS','AVRIL','MAI','JUIN',
                                                  'JUILLET','AOUT','SEPTEMBRE','OCTOBRE','NOVEMBRE','DECEMBRE'
                                                  ) NOT NULL,

                                              annee INT NOT NULL,

                                              medecin_id BIGINT NOT NULL,

    -- Un seul agenda mensuel par médecin et par mois et par année :
                                              UNIQUE KEY uk_agenda_unique (medecin_id, annee, mois),

                                              CONSTRAINT fk_agenda_medecin
                                                  FOREIGN KEY (medecin_id) REFERENCES Medecins(id)
                                                      ON DELETE CASCADE
                                                      ON UPDATE CASCADE,

                                              KEY idx_agenda_annee_mois (annee, mois)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- TABLE Agenda_JoursNonDisponibles
-- Représente AgendaMensuel.joursNonDisponibles (List<LocalDate>)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Agenda_JoursNonDisponibles (
                                                          agenda_id BIGINT NOT NULL,
                                                          jour DATE NOT NULL,

                                                          PRIMARY KEY (agenda_id, jour),

                                                          CONSTRAINT fk_ajnd_agenda
                                                              FOREIGN KEY (agenda_id) REFERENCES AgendaMensuels(id)
                                                                  ON DELETE CASCADE
                                                                  ON UPDATE CASCADE,

                                                          KEY idx_joursnon_disponibles (jour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS RDV (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                   date DATE NOT NULL,
                                   heure TIME NOT NULL,
                                   motif VARCHAR(255),
                                   statut ENUM('PLANIFIE','CONFIRME','EN_ATTENTE','ANNULE','TERMINE','ABSENT') NOT NULL,

                                   noteMedecin TEXT,

                                   patient_id BIGINT NOT NULL,
                                   medecin_id BIGINT NOT NULL,
                                   dossier_id BIGINT,
                                   agenda_id BIGINT NOT NULL,

    -- Champs BaseEntity
                                   dateCreation DATE DEFAULT CURRENT_DATE,
                                   dateDerniereModification DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   creePar VARCHAR(80),
                                   modifiePar VARCHAR(80),

    -- Foreign Keys
                                   CONSTRAINT fk_rdv_patient
                                       FOREIGN KEY (patient_id) REFERENCES Patients(id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE,

                                   CONSTRAINT fk_rdv_medecin
                                       FOREIGN KEY (medecin_id) REFERENCES Medecins(id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE,

                                   CONSTRAINT fk_rdv_dossier
                                       FOREIGN KEY (dossier_id) REFERENCES DossierMedical(id)
                                           ON DELETE SET NULL
                                           ON UPDATE CASCADE,

                                   CONSTRAINT fk_rdv_agenda
                                       FOREIGN KEY (agenda_id) REFERENCES AgendaMensuels(id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE,

    -- Index utiles
                                   KEY idx_rdv_date (date),
                                   KEY idx_rdv_medecin_date (medecin_id, date),
                                   KEY idx_rdv_agenda (agenda_id)
);
