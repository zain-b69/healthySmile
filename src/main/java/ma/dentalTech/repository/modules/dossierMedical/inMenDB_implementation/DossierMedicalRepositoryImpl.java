package ma.dentalTech.repository.modules.dossierMedical.inMenDB_implementation;

import ma.dentalTech.conf.Db;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {

    // Requête JOIN pour récupérer tous les dossiers (e.) et leurs infos entité (d.)
    private static final String SQL_FIND_BY_PATIENT_ID =
            "SELECT d.*, e.* " +
                    "FROM dossier_medicale d JOIN entite e ON d.id_entite = e.id_entite " +
                    "WHERE d.id_patient = ? " +
                    "ORDER BY d.date_de_creation DESC"; // Tri du plus récent au plus ancien


    @Override
    public List<DossierMedical> findByPatientId(Long idPatient) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DossierMedical> dossiers = new ArrayList<>();

        try {
            connection = Db.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_FIND_BY_PATIENT_ID);
            ps.setLong(1, idPatient); // Filtre par l'ID du patient

            rs = ps.executeQuery();

            while (rs.next()) {
                // Mappage de chaque ligne de résultat vers un objet DossierMedicale
                dossiers.add(mapRowToDossierMedicale(rs));
            }
            return dossiers;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la recherche des dossiers pour le patient ID " + idPatient);
            throw e;
        } finally {
            // Assurez-vous de fermer toutes les ressources
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Méthode utilitaire pour mapper une ligne de résultat SQL à l'objet DossierMedicale.
     */
    private DossierMedical mapRowToDossierMedicale(ResultSet rs) throws SQLException {
        DossierMedical dossier = new DossierMedical();

        // 1. Mappage des attributs ENTITE (e.*)
        dossier.setIdEntite(rs.getLong("id_entite"));
        dossier.setDateCreation(rs.getDate("date_creation").toLocalDate());
        // ... (Autres champs entite)

        // 2. Mappage des attributs DOSSIER MEDICALE (d.*)
        dossier.setId(rs.getLong("id_dm"));
        dossier.setPatientId(rs.getLong("id_patient"));
        dossier.setDateCreation(rs.getDate("date_de_creation").toLocalDate());

        return dossier;
    }

    // ... (Autres méthodes save, findById, etc.)
}