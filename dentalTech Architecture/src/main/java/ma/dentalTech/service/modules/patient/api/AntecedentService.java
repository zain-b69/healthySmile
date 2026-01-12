package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AntecedentService {

    // --- CRUD (utile mais à utiliser surtout pour seed / admin) ---
    List<Antecedent> findAll();
    Antecedent findById(Long id);
    void create(Antecedent a) throws SQLException;
    void update(Antecedent a);
    void deleteById(Long id);

    // --- Recherche / filtres ---
    Optional<Antecedent> findByNom(String nom);
    List<Antecedent> findByCategorie(CategorieAntecedent categorie);
    List<Antecedent> findByNiveauRisque(NiveauRisque niveau);
    List<Antecedent> findPage(int limit, int offset);
    boolean existsById(Long id);
    long count();

    // --- Navigation inverse ---
    List<Patient> getPatientsHavingAntecedent(Long antecedentId);

    // --- Seed idempotent (1 seule fois) ---
    void seedIfMissing(List<Antecedent> antecedents) throws SQLException;

    // Variante pratique : retourne celui en DB
    Antecedent createIfNotExists(Antecedent a) throws SQLException;
}
