package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository repo;

    public AntecedentServiceImpl(AntecedentRepository repo) {
        this.repo = repo;
    }

    // ---------- CRUD ----------

    @Override
    public List<Antecedent> findAll() {
        return repo.findAll();
    }

    @Override
    public Antecedent findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        return repo.findById(id);
    }

    @Override
    public void create(Antecedent a) throws SQLException {
        validateCreate(a);
        repo.create(a);
    }

    @Override
    public void update(Antecedent a) {
        if (a == null) throw new IllegalArgumentException("antecedent is null");
        if (a.getIdAntecedent() == null) throw new IllegalArgumentException("antecedent.idAntecedent is null");
        if (a.getNom() != null && a.getNom().isBlank()) throw new IllegalArgumentException("antecedent.nom invalide");
        repo.update(a);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        repo.deleteById(id);
    }

    // ---------- Recherche / filtres ----------

    @Override
    public Optional<Antecedent> findByNom(String nom) {
        if (nom == null || nom.isBlank()) return Optional.empty();
        return repo.findByNom(nom.trim());
    }

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        if (categorie == null) throw new IllegalArgumentException("categorie is null");
        return repo.findByCategorie(categorie);
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauRisque niveau) {
        if (niveau == null) throw new IllegalArgumentException("niveau is null");
        return repo.findByNiveauRisque(niveau);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return repo.existsById(id);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public List<Antecedent> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit doit être > 0");
        if (offset < 0) throw new IllegalArgumentException("offset doit être >= 0");
        return repo.findPage(limit, offset);
    }

    // ---------- Navigation inverse ----------

    @Override
    public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
        if (antecedentId == null) throw new IllegalArgumentException("antecedentId is null");
        return repo.getPatientsHavingAntecedent(antecedentId);
    }

    // ---------- Seed (idempotent) ----------

    @Override
    public void seedIfMissing(List<Antecedent> antecedents) throws SQLException {
        if (antecedents == null || antecedents.isEmpty()) return;

        for (Antecedent a : antecedents) {
            createIfNotExists(a);
        }
    }

    @Override
    public Antecedent createIfNotExists(Antecedent a) throws SQLException {
        validateCreate(a);

        String key = a.getNom().trim();

        Optional<Antecedent> existing = repo.findByNom(key);
        if (existing.isPresent()) {
            return existing.get();
        }

        // crée si absent
        repo.create(a);

        // si repo ne remplit pas idAntecedent, on recharge
        return repo.findByNom(key)
                .orElseThrow(() -> new IllegalStateException("ANTEDEDENT_CREATED_BUT_NOT_FOUND"));
    }

    // ---------- validation helpers ----------

    private static void validateCreate(Antecedent a) {
        if (a == null) throw new IllegalArgumentException("antecedent is null");
        if (a.getNom() == null || a.getNom().isBlank()) throw new IllegalArgumentException("antecedent.nom obligatoire");

        // categorie est String dans ton entity -> on exige juste non vide
        if (a.getCategorie() == null) {
            throw new IllegalArgumentException("antecedent.categorie obligatoire");
        }

        if (a.getNiveauDeRisque() == null) {
            throw new IllegalArgumentException("antecedent.niveauDeRisque obligatoire");
        }
    }
}
