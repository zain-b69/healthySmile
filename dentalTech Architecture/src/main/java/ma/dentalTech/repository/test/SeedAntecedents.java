package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.util.List;

public class SeedAntecedents {

    public static void main(String[] args) throws Exception {

        AntecedentService svc = ApplicationContext.getBean(AntecedentService.class);

        List<Antecedent> list = List.of(
                a("Allergie pénicilline", CategorieAntecedent.ALLERGIE, NiveauRisque.ELEVE),
                a("Allergie anesthésie", CategorieAntecedent.ALLERGIE, NiveauRisque.ELEVE),

                a("Diabète", CategorieAntecedent.MALADIE_CHRONIQUE, NiveauRisque.ELEVE),
                a("Hypertension", CategorieAntecedent.MALADIE_CHRONIQUE, NiveauRisque.MOYEN),
                a("Asthme", CategorieAntecedent.MALADIE_CHRONIQUE, NiveauRisque.MOYEN),

                a("Traitement anticoagulant", CategorieAntecedent.TRAITEMENT_EN_COURS, NiveauRisque.ELEVE),
                a("Traitement antibiotique", CategorieAntecedent.TRAITEMENT_EN_COURS, NiveauRisque.FAIBLE),

                a("Contre-indication aux AINS", CategorieAntecedent.CONTRE_INDICATION, NiveauRisque.MOYEN),

                a("Antécédent chirurgie cardiaque", CategorieAntecedent.ANTECEDENT_CHIRURGICAL, NiveauRisque.ELEVE),
                a("Hépatite", CategorieAntecedent.ANTECEDENT_INFECTIEUX, NiveauRisque.ELEVE),
                a("VIH", CategorieAntecedent.ANTECEDENT_INFECTIEUX, NiveauRisque.ELEVE),

                a("Bruxisme", CategorieAntecedent.ANTECEDENT_DENTAIRE, NiveauRisque.FAIBLE),
                a("Caries fréquentes", CategorieAntecedent.ANTECEDENT_DENTAIRE, NiveauRisque.MOYEN),

                a("Tabagisme", CategorieAntecedent.HABITUDE_DE_VIE, NiveauRisque.MOYEN),
                a("Alcool", CategorieAntecedent.HABITUDE_DE_VIE, NiveauRisque.FAIBLE),

                a("Autre", CategorieAntecedent.AUTRE, NiveauRisque.FAIBLE)
        );

        int inserted = 0;
        for (Antecedent a : list) {
            if (svc.findByNom(a.getNom()).isPresent()) {
                System.out.println("SKIP (exists): " + a.getNom());
                continue;
            }
            svc.create(a);
            inserted++;
            System.out.println("INSERT: " + a.getNom());
        }

        System.out.println("SeedAntecedents done. inserted=" + inserted);
    }

    private static Antecedent a(String nom, CategorieAntecedent cat, NiveauRisque risque) {
        return Antecedent.builder()
                .nom(nom)
                .categorie(CategorieAntecedent.valueOf(cat.name()))
                .niveauDeRisque(risque)
                .build();
    }
}
