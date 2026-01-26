# 🦷 HealthySmile

---
### Application de Gestion d’un Cabinet Dentaire
---

> Projet de Programmation Avancée JAVA – EMSI Rabat
>
> Réalisé par :
> - **Belkhou Zainab**
> - **El Kadouri Safaa**
> - **El Moqadem Oussama**
> - **El Hanchi El Amrani Ismail**
>
> Encadré par : **Pr. Omar EL MIDAOUI**

---

## 📖 Sommaire
1. [Contexte du projet](#-contexte-du-projet)
2. [Objectifs du projet](#-objectifs-du-projet)
3. [Technologies utilisées](#-technologies-utilisées)
4. [Architecture du projet](#-architecture-du-projet)
5. [Fonctionnalités principales](#-fonctionnalités-principales)
6. [Base de données](#-base-de-données)
7. [Structure du code source](#-structure-du-code-source)
8. [Charte graphique & UI/UX](#-charte-graphique--uiux)
9. [Procédure d’installation et d’exécution](#-procédure-dinstallation-et-dexécution)
10. [Scénario de démonstration](#-scénario-de-démonstration)
11. [Conclusion et perspectives](#-conclusion-et-perspectives)

---

## 🩺 Contexte du projet
La gestion manuelle des cabinets dentaires entraîne souvent des problèmes d’organisation, de planification des rendez-vous et de suivi des dossiers médicaux.  
Dans ce contexte, **HealthySmile** vise à proposer une **application bureau développée en Java Swing** permettant d’assurer une gestion efficace des aspects médicaux, administratifs et financiers d’un cabinet dentaire.

---

## 🎯 Objectifs du projet
- Automatiser la gestion des patients et des dossiers médicaux
- Faciliter la planification des rendez-vous
- Assurer le suivi des consultations, ordonnances et certificats
- Gérer la facturation et les paiements
- Proposer une interface ergonomique et intuitive
- Appliquer les principes de la programmation orientée objet

---

## 🧰 Technologies utilisées
| Catégorie | Technologies |
|---------|-------------|
| Langage | Java |
| Interface graphique | Java Swing |
| Base de données | MySQL |
| Accès aux données | JDBC |
| IDE | IntelliJ IDEA |
| Modélisation | UML (StarUML) |

---

## 🏗️ Architecture du projet
L’application est basée sur une **architecture en couches**, assurant une bonne séparation des responsabilités :

```
DentalTech/
├─ config/          → Configuration et injection de dépendances
├─ entities/        → Entités métiers (Patient, Dossier, RendezVous, etc.)
├─ repository/      → Accès aux données (DAO / JDBC)
├─ service/         → Logique métier (PatientService, FactureService…)
├─ mvc/
│  ├─ controllers/  → Contrôleurs des modules UI
│  ├─ dto/          → Objets de transfert de données (DTO)
│  └─ ui/           → Interface utilisateur (Swing)
│     ├─ common/        → palette : Composants réutilisables
│     ├─ rdv/           → Vues module Rendez-vous
│     ├─ caisse/        → Vues module Caisse
│     ├─ dashboard/     → Vues tableau de bord
│     ├─ .../           → Vues d'autres modules
│     └─ patient/       → Vues module Patient
└─ common/          → Exceptions, utilitaires, validateurs
```

Cette architecture facilite la maintenance, l’évolution et la lisibilité du code.

---

## ⚙️ Fonctionnalités principales

### 👨‍⚕️ Espace Médecin
- Consultation des dossiers médicaux
- Ajout de consultations et traitements
- Gestion des ordonnances et certificats
- Accès aux factures liées aux actes médicaux

### 👩‍💼 Espace Secrétaire
- Gestion des patients
- Planification et gestion des rendez-vous
- Gestion de la caisse et des paiements
- Consultation de l’agenda du médecin

### 🔐 Fonctions transversales
- Authentification et gestion des rôles
- Statistiques (patients, rendez-vous, revenus)
- Recherche et filtrage des données

---

## 🗄️ Base de données
- Base relationnelle **MySQL**
- Tables principales : `patient`, `rendez_vous`, `dossier_medical`, `consultation`, `facture`, `utilisateur`
- Connexion assurée via **JDBC**

**Exemple de configuration (`db.properties`)** :
```properties
db.url=jdbc:mysql://localhost:3306/healthy_smile
db.user=root
db.password=

```

---

## 📁 Structure du code source
Chaque package contient :
- `entities` → POJOs (avec Lombok)
- `repository` → Interfaces DAO + impl JDBC
- `service` → Logique métier
- `mvc` → Présentation (Swing)
- `common` → Outils, exceptions, validateurs
- `config` → Fabriques et contexte applicatif

---

## 🎨 Charte graphique & UI/UX
- **Palette de couleurs :**
    - Bleu foncé (menu latéral et navigation)
    - Bleu vif (boutons d’action)
    - Blanc (fond principal)
    - Gris clair (tableaux, séparations et zones secondaires)
    - Rouge (actions critiques : déconnexion, quitter)

- **Typographie :**
    - Police sans empattement standard (Java Swing – Look & Feel système)
    - Texte clair et lisible, adapté aux tableaux et formulaires

- **Règles UI :**
    - Menu latéral fixe pour une navigation rapide
    - Boutons d’action clairement identifiés
    - Espacement cohérent des champs et composants
    - Tableaux structurés pour une meilleure lisibilité
    - Interface épurée respectant la hiérarchie visuelle

---

## ⚙️ Procédure d’installation et d’exécution

1. **Cloner le projet :**
   ```bash
   git clone https://github.com/zain-b69/healthySmile
   ```

2. **Configurer la base de données :**
   - Importer le fichier `Dump20260125.sql` dans MySQL
   - Modifier `src/main/resources/db.properties`

3. **Compiler et exécuter :**
   ```bash
   mvn clean install
   java -jar target/DentalTech-1.0-SNAPSHOT-shaded.jar
   ```

4. **Connexion par défaut (test) :**
   ```
   Utilisateur : admin
   Mot de passe : admin123
   ```

---

## 🧪 Scénario de démo

1. **Connexion** avec un compte secrétaire  
2. **Ajout d’un nouveau patient**  
3. **Création d’un rendez-vous**  
4. **Saisie d’une consultation + ordonnance**  
5. **Génération d’une facture PDF**  
6. **Affichage du tableau de bord** (statistiques patients / revenus)

---
---

## 👨‍💻 Équipe projet
| Étudiant | Rôle    | Email                   |
|-----------|---------|-------------------------|
| Étudiant 1 | Zainab  | zainabbelkhou@gmail.com |
| Étudiant 2 | Safaa   | ...                     |
| Étudiant 3 | Ismail  | ...                     |
| Étudiant 4 | Oussama | ...                     |

---

© 2025 – **DentalTech | EMSI Rabat**  
Encadré par **Pr. Omar El Midaoui**
