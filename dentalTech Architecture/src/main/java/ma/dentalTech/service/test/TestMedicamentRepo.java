package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;

public class TestMedicamentRepo {

    public static void main(String[] args) {

        MedicamentRepo repo = ApplicationContext.getBean(MedicamentRepo.class);
        if (repo == null) throw new IllegalStateException("medicamentRepo introuvable");

        Medicament m = new Medicament();
        m.setNom("Doliprane");
        m.setLaboratoire("Sanofi");
        m.setType("Antalgique");
        m.setForme("Comprimé");
        m.setRemboursable(true);
        m.setPrixUnitaire(12.5);
        m.setDescription("Test insert");

        // SAVE
        Medicament saved = repo.save(m);
        System.out.println("✅ Insert OK id=" + saved.getIdMct());

        // FIND
        var found = repo.findById(saved.getIdMct());
        System.out.println("✅ FindById: " + found.orElse(null));
    }
}
