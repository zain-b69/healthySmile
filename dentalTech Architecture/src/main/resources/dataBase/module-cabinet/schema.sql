-- Noms de tables : Cabinets, Charges, Revenues, Statistiques, Cabinet_Staff.
-- Table Staffs(id) existe déjà dans le module users (sinon commente les FKs / seeds de Cabinet_Staff).
-- Pour CategorieStatistique, la colonne est en VARCHAR(50) pour ne pas figer les valeurs SQL (on laisse Java faire le enum).

-- ==========================================================
--  Module CabinetMedical / Charges / Revenues / Statistiques
-- ==========================================================

-- Table Cabinets (CabinetMedical)
CREATE TABLE IF NOT EXISTS Cabinets (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nom        VARCHAR(150)  NOT NULL,
                                        email      VARCHAR(150),
                                        logo       VARCHAR(255),
                                        adresse    VARCHAR(255),
                                        cin        VARCHAR(30),
                                        tel1       VARCHAR(30),
                                        tel2       VARCHAR(30),
                                        siteWeb    VARCHAR(255),
                                        instagram  VARCHAR(255),
                                        facebook   VARCHAR(255),
                                        description TEXT,

    -- Champs d'audit BaseEntity
                                        dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
                                        dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
                                        creePar                  VARCHAR(80),
                                        modifiePar               VARCHAR(80),

                                        UNIQUE KEY uk_cabinets_nom   (nom),
                                        UNIQUE KEY uk_cabinets_email (email),
                                        KEY idx_cabinets_nom_adresse (nom, adresse)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- =====================================
-- Table Charges (charges du cabinet)
-- =====================================
CREATE TABLE IF NOT EXISTS Charges (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       titre       VARCHAR(150) NOT NULL,
                                       description TEXT,
                                       montant     DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                       date        DATETIME      NOT NULL,

                                       cabinet_id  BIGINT,

    -- Champs d'audit BaseEntity
                                       dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
                                       dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
                                       creePar                  VARCHAR(80),
                                       modifiePar               VARCHAR(80),

                                       CONSTRAINT fk_charges_cabinet
                                           FOREIGN KEY (cabinet_id) REFERENCES Cabinets(id)
                                               ON DELETE SET NULL
                                               ON UPDATE CASCADE,

                                       KEY idx_charges_date (date),
                                       KEY idx_charges_cabinet_date (cabinet_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- =====================================
-- Table Revenues (revenus du cabinet)
-- =====================================
CREATE TABLE IF NOT EXISTS Revenues (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        titre       VARCHAR(150) NOT NULL,
                                        description TEXT,
                                        montant     DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                        date        DATETIME      NOT NULL,

                                        cabinet_id  BIGINT,

    -- Champs d'audit BaseEntity
                                        dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
                                        dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
                                        creePar                  VARCHAR(80),
                                        modifiePar               VARCHAR(80),

                                        CONSTRAINT fk_revenues_cabinet
                                            FOREIGN KEY (cabinet_id) REFERENCES Cabinets(id)
                                                ON DELETE SET NULL
                                                ON UPDATE CASCADE,

                                        KEY idx_revenues_date (date),
                                        KEY idx_revenues_cabinet_date (cabinet_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ==========================================
-- Table Statistiques (Statistiques du cabinet)
-- ==========================================
CREATE TABLE IF NOT EXISTS Statistiques (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            nom        VARCHAR(150)  NOT NULL,
                                            categorie  VARCHAR(50)   NOT NULL,    -- correspond à CategorieStatistique.name()
                                            chiffre    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                                            dateCalcul DATE          NOT NULL,

                                            cabinet_id BIGINT,

    -- Champs d'audit BaseEntity
                                            dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
                                            dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
                                            creePar                  VARCHAR(80),
                                            modifiePar               VARCHAR(80),

                                            CONSTRAINT fk_statistiques_cabinet
                                                FOREIGN KEY (cabinet_id) REFERENCES Cabinets(id)
                                                    ON DELETE SET NULL
                                                    ON UPDATE CASCADE,

                                            KEY idx_statistiques_date (dateCalcul),
                                            KEY idx_statistiques_cabinet_date (cabinet_id, dateCalcul),
                                            KEY idx_statistiques_categorie (categorie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ===========================================================
-- Table d'association Cabinet_Staff (Many-to-Many)
-- ===========================================================
-- Hypothèse : il existe une table Staffs(id BIGINT PRIMARY KEY)
-- dans le module USERS.
CREATE TABLE IF NOT EXISTS Cabinet_Staff (
                                             cabinet_id BIGINT NOT NULL,
                                             staff_id   BIGINT NOT NULL,

                                             PRIMARY KEY (cabinet_id, staff_id),

                                             CONSTRAINT fk_cabinetstaff_cabinet
                                                 FOREIGN KEY (cabinet_id) REFERENCES Cabinets(id)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE,

                                             CONSTRAINT fk_cabinetstaff_staff
                                                 FOREIGN KEY (staff_id)   REFERENCES Staffs(id)
                                                     ON DELETE CASCADE
                                                     ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
