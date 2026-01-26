package ma.dentalTech.service.test;

import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;
import ma.dentalTech.configuration.ApplicationContext;

public class TestReposRunner {

    public static void main(String[] args) {
        OrdonnanceRepo ordonnanceRepo = ApplicationContext.getBean(OrdonnanceRepo.class);
        PrescriptionRepo prescriptionRepo = ApplicationContext.getBean(PrescriptionRepo.class);
        SituationFinanciereRepo sfRepo = ApplicationContext.getBean(SituationFinanciereRepo.class);

        System.out.println("✅ ordonnanceRepo = " + ordonnanceRepo);
        System.out.println("✅ prescriptionRepo = " + prescriptionRepo);
        System.out.println("✅ sfRepo = " + sfRepo);

        System.out.println("Ordonnances count = " + ordonnanceRepo.count());
        System.out.println("Prescriptions count = " + prescriptionRepo.count());
        System.out.println("SituationsFinancieres count = " + sfRepo.count());

        // paging (évite gros affichage)
        System.out.println("Ordonnances page = " + ordonnanceRepo.findPage(5, 0).size());
        System.out.println("Prescriptions page = " + prescriptionRepo.findPage(5, 0).size());
        System.out.println("SF page = " + sfRepo.findPage(5, 0).size());

        System.out.println("✅ Repo tests OK");
    }
}
