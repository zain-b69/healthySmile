-- Schéma MySQL pour DentalSoft (version française, avec héritage logique)

CREATE TABLE entite (
                        id_entite BIGINT PRIMARY KEY AUTO_INCREMENT,
                        date_creation DATE,
                        date_derniere_modification DATETIME,
                        modifie_par VARCHAR(100),
                        cree_par VARCHAR(100)
);

CREATE TABLE patient (
                         id_patient BIGINT PRIMARY KEY AUTO_INCREMENT,
                         id_entite BIGINT,
                         nom VARCHAR(150),
                         date_de_naissance DATE,
                         sexe ENUM('MASCULIN','FEMININ'),
                         adresse VARCHAR(255),
                         telephone VARCHAR(30),
                         assurance ENUM('CNSS','CIMR','RAMED','AUCUNE'),
                         FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE situation_financiere (
                                      id_sf BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      id_entite BIGINT,
                                      totale_des_actes DOUBLE,
                                      totale_paye DOUBLE,
                                      credit DOUBLE,
                                      statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                                      en_promo ENUM('OUI','NON'),
                                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE dossier_medicale (
                                  id_dm BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  id_entite BIGINT,
                                  date_de_creation DATE,
                                  FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE facture (
                         id_facture BIGINT PRIMARY KEY AUTO_INCREMENT,
                         id_entite BIGINT,
                         totale_facture DOUBLE,
                         reste DOUBLE,
                         statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                         date_facture DATETIME,
                         FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE consultation (
                              id_consultation BIGINT PRIMARY KEY AUTO_INCREMENT,
                              id_entite BIGINT,
                              date DATE,
                              statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                              observation_medecin VARCHAR(255),
                              FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE ordonnance (
                            id_ord BIGINT PRIMARY KEY AUTO_INCREMENT,
                            id_entite BIGINT,
                            date DATE,
                            FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE certificat (
                            id_certif BIGINT PRIMARY KEY AUTO_INCREMENT,
                            id_entite BIGINT,
                            date_debut DATE,
                            date_fin DATE,
                            duree INT,
                            note_medecin VARCHAR(255),
                            FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE prescription (
                              id_pr BIGINT PRIMARY KEY AUTO_INCREMENT,
                              id_entite BIGINT,
                              quantite INT,
                              categorie VARCHAR(100),
                              frequence VARCHAR(100),
                              duree_en_jours INT,
                              FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

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

CREATE TABLE utilisateur (
                             id_user BIGINT PRIMARY KEY AUTO_INCREMENT,
                             id_entite BIGINT,
                             nom VARCHAR(150),
                             email VARCHAR(150),
                             adresse VARCHAR(255),
                             cin VARCHAR(50),
                             telephone VARCHAR(30),
                             sexe ENUM('MASCULIN','FEMININ'),
                             login VARCHAR(100),
                             mot_de_passe VARCHAR(255),
                             dernier_login DATE,
                             date_naissance DATE,
                             FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE role (
                      id_role BIGINT PRIMARY KEY AUTO_INCREMENT,
                      id_entite BIGINT,
                      libelle ENUM('ADMIN','MEDECIN','SECRETAIRE','PATIENT'),
                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE intervention_medecin (
                                      id_im BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      id_entite BIGINT,
                                      prix_de_patient DOUBLE,
                                      num_dent INT,
                                      FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE rdv (
                     id_rdv BIGINT PRIMARY KEY AUTO_INCREMENT,
                     id_entite BIGINT,
                     date DATE,
                     heure VARCHAR(20),
                     motif VARCHAR(255),
                     statut ENUM('EN_ATTENTE','EN_COURS','TERMINE','ANNULE'),
                     note_medecin VARCHAR(255),
                     FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);

CREATE TABLE antecedents (
                             id_antecedent BIGINT PRIMARY KEY AUTO_INCREMENT,
                             id_entite BIGINT,
                             nom VARCHAR(150),
                             categorie VARCHAR(100),
                             niveau_de_risque ENUM('FAIBLE','MOYEN','ELEVE','CRITIQUE'),
                             FOREIGN KEY (id_entite) REFERENCES entite(id_entite)
);
