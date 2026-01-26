-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: healthysmile-v3
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acte`
--

DROP TABLE IF EXISTS `acte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acte` (
  `id_acte` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `libelle` varchar(150) DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `prix_de_base` double DEFAULT NULL,
  PRIMARY KEY (`id_acte`),
  KEY `id_entite` (`id_entite`),
  CONSTRAINT `acte_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acte`
--

LOCK TABLES `acte` WRITE;
/*!40000 ALTER TABLE `acte` DISABLE KEYS */;
INSERT INTO `acte` VALUES (1,NULL,'Consultation simple','Consultation',200),(2,NULL,'Radio rétro-alvéolaire','Radiologie',120),(3,NULL,'Traitement carie (composite 1 face)','Soins',350),(5,NULL,'Surfaçage radiculaire (par quadrant)','Parodontologie',700),(6,NULL,'Couronne céramo-métallique','Prothèse fixe',2500),(7,NULL,'Pose implant (hors couronne)','Implantologie',5900),(8,NULL,'Blanchiment (arcade)','Esthétique',1500),(9,NULL,'Consultation ortho','Orthodontie',250),(11,NULL,'Detatrage','Soins',250);
/*!40000 ALTER TABLE `acte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agenda`
--

DROP TABLE IF EXISTS `agenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agenda` (
  `id_agenda` bigint NOT NULL AUTO_INCREMENT,
  `id_medecin` bigint NOT NULL,
  `annee` smallint NOT NULL,
  `mois` tinyint NOT NULL,
  PRIMARY KEY (`id_agenda`),
  UNIQUE KEY `uk_agenda_medecin_mois` (`id_medecin`,`annee`,`mois`),
  CONSTRAINT `fk_agenda_medecin` FOREIGN KEY (`id_medecin`) REFERENCES `medecin` (`id_medecin`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agenda`
--

