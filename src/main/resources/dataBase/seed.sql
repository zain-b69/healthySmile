-- Jeu de données de Test
INSERT INTO role(libelle) VALUES ('ADMIN'), ('MEDECIN'), ('SECRETAIRE')
ON DUPLICATE KEY UPDATE libelle=VALUES(libelle);
-- etc
