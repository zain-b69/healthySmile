-- ==========================================================
--  MODULE USERS : Utilisateurs / Staff / Admin / Medecin /
--                 Secretaire / Roles / Notifications
--  Schema SQL aligné 100% avec les enums Java fournis
-- ==========================================================


-- ==========================================================
-- Table Utilisateurs
-- Racine de toute la hiérarchie (Staff / Admin / Medecin / Secretaire)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Utilisateurs (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                            nom        VARCHAR(120)  NOT NULL,
                                            email      VARCHAR(150)  NOT NULL,
                                            adresse    VARCHAR(255),
                                            cin        VARCHAR(30),
                                            tel        VARCHAR(30),

                                            sexe ENUM('Homme','Femme') NOT NULL,

                                            login       VARCHAR(80)  NOT NULL,
                                            motDePasse  VARCHAR(255) NOT NULL,

                                            lastLoginDate DATE,
                                            dateNaissance DATE,

    -- Champs communs (BaseEntity)
                                            dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
                                            dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
                                            creePar                  VARCHAR(80),
                                            modifiePar               VARCHAR(80),

                                            UNIQUE KEY uk_user_email (email),
                                            UNIQUE KEY uk_user_login (login),
                                            UNIQUE KEY uk_user_cin   (cin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Staffs (Staff extends Utilisateur)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Staffs (
                                      id BIGINT PRIMARY KEY,

                                      salaire        DECIMAL(12,2),
                                      prime          DECIMAL(12,2),
                                      dateRecrutement DATE,
                                      soldeConge     INT,

                                      CONSTRAINT fk_staff_user
                                          FOREIGN KEY (id) REFERENCES Utilisateurs(id)
                                              ON DELETE CASCADE
                                              ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Admins (Admin extends Staff)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Admins (
                                      id BIGINT PRIMARY KEY,

                                      CONSTRAINT fk_admin_staff
                                          FOREIGN KEY (id) REFERENCES Staffs(id)
                                              ON DELETE CASCADE
                                              ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Medecins (Medecin extends Staff)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Medecins (
                                        id BIGINT PRIMARY KEY,

                                        specialite VARCHAR(150),
                                        agenda_id BIGINT NULL,

                                        CONSTRAINT fk_medecin_staff
                                            FOREIGN KEY (id) REFERENCES Staffs(id)
                                                ON DELETE CASCADE
                                                ON UPDATE CASCADE

    -- FK agenda_id à activer quand ton module Agenda sera créé
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Secretaires (Secretaire extends Staff)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Secretaires (
                                           id BIGINT PRIMARY KEY,

                                           numCNSS VARCHAR(50),
                                           commission DECIMAL(12,2),

                                           CONSTRAINT fk_secretaire_staff
                                               FOREIGN KEY (id) REFERENCES Staffs(id)
                                                   ON DELETE CASCADE
                                                   ON UPDATE CASCADE,

                                           UNIQUE KEY uk_secretaire_numcnss (numCNSS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Roles
-- ENUM : RoleType { ADMIN, MEDECIN, SECRETAIRE }
-- ==========================================================
CREATE TABLE IF NOT EXISTS Roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                     libelle VARCHAR(120) NOT NULL,

                                     type ENUM('ADMIN','MEDECIN','SECRETAIRE') NOT NULL,

    -- Champs BaseEntity
                                     dateCreation             DATE     NOT NULL DEFAULT (CURRENT_DATE),
                                     dateDerniereModification DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     creePar                  VARCHAR(80),
                                     modifiePar               VARCHAR(80),

                                     UNIQUE KEY uk_role_libelle (libelle),
                                     KEY idx_role_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Role_Privileges
-- ==========================================================
CREATE TABLE IF NOT EXISTS Role_Privileges (
                                               role_id BIGINT NOT NULL,
                                               privilege VARCHAR(150) NOT NULL,

                                               PRIMARY KEY (role_id, privilege),

                                               CONSTRAINT fk_privilege_role
                                                   FOREIGN KEY (role_id) REFERENCES Roles(id)
                                                       ON DELETE CASCADE
                                                       ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table User_Roles (association Many-to-Many)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Utilisateur_Roles (
                                                 utilisateur_id BIGINT NOT NULL,
                                                 role_id BIGINT NOT NULL,

                                                 PRIMARY KEY (utilisateur_id, role_id),

                                                 CONSTRAINT fk_user_role_user
                                                     FOREIGN KEY (utilisateur_id) REFERENCES Utilisateurs(id)
                                                         ON DELETE CASCADE
                                                         ON UPDATE CASCADE,

                                                 CONSTRAINT fk_user_role_role
                                                     FOREIGN KEY (role_id) REFERENCES Roles(id)
                                                         ON DELETE CASCADE
                                                         ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- ==========================================================
-- Table Notifications
-- ENUMS EXACTS :
--   TitreNotification: INFO, AVERTISSEMENT, ERREUR, SUCCES, RAPPEL
--   TypeNotification: RAPPEL_RDV, ALERTE_STOCK, URGENCE,
--                     RDV_CONFIRME, RDV_ANNULE, PAIEMENT_DU,
--                     MESSAGE_SYSTEME, NOUVEAU_PATIENT, NOUVELLE_CONSULTATION
--   PrioriteNotification : BASSE, NORMALE, HAUTE, URGENTE
-- ==========================================================
CREATE TABLE IF NOT EXISTS Notifications (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                             titre ENUM('INFO','AVERTISSEMENT','ERREUR','SUCCES','RAPPEL') NOT NULL,

                                             message VARCHAR(500) NOT NULL,

                                             date DATE NOT NULL,
                                             time TIME NOT NULL,

                                             type ENUM(
                                                 'RAPPEL_RDV',
                                                 'ALERTE_STOCK',
                                                 'URGENCE',
                                                 'RDV_CONFIRME',
                                                 'RDV_ANNULE',
                                                 'PAIEMENT_DU',
                                                 'MESSAGE_SYSTEME',
                                                 'NOUVEAU_PATIENT',
                                                 'NOUVELLE_CONSULTATION'
                                                 ) NOT NULL,

                                             priorite ENUM('BASSE','NORMALE','HAUTE','URGENTE') NOT NULL,

                                             lue BOOLEAN NOT NULL DEFAULT FALSE,

                                             utilisateur_id BIGINT NOT NULL,

    -- Champs BaseEntity
                                             dateCreation             DATE     NOT NULL DEFAULT (CURRENT_DATE),
                                             dateDerniereModification DATETIME DEFAULT CURRENT_TIMESTAMP,
                                             creePar                  VARCHAR(80),
                                             modifiePar               VARCHAR(80),

                                             CONSTRAINT fk_notification_user
                                                 FOREIGN KEY (utilisateur_id) REFERENCES Utilisateurs(id)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE,

                                             KEY idx_notif_user_date (utilisateur_id, date, time),
                                             KEY idx_notif_priorite (priorite),
                                             KEY idx_notif_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