LOCK TABLES `agenda` WRITE;
/*!40000 ALTER TABLE `agenda` DISABLE KEYS */;
INSERT INTO `agenda` VALUES (1,10,2026,1),(2,10,2026,2),(3,10,2026,4);
/*!40000 ALTER TABLE `agenda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agenda_jours_non_disponibles`
--

DROP TABLE IF EXISTS `agenda_jours_non_disponibles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agenda_jours_non_disponibles` (
  `id_agenda` bigint NOT NULL,
  `date_non_disponible` date NOT NULL,
  PRIMARY KEY (`id_agenda`,`date_non_disponible`),
  CONSTRAINT `agenda_jours_non_disponibles_ibfk_1` FOREIGN KEY (`id_agenda`) REFERENCES `agenda` (`id_agenda`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agenda_jours_non_disponibles`
--

LOCK TABLES `agenda_jours_non_disponibles` WRITE;
/*!40000 ALTER TABLE `agenda_jours_non_disponibles` DISABLE KEYS */;
/*!40000 ALTER TABLE `agenda_jours_non_disponibles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `antecedents`
--

DROP TABLE IF EXISTS `antecedents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `antecedents` (
  `id_antecedent` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `nom` varchar(150) DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `niveau_de_risque` enum('FAIBLE','MOYEN','ELEVE','CRITIQUE') DEFAULT NULL,
  PRIMARY KEY (`id_antecedent`),
  KEY `id_entite` (`id_entite`),
  CONSTRAINT `antecedents_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `antecedents`
--

LOCK TABLES `antecedents` WRITE;
/*!40000 ALTER TABLE `antecedents` DISABLE KEYS */;
INSERT INTO `antecedents` VALUES (1,7,'Diabète','CHRONIQUE','ELEVE'),(3,10,'Allergie anesthésie','ALLERGIE','CRITIQUE'),(10,38,'Allergie pénicilline','ALLERGIE','ELEVE'),(11,41,'Hypertension','MALADIE_CHRONIQUE','MOYEN'),(12,42,'Asthme','MALADIE_CHRONIQUE','MOYEN'),(13,43,'Traitement anticoagulant','TRAITEMENT_EN_COURS','ELEVE'),(14,44,'Traitement antibiotique','TRAITEMENT_EN_COURS','FAIBLE'),(15,45,'Contre-indication aux AINS','CONTRE_INDICATION','MOYEN'),(16,46,'Antécédent chirurgie cardiaque','ANTECEDENT_CHIRURGICAL','ELEVE'),(17,47,'Hépatite','ANTECEDENT_INFECTIEUX','ELEVE'),(18,48,'VIH','ANTECEDENT_INFECTIEUX','ELEVE'),(19,49,'Bruxisme','ANTECEDENT_DENTAIRE','FAIBLE'),(20,50,'Caries fréquentes','ANTECEDENT_DENTAIRE','MOYEN'),(21,51,'Tabagisme','HABITUDE_DE_VIE','MOYEN'),(22,52,'Alcool','HABITUDE_DE_VIE','FAIBLE'),(23,53,'Autre','AUTRE','FAIBLE'),(26,67,'Hepatite C','ANTECEDENT_INFECTIEUX','FAIBLE');
/*!40000 ALTER TABLE `antecedents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cabinet_medicale`
--

DROP TABLE IF EXISTS `cabinet_medicale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cabinet_medicale` (
  `id_cabinet` bigint NOT NULL AUTO_INCREMENT,
  `id_user` bigint NOT NULL,
  `nom` varchar(100) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `cin` varchar(30) DEFAULT NULL,
  `tel1` varchar(30) DEFAULT NULL,
  `tel2` varchar(30) DEFAULT NULL,
  `site_web` varchar(255) DEFAULT NULL,
  `instagram` varchar(255) DEFAULT NULL,
  `facebook` varchar(255) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id_cabinet`),
  KEY `fk_cabinet_user` (`id_user`),
  CONSTRAINT `fk_cabinet_user` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cabinet_medicale`
--

LOCK TABLES `cabinet_medicale` WRITE;
/*!40000 ALTER TABLE `cabinet_medicale` DISABLE KEYS */;
INSERT INTO `cabinet_medicale` VALUES (2,6,'Cabinet HealthySmile','contact@healthysmile.ma',NULL,'123 Avenue Mohammed V, Rabat','AB123456','0612345678',NULL,'https://healthysmile.ma','https://instagram.com/healthysmile','https://facebook.com/healthysmile','Cabinet spécialisé en soins dentaires.');
/*!40000 ALTER TABLE `cabinet_medicale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cabinet_staff`
--

DROP TABLE IF EXISTS `cabinet_staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cabinet_staff` (
  `cabinet_id` bigint NOT NULL,
  `staff_id` bigint NOT NULL,
  PRIMARY KEY (`cabinet_id`,`staff_id`),
  KEY `fk_staff` (`staff_id`),
  CONSTRAINT `fk_cabinet` FOREIGN KEY (`cabinet_id`) REFERENCES `cabinet_medicale` (`id_cabinet`) ON DELETE CASCADE,
  CONSTRAINT `fk_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id_staff`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cabinet_staff`
--

LOCK TABLES `cabinet_staff` WRITE;
/*!40000 ALTER TABLE `cabinet_staff` DISABLE KEYS */;
/*!40000 ALTER TABLE `cabinet_staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `certificat`
--

DROP TABLE IF EXISTS `certificat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificat` (
  `id_certif` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_consultation` bigint NOT NULL,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `duree` int DEFAULT NULL,
  `note_medecin` varchar(255) DEFAULT NULL,
  `id_dm` bigint NOT NULL,
  PRIMARY KEY (`id_certif`),
  KEY `id_entite` (`id_entite`),
  KEY `id_consultation` (`id_consultation`),
  KEY `fk_certificat_dm` (`id_dm`),
  CONSTRAINT `certificat_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `certificat_ibfk_2` FOREIGN KEY (`id_consultation`) REFERENCES `consultation` (`id_consultation`),
  CONSTRAINT `fk_certificat_dm` FOREIGN KEY (`id_dm`) REFERENCES `dossier_medicale` (`id_dm`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificat`
--

LOCK TABLES `certificat` WRITE;
/*!40000 ALTER TABLE `certificat` DISABLE KEYS */;
/*!40000 ALTER TABLE `certificat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `charge`
--

DROP TABLE IF EXISTS `charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `charge` (
  `id_charge` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_cabinet` bigint DEFAULT NULL,
  `titre` varchar(150) NOT NULL,
  `montant` double NOT NULL,
  `description` text,
  `date_charge` date NOT NULL,
  PRIMARY KEY (`id_charge`),
  KEY `id_entite` (`id_entite`),
  KEY `idx_charge_id_cabinet` (`id_cabinet`),
  CONSTRAINT `charge_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `fk_charge_cabinet` FOREIGN KEY (`id_cabinet`) REFERENCES `cabinet_medicale` (`id_cabinet`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charge`
--

LOCK TABLES `charge` WRITE;
/*!40000 ALTER TABLE `charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation`
--

DROP TABLE IF EXISTS `consultation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation` (
  `id_consultation` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_medecin` bigint NOT NULL,
  `date` date NOT NULL,
  `statut` enum('PLANIFIEE','EN_COURS','TERMINEE','ANNULEE') DEFAULT 'PLANIFIEE',
  `observation_medecin` varchar(255) DEFAULT NULL,
  `id_dm` bigint NOT NULL,
  `id_rdv` bigint DEFAULT NULL,
  PRIMARY KEY (`id_consultation`),
  UNIQUE KEY `uk_consultation_rdv` (`id_rdv`),
  KEY `id_entite` (`id_entite`),
  KEY `id_medecin` (`id_medecin`),
  KEY `fk_consultation_dm` (`id_dm`),
  CONSTRAINT `consultation_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `consultation_ibfk_3` FOREIGN KEY (`id_medecin`) REFERENCES `utilisateur` (`id_user`),
  CONSTRAINT `fk_consultation_dm` FOREIGN KEY (`id_dm`) REFERENCES `dossier_medicale` (`id_dm`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_consultation_rdv` FOREIGN KEY (`id_rdv`) REFERENCES `rdv` (`id_rdv`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation`
--

LOCK TABLES `consultation` WRITE;
/*!40000 ALTER TABLE `consultation` DISABLE KEYS */;
INSERT INTO `consultation` VALUES (3,NULL,6,'2026-01-14','PLANIFIEE',NULL,4,3),(4,NULL,6,'2026-01-14','PLANIFIEE',NULL,5,4),(5,NULL,6,'2026-01-14','PLANIFIEE',NULL,7,6),(6,NULL,6,'2026-01-14','PLANIFIEE',NULL,4,7),(8,NULL,6,'2026-01-15','PLANIFIEE',NULL,9,9),(9,NULL,6,'2026-01-15','PLANIFIEE',NULL,11,10),(16,NULL,11,'2026-01-17','PLANIFIEE',NULL,7,12);
/*!40000 ALTER TABLE `consultation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creneau`
--

DROP TABLE IF EXISTS `creneau`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creneau` (
  `id_creneau` bigint NOT NULL AUTO_INCREMENT,
  `id_agenda` bigint NOT NULL,
  `date_jour` date NOT NULL,
  `heure_debut` time NOT NULL,
  `heure_fin` time NOT NULL,
  `statut` enum('LIBRE','RESERVE','BLOQUE') NOT NULL DEFAULT 'LIBRE',
  PRIMARY KEY (`id_creneau`),
  UNIQUE KEY `uk_creneau_unique` (`id_agenda`,`date_jour`,`heure_debut`,`heure_fin`),
  CONSTRAINT `fk_creneau_agenda` FOREIGN KEY (`id_agenda`) REFERENCES `agenda` (`id_agenda`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `creneau_chk_1` CHECK ((`heure_fin` > `heure_debut`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creneau`
--

LOCK TABLES `creneau` WRITE;
/*!40000 ALTER TABLE `creneau` DISABLE KEYS */;
/*!40000 ALTER TABLE `creneau` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dossier_medicale`
--

DROP TABLE IF EXISTS `dossier_medicale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dossier_medicale` (
  `id_dm` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_patient` bigint NOT NULL,
  `date_de_creation` date DEFAULT NULL,
  PRIMARY KEY (`id_dm`),
  KEY `id_entite` (`id_entite`),
  KEY `id_patient` (`id_patient`),
  CONSTRAINT `dossier_medicale_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `dossier_medicale_ibfk_2` FOREIGN KEY (`id_patient`) REFERENCES `patient` (`id_patient`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dossier_medicale`
--

LOCK TABLES `dossier_medicale` WRITE;
/*!40000 ALTER TABLE `dossier_medicale` DISABLE KEYS */;
INSERT INTO `dossier_medicale` VALUES (4,61,5,'2025-12-24'),(5,NULL,6,'2026-01-11'),(6,NULL,7,'2026-01-11'),(7,NULL,19,'2026-01-14'),(9,NULL,13,'2026-01-14'),(10,NULL,12,'2026-01-14'),(11,NULL,8,'2026-01-14');
/*!40000 ALTER TABLE `dossier_medicale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entite`
--

DROP TABLE IF EXISTS `entite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entite` (
  `id_entite` bigint NOT NULL AUTO_INCREMENT,
  `date_creation` date DEFAULT NULL,
  `date_derniere_modification` datetime DEFAULT NULL,
  `modifie_par` varchar(100) DEFAULT NULL,
  `cree_par` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entite`
--

LOCK TABLES `entite` WRITE;
/*!40000 ALTER TABLE `entite` DISABLE KEYS */;
INSERT INTO `entite` VALUES (1,'2025-11-09','2025-11-09 18:43:05','admin','admin'),(2,'2025-11-09','2025-11-09 18:43:05','admin','admin'),(3,'2025-11-09','2025-11-09 18:43:05','system','admin'),(5,'2025-12-18','2025-12-18 00:15:29','test','test'),(6,'2025-12-18','2025-12-18 00:23:34','test','test'),(7,'2025-12-18','2025-12-18 00:23:34','test','test'),(8,'2025-12-18','2026-01-14 16:17:50',NULL,'test'),(10,'2025-12-18','2025-12-18 01:32:59','seed','seed'),(12,'2025-12-18','2025-12-18 11:34:59','test','test'),(14,'2025-12-18','2025-12-18 12:49:57','seed','seed'),(15,'2025-12-18','2025-12-18 12:51:23','seed','seed'),(16,'2025-12-18','2025-12-18 12:55:29','seed','seed'),(17,'2025-12-18','2025-12-18 12:55:29','seed','seed'),(18,'2025-12-18','2025-12-18 13:02:00','seed','seed'),(19,'2025-12-18','2025-12-18 13:03:37','seed','seed'),(20,'2025-12-18','2025-12-18 13:03:38','seed','seed'),(21,'2025-12-18','2025-12-18 13:03:59','seed','seed'),(22,'2025-12-18','2025-12-18 12:13:03','seed','seed'),(25,'2025-12-18','2025-12-18 12:24:15','seed','seed'),(26,'2025-12-18','2025-12-18 12:25:02','admin','admin'),(30,'2025-12-18','2025-12-18 14:17:32','seed','seed'),(33,'2025-12-20','2025-12-20 11:42:45','admin','admin'),(36,'2025-12-22','2025-12-22 12:30:53','seed','seed'),(37,'2025-12-22','2025-12-22 12:31:34','seed','seed'),(38,'2025-12-23','2025-12-23 21:40:25',NULL,NULL),(39,'2025-12-23','2025-12-23 21:48:48','system','system'),(40,'2025-12-23','2025-12-23 21:54:40',NULL,NULL),(41,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(42,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(43,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(44,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(45,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(46,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(47,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(48,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(49,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(50,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(51,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(52,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(53,'2025-12-23','2025-12-23 22:03:20',NULL,NULL),(57,'2025-12-24','2025-12-24 14:49:13','system','system'),(58,'2025-12-24','2025-12-24 22:36:34','seed','seed'),(59,'2025-12-24','2025-12-24 22:43:43','seed','seed'),(60,'2025-12-24','2025-12-24 22:45:18','seed','seed'),(61,'2025-12-24','2025-12-24 22:45:18','seed','seed'),(63,'2026-01-11','2026-01-11 13:42:56','seed','seed'),(64,'2026-01-14','2026-01-14 12:57:13',NULL,NULL),(67,'2026-01-15','2026-01-15 10:35:37',NULL,NULL),(68,'2026-01-17','2026-01-17 17:07:27','seed','seed'),(70,'2026-01-17','2026-01-17 21:36:44','seed','seed');
/*!40000 ALTER TABLE `entite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facture`
--

DROP TABLE IF EXISTS `facture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facture` (
  `id_facture` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `totale_facture` double DEFAULT NULL,
  `reste` double DEFAULT NULL,
  `statut` enum('EN_ATTENTE','PAYEE','PARTIELLEMENT_PAYEE','ANNULEE','IMPAYEE') DEFAULT 'EN_ATTENTE',
  `date_facture` datetime DEFAULT NULL,
  `id_consultation` bigint NOT NULL,
  `id_sf` bigint NOT NULL,
  PRIMARY KEY (`id_facture`),
  KEY `id_entite` (`id_entite`),
  KEY `fk_facture_consultation` (`id_consultation`),
  KEY `fk_facture_sf` (`id_sf`),
  CONSTRAINT `facture_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `fk_facture_consultation` FOREIGN KEY (`id_consultation`) REFERENCES `consultation` (`id_consultation`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_facture_sf` FOREIGN KEY (`id_sf`) REFERENCES `situation_financiere` (`id_sf`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facture`
--

LOCK TABLES `facture` WRITE;
/*!40000 ALTER TABLE `facture` DISABLE KEYS */;
INSERT INTO `facture` VALUES (16,NULL,12345,0,'PAYEE','2026-01-17 13:48:21',3,10),(18,NULL,400,300,'ANNULEE','2026-01-17 17:05:42',5,11);
/*!40000 ALTER TABLE `facture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `intervention_medecin`
--

DROP TABLE IF EXISTS `intervention_medecin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `intervention_medecin` (
  `id_im` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_consultation` bigint NOT NULL,
  `id_acte` bigint NOT NULL,
  `prix_de_patient` double DEFAULT NULL,
  `num_dent` int DEFAULT NULL,
  PRIMARY KEY (`id_im`),
  KEY `id_entite` (`id_entite`),
  KEY `id_consultation` (`id_consultation`),
  KEY `id_acte` (`id_acte`),
  CONSTRAINT `intervention_medecin_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `intervention_medecin_ibfk_2` FOREIGN KEY (`id_consultation`) REFERENCES `consultation` (`id_consultation`) ON DELETE CASCADE,
  CONSTRAINT `intervention_medecin_ibfk_3` FOREIGN KEY (`id_acte`) REFERENCES `acte` (`id_acte`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intervention_medecin`
--

LOCK TABLES `intervention_medecin` WRITE;
/*!40000 ALTER TABLE `intervention_medecin` DISABLE KEYS */;
INSERT INTO `intervention_medecin` VALUES (1,NULL,3,1,300,12),(2,NULL,3,3,150,21),(3,NULL,4,1,300,12),(4,NULL,4,3,150,21),(5,NULL,5,1,300,12),(6,NULL,5,3,150,21),(7,NULL,6,1,300,12),(8,NULL,6,3,150,21),(11,NULL,8,1,300,12),(12,NULL,8,3,150,21),(13,NULL,9,1,300,12),(14,NULL,9,3,150,21),(15,NULL,16,1,300,12),(16,NULL,16,3,150,21);
/*!40000 ALTER TABLE `intervention_medecin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medecin`
--

DROP TABLE IF EXISTS `medecin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medecin` (
  `id_medecin` bigint NOT NULL,
  `specialite` varchar(100) NOT NULL,
  PRIMARY KEY (`id_medecin`),
  CONSTRAINT `fk_medecin_staff` FOREIGN KEY (`id_medecin`) REFERENCES `staff` (`id_staff`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medecin`
--

LOCK TABLES `medecin` WRITE;
/*!40000 ALTER TABLE `medecin` DISABLE KEYS */;
INSERT INTO `medecin` VALUES (10,'ORTHODONTIE');
/*!40000 ALTER TABLE `medecin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicament`
--

DROP TABLE IF EXISTS `medicament`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicament` (
  `id_mct` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `nom` varchar(150) DEFAULT NULL,
  `laboratoire` varchar(150) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `forme` varchar(100) DEFAULT NULL,
  `remboursable` tinyint(1) DEFAULT NULL,
  `prix_unitaire` double DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id_mct`),
  KEY `id_entite` (`id_entite`),
  CONSTRAINT `medicament_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicament`
--

LOCK TABLES `medicament` WRITE;
/*!40000 ALTER TABLE `medicament` DISABLE KEYS */;
INSERT INTO `medicament` VALUES (1,NULL,'Doliprane','Sanofi','Antalgique','Comprimé',1,12.5,'Test insert');
/*!40000 ALTER TABLE `medicament` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id_notification` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_utilisateur` bigint NOT NULL,
  `titre` enum('INFO','AVERTISSEMENT','ERREUR','SUCCES','RAPPEL') NOT NULL,
  `message` text,
  `type_notification` enum('RAPPEL_RDV','ALERTE_STOCK','URGENCE','RDV_CONFIRME','RDV_ANNULE','PAIEMENT_DU','MESSAGE_SYSTEME','NOUVEAU_PATIENT','NOUVELLE_CONSULTATION') DEFAULT NULL,
  `priorite` enum('BASSE','NORMALE','HAUTE','URGENTE') DEFAULT 'NORMALE',
  `date_notification` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lu` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_notification`),
  KEY `fk_notification_entite` (`id_entite`),
  KEY `fk_notification_utilisateur` (`id_utilisateur`),
  CONSTRAINT `fk_notification_entite` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `fk_notification_utilisateur` FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateur` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,39,6,'INFO','Bienvenue dans HealthySmile','MESSAGE_SYSTEME','NORMALE','2025-12-23 21:48:49',1),(2,57,6,'INFO','Workflow OK: patient 18 created + antecedents linked','MESSAGE_SYSTEME','NORMALE','2025-12-24 14:49:13',0);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ordonnance`
--

DROP TABLE IF EXISTS `ordonnance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordonnance` (
  `id_ord` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_consultation` bigint NOT NULL,
  `date` date DEFAULT NULL,
  `id_dm` bigint NOT NULL,
  PRIMARY KEY (`id_ord`),
  KEY `id_entite` (`id_entite`),
  KEY `id_consultation` (`id_consultation`),
  KEY `fk_ordonnance_dm` (`id_dm`),
  CONSTRAINT `fk_ordonnance_dm` FOREIGN KEY (`id_dm`) REFERENCES `dossier_medicale` (`id_dm`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ordonnance_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `ordonnance_ibfk_2` FOREIGN KEY (`id_consultation`) REFERENCES `consultation` (`id_consultation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ordonnance`
--

LOCK TABLES `ordonnance` WRITE;
/*!40000 ALTER TABLE `ordonnance` DISABLE KEYS */;
/*!40000 ALTER TABLE `ordonnance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `id_patient` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `nom` varchar(150) NOT NULL,
  `date_de_naissance` date NOT NULL,
  `sexe` enum('MASCULIN','FEMININ') DEFAULT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `telephone` varchar(30) DEFAULT NULL,
  `assurance` enum('CNSS','CIMR','RAMED','AUCUNE') DEFAULT NULL,
  PRIMARY KEY (`id_patient`),
  KEY `id_entite` (`id_entite`),
  CONSTRAINT `patient_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (5,1,'Dupont Jean','1985-04-12','MASCULIN','12 Rue Victor Hugo, Paris','0601020304','CNSS'),(6,2,'Martin Claire','1990-09-25','FEMININ','45 Avenue Lafayette, Lyon','0611223344','CIMR'),(7,1,'Dupont Jean','1985-04-12','MASCULIN','12 Rue Victor Hugo, Paris','0601020304','CNSS'),(8,2,'Martin Claire','1990-09-25','FEMININ','45 Avenue Lafayette, Lyon','0611223344','CIMR'),(11,6,'Test Patient','2000-01-15','MASCULIN','Rabat','0600000000','AUCUNE'),(12,8,'Test Patient','2000-01-15','MASCULIN','Rabat','0600000000','RAMED'),(13,12,'Test Patient','2000-01-15','MASCULIN','Rabat','0600000000','AUCUNE'),(14,26,'Patient Test','2000-01-15','MASCULIN','Rabat','0600000000','AUCUNE'),(15,33,'Patient Test','2000-01-15','MASCULIN','Rabat','0600000000','AUCUNE'),(19,64,'patient','1998-01-16','MASCULIN','inconnue','1234567890','CNSS');
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_antecedent`
--

DROP TABLE IF EXISTS `patient_antecedent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient_antecedent` (
  `id_patient` bigint NOT NULL,
  `id_antecedent` bigint NOT NULL,
  PRIMARY KEY (`id_patient`,`id_antecedent`),
  KEY `id_antecedent` (`id_antecedent`),
  CONSTRAINT `patient_antecedent_ibfk_1` FOREIGN KEY (`id_patient`) REFERENCES `patient` (`id_patient`),
  CONSTRAINT `patient_antecedent_ibfk_2` FOREIGN KEY (`id_antecedent`) REFERENCES `antecedents` (`id_antecedent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_antecedent`
--

LOCK TABLES `patient_antecedent` WRITE;
/*!40000 ALTER TABLE `patient_antecedent` DISABLE KEYS */;
INSERT INTO `patient_antecedent` VALUES (11,1),(19,1),(19,17),(15,22);
/*!40000 ALTER TABLE `patient_antecedent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescription`
--

DROP TABLE IF EXISTS `prescription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription` (
  `id_pr` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_ordonnance` bigint NOT NULL,
  `quantite` int DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `frequence` varchar(100) DEFAULT NULL,
  `duree_en_jours` int DEFAULT NULL,
  PRIMARY KEY (`id_pr`),
  KEY `id_entite` (`id_entite`),
  KEY `id_ordonnance` (`id_ordonnance`),
  CONSTRAINT `prescription_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `prescription_ibfk_2` FOREIGN KEY (`id_ordonnance`) REFERENCES `ordonnance` (`id_ord`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescription`
--

LOCK TABLES `prescription` WRITE;
/*!40000 ALTER TABLE `prescription` DISABLE KEYS */;
/*!40000 ALTER TABLE `prescription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescription_medicament`
--

DROP TABLE IF EXISTS `prescription_medicament`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_medicament` (
  `id_pr` bigint NOT NULL,
  `id_mct` bigint NOT NULL,
  `quantite` int DEFAULT '1',
  PRIMARY KEY (`id_pr`,`id_mct`),
  KEY `fk_pm_medicament` (`id_mct`),
  CONSTRAINT `fk_pm_medicament` FOREIGN KEY (`id_mct`) REFERENCES `medicament` (`id_mct`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_pm_prescription` FOREIGN KEY (`id_pr`) REFERENCES `prescription` (`id_pr`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescription_medicament`
--

LOCK TABLES `prescription_medicament` WRITE;
/*!40000 ALTER TABLE `prescription_medicament` DISABLE KEYS */;
/*!40000 ALTER TABLE `prescription_medicament` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rdv`
--

DROP TABLE IF EXISTS `rdv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rdv` (
  `id_rdv` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_patient` bigint NOT NULL,
  `id_medecin` bigint NOT NULL,
  `date` date NOT NULL,
  `heure` varchar(20) DEFAULT NULL,
  `motif` varchar(255) DEFAULT NULL,
  `statut` enum('PLANIFIE','CONFIRME','EN_ATTENTE','ANNULE','TERMINE','EN_COURS','ABSENT') DEFAULT 'PLANIFIE',
  `note_medecin` varchar(255) DEFAULT NULL,
  `id_creneau` bigint DEFAULT NULL,
  `id_dm` bigint NOT NULL,
  `agenda_mensuel_id` bigint NOT NULL,
  PRIMARY KEY (`id_rdv`),
  UNIQUE KEY `id_creneau` (`id_creneau`),
  KEY `id_entite` (`id_entite`),
  KEY `id_patient` (`id_patient`),
  KEY `id_medecin` (`id_medecin`),
  KEY `fk_rdv_dm` (`id_dm`),
  KEY `fk_rdv_agenda` (`agenda_mensuel_id`),
  CONSTRAINT `fk_rdv_agenda` FOREIGN KEY (`agenda_mensuel_id`) REFERENCES `agenda` (`id_agenda`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rdv_creneau` FOREIGN KEY (`id_creneau`) REFERENCES `creneau` (`id_creneau`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_rdv_dm` FOREIGN KEY (`id_dm`) REFERENCES `dossier_medicale` (`id_dm`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rdv_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `rdv_ibfk_2` FOREIGN KEY (`id_patient`) REFERENCES `patient` (`id_patient`),
  CONSTRAINT `rdv_ibfk_3` FOREIGN KEY (`id_medecin`) REFERENCES `utilisateur` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rdv`
--

LOCK TABLES `rdv` WRITE;
/*!40000 ALTER TABLE `rdv` DISABLE KEYS */;
INSERT INTO `rdv` VALUES (3,NULL,5,10,'2026-01-12','10:00:00','Consultation de control','ANNULE',NULL,NULL,4,1),(4,NULL,6,10,'2026-01-12','09:00:00','Douleur dentaire','CONFIRME',NULL,NULL,5,1),(5,NULL,7,10,'2026-01-11','18:01:00','Carie','EN_ATTENTE',NULL,NULL,6,1),(6,NULL,19,10,'2026-01-15','09:00:00','10:00','EN_ATTENTE',NULL,NULL,7,1),(7,NULL,5,10,'2026-01-15','10:00:00','11:00','EN_ATTENTE',NULL,NULL,4,1),(9,NULL,13,10,'2026-01-15','11:00:00','qwertyuiop','ANNULE',NULL,NULL,9,1),(10,NULL,8,10,'2026-01-15','14:00:00','constui','EN_ATTENTE',NULL,NULL,11,1),(11,NULL,5,10,'2026-01-17','17:00:00','qwerty','EN_ATTENTE',NULL,NULL,4,1),(12,NULL,19,10,'2026-01-18','08:00:00','urgent','ANNULE',NULL,NULL,7,1);
/*!40000 ALTER TABLE `rdv` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revenu`
--

DROP TABLE IF EXISTS `revenu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revenu` (
  `id_revenu` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_cabinet` bigint DEFAULT NULL,
  `titre` varchar(150) NOT NULL,
  `montant` double NOT NULL,
  `date_revenu` date NOT NULL,
  `description` text,
  PRIMARY KEY (`id_revenu`),
  KEY `id_entite` (`id_entite`),
  KEY `idx_revenu_id_cabinet` (`id_cabinet`),
  CONSTRAINT `fk_revenu_cabinet` FOREIGN KEY (`id_cabinet`) REFERENCES `cabinet_medicale` (`id_cabinet`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `revenu_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revenu`
--

LOCK TABLES `revenu` WRITE;
/*!40000 ALTER TABLE `revenu` DISABLE KEYS */;
INSERT INTO `revenu` VALUES (1,NULL,2,'Paiement facture',1200,'2026-01-17','Paiement partiel facture patient');
/*!40000 ALTER TABLE `revenu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id_role` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `libelle` enum('ADMIN','MEDECIN','SECRETAIRE','PATIENT') NOT NULL,
  PRIMARY KEY (`id_role`),
  UNIQUE KEY `libelle` (`libelle`),
  KEY `id_entite` (`id_entite`),
  CONSTRAINT `role_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (5,16,'ADMIN'),(14,36,'MEDECIN'),(15,37,'SECRETAIRE');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `secretaire`
--

DROP TABLE IF EXISTS `secretaire`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `secretaire` (
  `id_staff` bigint NOT NULL,
  `num_cnss` varchar(50) NOT NULL,
  `commission` double DEFAULT '0',
  PRIMARY KEY (`id_staff`),
  CONSTRAINT `fk_secretaire_staff` FOREIGN KEY (`id_staff`) REFERENCES `staff` (`id_staff`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `secretaire`
--

LOCK TABLES `secretaire` WRITE;
/*!40000 ALTER TABLE `secretaire` DISABLE KEYS */;
INSERT INTO `secretaire` VALUES (11,'CNSS-0001',250),(12,'CNSS-0001',250);
/*!40000 ALTER TABLE `secretaire` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `situation_financiere`
--

DROP TABLE IF EXISTS `situation_financiere`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `situation_financiere` (
  `id_sf` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `totale_des_actes` double DEFAULT NULL,
  `totale_paye` double DEFAULT NULL,
  `credit` double DEFAULT NULL,
  `statut` enum('EN_REGLE','EN_CREDIT','EN_PROMO') DEFAULT NULL,
  `id_dm` bigint NOT NULL,
  PRIMARY KEY (`id_sf`),
  KEY `id_entite` (`id_entite`),
  KEY `fk_sf_dm` (`id_dm`),
  CONSTRAINT `fk_sf_dm` FOREIGN KEY (`id_dm`) REFERENCES `dossier_medicale` (`id_dm`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `situation_financiere_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `situation_financiere`
--

LOCK TABLES `situation_financiere` WRITE;
/*!40000 ALTER TABLE `situation_financiere` DISABLE KEYS */;
INSERT INTO `situation_financiere` VALUES (10,NULL,3500,3500,0,'EN_REGLE',4),(11,NULL,1200,1200,0,'EN_REGLE',5),(12,NULL,800,0,0,'EN_CREDIT',7),(14,NULL,1800,1800,0,'EN_PROMO',10),(15,NULL,950,500,0,'EN_PROMO',11),(16,NULL,4200,4200,0,'EN_REGLE',9);
/*!40000 ALTER TABLE `situation_financiere` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `id_staff` bigint NOT NULL,
  `cabinet_id` bigint DEFAULT NULL,
  `date_recrutement` date DEFAULT NULL,
  `salaire` double DEFAULT NULL,
  `prime` double DEFAULT '0',
  `solde_conge` int DEFAULT '0',
  PRIMARY KEY (`id_staff`),
  KEY `fk_staff_cabinet` (`cabinet_id`),
  CONSTRAINT `fk_staff_cabinet` FOREIGN KEY (`cabinet_id`) REFERENCES `cabinet_medicale` (`id_cabinet`),
  CONSTRAINT `fk_staff_utilisateur` FOREIGN KEY (`id_staff`) REFERENCES `utilisateur` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (10,NULL,'2024-09-01',12000,1000,20),(11,NULL,'2025-01-10',6500,500,18),(12,NULL,'2025-01-10',6500,500,18);
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistiques`
--

DROP TABLE IF EXISTS `statistiques`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistiques` (
  `id_statistique` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_cabinet` bigint NOT NULL,
  `periode` varchar(50) NOT NULL,
  `date_debut` date NOT NULL,
  `date_fin` date NOT NULL,
  `nb_patients` int DEFAULT '0',
  `nb_rdv` int DEFAULT '0',
  `nb_consultation` int DEFAULT '0',
  `revenu_total` double DEFAULT '0',
  `taux_annulation` decimal(5,2) DEFAULT '0.00',
  `acte_plus_effectif` varchar(255) DEFAULT NULL,
  `categorie` enum('REVENUS','PATIENTS','CONSULTATIONS','RENDEZ_VOUS','ACTES','CHARGES') DEFAULT 'REVENUS',
  PRIMARY KEY (`id_statistique`),
  UNIQUE KEY `uk_statistique_cabinet_periode` (`id_cabinet`,`periode`,`date_debut`),
  KEY `id_entite` (`id_entite`),
  KEY `fk_statistique_cabinet` (`id_cabinet`),
  CONSTRAINT `fk_statistique_cabinet` FOREIGN KEY (`id_cabinet`) REFERENCES `cabinet_medicale` (`id_cabinet`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_statistique_entite` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistiques`
--

LOCK TABLES `statistiques` WRITE;
/*!40000 ALTER TABLE `statistiques` DISABLE KEYS */;
INSERT INTO `statistiques` VALUES (1,NULL,2,'DU 2026-01-01 AU 2026-01-17','2026-01-01','2026-01-17',45,60,50,12500,5.00,'Détartrage','REVENUS'),(2,NULL,2,'DU 2026-03-01 AU 2026-03-31','2026-03-01','2026-03-31',120,0,0,0,0.00,NULL,'PATIENTS'),(3,NULL,2,'DU 2026-04-01 AU 2026-04-30','2026-04-01','2026-04-30',0,0,85,0,2.00,NULL,'CONSULTATIONS'),(4,NULL,2,'DU 2026-05-01 AU 2026-05-31','2026-05-01','2026-05-31',0,140,0,0,8.50,NULL,'RENDEZ_VOUS'),(5,NULL,2,'DU 2026-06-01 AU 2026-06-30','2026-06-01','2026-06-30',0,0,0,0,0.00,'Détartrage','ACTES'),(6,NULL,2,'DU 2026-07-01 AU 2026-07-31','2026-07-01','2026-07-31',0,0,0,0,0.00,NULL,'CHARGES');
/*!40000 ALTER TABLE `statistiques` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateur` (
  `id_user` bigint NOT NULL AUTO_INCREMENT,
  `id_entite` bigint DEFAULT NULL,
  `id_role` bigint NOT NULL,
  `nom` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `cin` varchar(50) DEFAULT NULL,
  `telephone` varchar(30) DEFAULT NULL,
  `sexe` enum('MASCULIN','FEMININ') DEFAULT NULL,
  `login` varchar(100) DEFAULT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `dernier_login` date DEFAULT NULL,
  `date_naissance` date DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `login` (`login`),
  KEY `id_entite` (`id_entite`),
  KEY `id_role` (`id_role`),
  CONSTRAINT `utilisateur_ibfk_1` FOREIGN KEY (`id_entite`) REFERENCES `entite` (`id_entite`),
  CONSTRAINT `utilisateur_ibfk_2` FOREIGN KEY (`id_role`) REFERENCES `role` (`id_role`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (6,22,5,'Admin','admin@test.com','Rabat','BB123456','0600000001','MASCULIN','admin','$2a$12$BMLpvBDm22noOwWIQJCerOAlhhonhZzwG/7Su52ATEX5crKQ1yhEi','2025-12-18','1999-01-01'),(10,63,14,'Dr Medecin Test','med1@test.com','Rabat','MD123456','0611111111','MASCULIN','med1','$2a$10$gA5iQC7ctbVI1rDZaBu8NugCYBqOZRRlzPX70kP/FW6ECEJWjRPG6','2026-01-11','1988-05-12'),(11,68,14,'Secretaire Test','sec1@test.com','Rabat','SC123456','0622222222','MASCULIN','sec1','$2a$10$D6EcCYS0ZqMz76Jrvk6Lv.MbkwoMxSrYvGyZRBVA.8ILGN89qnkLG','2026-01-17','1996-02-20'),(12,70,15,'Secretaire Test','sec12@test.com','Rabat','SC123456','0622222222','FEMININ','sec12','$2a$10$TriMagNDULgHqgMrbOXjo.jQVwvhdu8HB5AUZUNRIpKJwSTWSVQgm','2026-01-17','1996-02-20');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-25 23:39:29
