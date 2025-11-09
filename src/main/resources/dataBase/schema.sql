-- Schéma MySQL pour DentalSoft (version française, avec héritage logique)

-- ----------------------------------------------------
-- 1. Table ENTITE (Héritage de base)
-- ----------------------------------------------------
CREATE TABLE entite (
                        id_entite BIGINT PRIMARY KEY AUTO_INCREMENT,
                        date_creation DATE,
                        date_derniere_modification DATETIME,
                        modifie_par VARCHAR(100),
                        cree_par VARCHAR(100)
);

-- ----------------------------------------------------
-- 2. Table ROLE
-- ----------------------------------------------------
CREATE TABLE role (
                      id_role BIGINT PRIMARY KEY AUTO_INCREMENT,
                      id_entite BIGINT,
                      libelle ENUM('ADMIN','MEDECIN','SECRETAIRE','PATIENT') NOT NULL UNIQUE, -- Rôle doit être unique et non null
                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

-- ----------------------------------------------------
-- 3. Table UTILISATEUR (Doit être liée au ROLE)
-- ----------------------------------------------------
CREATE TABLE utilisateur (
                             id_user BIGINT PRIMARY KEY AUTO_INCREMENT,
                             id_entite BIGINT,
                             id_role BIGINT NOT NULL, -- CLE ETRANGERE VERS ROLE AJOUTEE
                             nom VARCHAR(150) NOT NULL, -- NOT NULL ajouté
                             email VARCHAR(150) NOT NULL UNIQUE, -- NOT NULL ajouté
                             adresse VARCHAR(255),
                             cin VARCHAR(50),
                             telephone VARCHAR(30),
                             sexe ENUM('MASCULIN','FEMININ'),
                             login VARCHAR(100) UNIQUE,
                             mot_de_passe VARCHAR(255) NOT NULL, -- NOT NULL ajouté
                             dernier_login DATE,
                             date_naissance DATE,

                             FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                             FOREIGN KEY (id_role) REFERENCES role(id_role)
);

-- ----------------------------------------------------
-- 4. Table PATIENT
-- ----------------------------------------------------
CREATE TABLE patient (
                         id_patient BIGINT PRIMARY KEY AUTO_INCREMENT,
                         id_entite BIGINT,
                         nom VARCHAR(150) NOT NULL, -- NOT NULL ajouté
                         date_de_naissance DATE NOT NULL, -- NOT NULL ajouté
                         sexe ENUM('MASCULIN','FEMININ'),
                         adresse VARCHAR(255),
                         telephone VARCHAR(30),
                         assurance ENUM('CNSS','CIMR','RAMED','AUCUNE'),
                         FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

-- ----------------------------------------------------
-- 5. Table DOSSIER MEDICALE (Lien Patient CONFIRMÉ)
-- ----------------------------------------------------
CREATE TABLE dossier_medicale (
                                  id_dm BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  id_entite BIGINT,
                                  id_patient BIGINT NOT NULL, -- LIEN VERS PATIENT AJOUTÉ
                                  date_de_creation DATE,
                                  FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                                  FOREIGN KEY (id_patient) REFERENCES patient(id_patient)
);

-- ----------------------------------------------------
-- 6. Table SITUATION FINANCIERE (Lien Patient AJOUTÉ)
-- ----------------------------------------------------
CREATE TABLE situation_financiere (
                                      id_sf BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      id_entite BIGINT,
                                      id_patient BIGINT NOT NULL, -- LIEN VERS PATIENT AJOUTÉ
                                      totale_des_actes DOUBLE,
                                      totale_paye DOUBLE,
                                      credit DOUBLE,
                                      statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                                      en_promo ENUM('OUI','NON'),
                                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                                      FOREIGN KEY (id_patient) REFERENCES patient(id_patient)
);

-- ----------------------------------------------------
-- 7. Table FACTURE (Lien Patient AJOUTÉ)
-- ----------------------------------------------------
CREATE TABLE facture (
                         id_facture BIGINT PRIMARY KEY AUTO_INCREMENT,
                         id_entite BIGINT,
                         id_patient BIGINT NOT NULL, -- LIEN VERS PATIENT AJOUTÉ
                         totale_facture DOUBLE,
                         reste DOUBLE,
                         statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                         date_facture DATETIME,
                         FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                         FOREIGN KEY (id_patient) REFERENCES patient(id_patient)
);

-- ----------------------------------------------------
-- 8. Table RDV (Lien Patient et Médecin AJOUTÉS)
-- ----------------------------------------------------
CREATE TABLE rdv (
                     id_rdv BIGINT PRIMARY KEY AUTO_INCREMENT,
                     id_entite BIGINT,
                     id_patient BIGINT NOT NULL, -- LIEN VERS PATIENT AJOUTÉ
                     id_medecin BIGINT NOT NULL, -- LIEN VERS UTILISATEUR (MEDECIN) AJOUTÉ
                     date DATE NOT NULL,
                     heure VARCHAR(20),
                     motif VARCHAR(255),
                     statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                     note_medecin VARCHAR(255),
                     FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                     FOREIGN KEY (id_patient) REFERENCES patient(id_patient),
                     FOREIGN KEY (id_medecin) REFERENCES utilisateur(id_user)
);

-- ----------------------------------------------------
-- 9. Table CONSULTATION (Lien Patient et Médecin AJOUTÉS)
-- ----------------------------------------------------
CREATE TABLE consultation (
                              id_consultation BIGINT PRIMARY KEY AUTO_INCREMENT,
                              id_entite BIGINT,
                              id_patient BIGINT NOT NULL, -- LIEN VERS PATIENT AJOUTÉ
                              id_medecin BIGINT NOT NULL, -- LIEN VERS UTILISATEUR (MEDECIN) AJOUTÉ
                              date DATE NOT NULL,
                              statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                              observation_medecin VARCHAR(255),
                              FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                              FOREIGN KEY (id_patient) REFERENCES patient(id_patient),
                              FOREIGN KEY (id_medecin) REFERENCES utilisateur(id_user)
);

-- ----------------------------------------------------
-- 10. Table ORDONNANCE (Lien Consultation AJOUTÉ)
-- ----------------------------------------------------
CREATE TABLE ordonnance (
                            id_ord BIGINT PRIMARY KEY AUTO_INCREMENT,
                            id_entite BIGINT,
                            id_consultation BIGINT NOT NULL, -- LIEN VERS CONSULTATION AJOUTÉ
                            date DATE,
                            FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                            FOREIGN KEY (id_consultation) REFERENCES consultation(id_consultation)
);

-- ----------------------------------------------------
-- 11. Table CERTIFICAT (Lien Consultation AJOUTÉ)
-- ----------------------------------------------------
CREATE TABLE certificat (
                            id_certif BIGINT PRIMARY KEY AUTO_INCREMENT,
                            id_entite BIGINT,
                            id_consultation BIGINT NOT NULL, -- LIEN VERS CONSULTATION AJOUTÉ
                            date_debut DATE,
                            date_fin DATE,
                            duree INT,
                            note_medecin VARCHAR(255),
                            FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                            FOREIGN KEY (id_consultation) REFERENCES consultation(id_consultation)
);

-- ----------------------------------------------------
-- 12. Table PRESCRIPTION (Doit être liée à Ordonnance ou Consultation)
-- ----------------------------------------------------
CREATE TABLE prescription (
                              id_pr BIGINT PRIMARY KEY AUTO_INCREMENT,
                              id_entite BIGINT,
                              id_ordonnance BIGINT NOT NULL, -- AJOUT DU LIEN VERS ORDONNANCE
                              quantite INT,
                              categorie VARCHAR(100),
                              frequence VARCHAR(100),
                              duree_en_jours INT,
                              FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                              FOREIGN KEY (id_ordonnance) REFERENCES ordonnance(id_ord)
);

-- ----------------------------------------------------
-- 13. Autres tables (Médicament, Acte, Antécédents, Intervention)
-- ----------------------------------------------------

CREATE TABLE acte (
                      id_acte BIGINT PRIMARY KEY AUTO_INCREMENT,
                      id_entite BIGINT,
                      libelle VARCHAR(150),
                      categorie VARCHAR(100),
                      prix_de_base DOUBLE,
                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE medicament (
                            id_mct BIGINT PRIMARY KEY AUTO_INCREMENT,
                            id_entite BIGINT,
                            nom VARCHAR(150),
                            laboratoire VARCHAR(150),
                            type VARCHAR(100),
                            forme VARCHAR(100),
                            remboursable BOOLEAN,
                            prix_unitaire DOUBLE,
                            description TEXT,
                            FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE intervention_medecin (
                                      id_im BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      id_entite BIGINT,
                                      id_consultation BIGINT NOT NULL, -- LIEN VERS CONSULTATION AJOUTÉ
                                      id_acte BIGINT NOT NULL, -- LIEN VERS ACTE AJOUTÉ
                                      prix_de_patient DOUBLE,
                                      num_dent INT,
                                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                                      FOREIGN KEY (id_consultation) REFERENCES consultation(id_consultation),
                                      FOREIGN KEY (id_acte) REFERENCES acte(id_acte)
);

CREATE TABLE antecedents (
                             id_antecedent BIGINT PRIMARY KEY AUTO_INCREMENT,
                             id_entite BIGINT,
                             id_patient BIGINT NOT NULL, -- LIEN VERS PATIENT AJOUTÉ
                             nom VARCHAR(150),
                             categorie VARCHAR(100),
                             niveau_de_risque ENUM('FAIBLE','MOYEN','ELEVE','CRITIQUE'),
                             FOREIGN KEY (id_entite) REFERENCES entite(id_entite),
                             FOREIGN KEY (id_patient) REFERENCES patient(id_patient)
);