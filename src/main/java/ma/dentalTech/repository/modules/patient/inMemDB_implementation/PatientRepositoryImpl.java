package ma.dentalTech.repository.modules.patient.inMemDB_implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import java.sql.*;
import ma.dentalTech.conf.Db;

public class PatientRepositoryImpl implements PatientRepository {

    private final List<Patient> data = new ArrayList<>();

    public PatientRepositoryImpl() {
        // Données d'exemple : 3 patients d'aujourd'hui, 1 d'hier
        LocalDateTime now = LocalDateTime.now();
        data.add(Patient.builder()
                .id(1L).nom("Amal").prenom("Z.")
                .email("amal@example.com").telephone("0611-111111")
                .dateNaissance(LocalDate.of(1995, 5, 12))
                .dateCreation(now.minusMinutes(5))
                .sexe(Sexe.Femme).assurance(Assurance.CNSS)
                .build());

        data.add(Patient.builder()
                .id(2L).nom("Hassan").prenom("B.")
                .email("hassan@example.com").telephone("0622-222222")
                .dateNaissance(LocalDate.of(1989, 9, 23))
                .dateCreation(now.minusHours(1))
                .sexe(Sexe.Homme).assurance(Assurance.CNOPS)
                .build());

        data.add(Patient.builder()
                .id(3L).nom("Nour").prenom("C.")
                .email("nour@example.com").telephone("0633-333333")
                .dateNaissance(LocalDate.of(2000, 2, 2))
                .dateCreation(now.minusMinutes(30))
                .sexe(Sexe.Femme).assurance(Assurance.Autre)
                .build());

        data.add(Patient.builder()
                .id(4L).nom("Youssef").prenom("D.")
                .email("youssef@example.com").telephone("0644-444444")
                .dateNaissance(LocalDate.of(1992, 11, 1))
                .dateCreation(now.minusDays(1)) // hier → ne doit pas s'afficher
                .sexe(Sexe.Homme).assurance(Assurance.Aucune)
                .build());

        // Tri stable par id pour cohérence (findAll renverra trié par date desc via service)
        data.sort(Comparator.comparing(Patient::getId));
    }

    @Override
    public List<Patient> findAll() { return List.copyOf(data); }

    @Override
    public Patient findById(Long id) {
        return data.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void create(Patient patient) { data.add(patient); }

    @Override
    public void update(Patient patient) {
        deleteById(patient.getId());
        data.add(patient);
    }

    @Override
    public void delete(Patient patient) { data.removeIf(p -> p.getId().equals(patient.getId())); }

    @Override
    public void deleteById(Long id) { data.removeIf(p -> p.getId().equals(id)); }

    // Assurez-vous d'avoir les imports de ma.dentalTech.config.Db et java.sql.*

    @Override
    public Patient save(Patient patient) throws SQLException {
        Connection connection = null;
        PreparedStatement psEntite = null;
        PreparedStatement psPatient = null;

        // Requêtes SQL
        final String SQL_INSERT_ENTITE =
                "INSERT INTO entite (date_creation, date_derniere_modification) VALUES (?, ?)";
        // NOTE: 'cree_par' est omis ici si vous n'avez pas ajouté setCreePar

        final String SQL_INSERT_PATIENT =
                "INSERT INTO patient (id_entite, nom, date_de_naissance, sexe, adresse, telephone, assurance) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            // 1. Démarrer la transaction
            connection = Db.getInstance().getConnection();
            connection.setAutoCommit(false);

            // ---------------------------------------------------------------------------------
            // CRITIQUE 1 : Insertion dans ENTITE et récupération de l'ID
            // ---------------------------------------------------------------------------------
            psEntite = connection.prepareStatement(SQL_INSERT_ENTITE, Statement.RETURN_GENERATED_KEYS);

            // Paramètres pour ENTITE
            psEntite.setDate(1, Date.valueOf(patient.getDateCreation().toLocalDate()));
            psEntite.setTimestamp(2, Timestamp.valueOf(patient.getDateCreation()));

            psEntite.executeUpdate();

            // RÉCUPÉRATION CRITIQUE de l'ID de l'entité
            try (ResultSet rs = psEntite.getGeneratedKeys()) {
                if (rs.next()) {
                    Long generatedIdEntite = rs.getLong(1);
                    // Mettre à jour l'objet Java avec l'ID généré
                    patient.setIdEntite(generatedIdEntite);
                } else {
                    throw new SQLException("Échec de la récupération de l'ID Entite. Insertion interrompue.");
                }
            }

            // ---------------------------------------------------------------------------------
            // CRITIQUE 2 : Insertion dans PATIENT en utilisant l'ID récupéré
            // ---------------------------------------------------------------------------------
            if (patient.getIdEntite() == null || patient.getIdEntite() == 0) {
                throw new SQLException("ID Entite est manquant après la première insertion.");
            }

            psPatient = connection.prepareStatement(SQL_INSERT_PATIENT, Statement.RETURN_GENERATED_KEYS);

            // 1er paramètre CRITIQUE : La clé étrangère
            psPatient.setLong(1, patient.getIdEntite());

            // Autres paramètres
            psPatient.setString(2, patient.getNom());
            psPatient.setDate(3, Date.valueOf(patient.getDateDeNaissance()));
            psPatient.setString(4, patient.getSexe().name()); // Assurez-vous que Sexe est un Enum
            psPatient.setString(5, patient.getAdresse());
            psPatient.setString(6, patient.getTelephone());
            psPatient.setString(7, patient.getAssurance().name()); // Assurez-vous que Assurance est un Enum

            psPatient.executeUpdate();

            // Récupérer l'ID spécifique au Patient (id_patient)
            try (ResultSet rs = psPatient.getGeneratedKeys()) {
                if (rs.next()) {
                    patient.setId(rs.getLong(1));
                }
            }

            // 3. Finaliser la transaction
            connection.commit();
            return patient;

        } catch (SQLException e) {
            // En cas d'erreur, annuler toutes les modifications
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException excep) {
                    // Ignore
                }
            }
            throw new SQLException("Erreur de sauvegarde du patient: Violation de clé étrangère possible.", e);

        } finally {
            // Fermer les ressources
            // ...
        }
    }
}
