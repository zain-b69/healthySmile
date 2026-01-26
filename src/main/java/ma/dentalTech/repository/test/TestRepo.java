package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.entities.users.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TestRepo {

    public static void main(String[] args) throws SQLException {

        // 1) récupérer les repos depuis ApplicationContext
        PatientRepository patientRepo = ApplicationContext.getBean(PatientRepository.class);
        AntecedentRepository antecedentRepo = ApplicationContext.getBean(AntecedentRepository.class);
        UtilisateurRepository utilisateurRepo = ApplicationContext.getBean(UtilisateurRepository.class);

        if (patientRepo == null || antecedentRepo == null || utilisateurRepo == null) {
            throw new IllegalStateException("Beans manquants: vérifie beans.properties (patientRepo/antecedentRepo/utilisateurRepo).");
        }

        // 2) AUTH
        String login = "admin";        // <-- mets un login existant
        String password = "admin123";  // <-- mets le vrai password

        Utilisateur u = utilisateurRepo.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Auth failed: login introuvable: " + login));

        // si ton mot de passe est hashé BCrypt:
        if (u.getMotDePasse() == null || !BCrypt.checkpw(password, u.getMotDePasse())) {
            throw new RuntimeException("Auth failed: mot de passe incorrect");
        }

        System.out.println("✅ Auth OK : " + u.getNom() + " (id=" + u.getIdUser() + ")");

        // 3) créer 1 patient
        Patient p = Patient.builder()
                .nom("Patient Test")
                .dateNaissance(LocalDate.of(2000, 1, 15))
                .sexe(Sexe.MASCULIN)      // ⚠️ doit correspondre à la DB (MASCULIN/FEMININ)
                .adresse("Rabat")
                .telephone("0600000000")
                .assurance(Assurance.AUCUNE) // ⚠️ doit exister dans enum Assurance
                .build();

        p.setCreePar(login);
        p.setModifiePar(login);

        patientRepo.create(p);
        System.out.println("✅ Patient créé : idPatient=" + p.getIdPatient());

        // 4) créer 2 antécédents
        Antecedent a1 = new Antecedent();
        a1.setNom("Diabète");
        a1.setCategorie(CategorieAntecedent.valueOf("CHRONIQUE")); // adapte selon ton type (String/Enum)
        a1.setNiveauDeRisque(NiveauRisque.ELEVE);
        a1.setCreePar(login);
        a1.setModifiePar(login);
        antecedentRepo.create(a1);

        Antecedent a2 = new Antecedent();
        a2.setNom("Allergie anesthésie");
        a2.setCategorie(CategorieAntecedent.valueOf("ALLERGIE"));
        a2.setNiveauDeRisque(NiveauRisque.CRITIQUE);
        a2.setCreePar(login);
        a2.setModifiePar(login);
        antecedentRepo.create(a2);

        System.out.println("✅ Antecedents créés : a1=" + a1.getIdAntecedent() + ", a2=" + a2.getIdAntecedent());

        // 5) lier patient <-> antecedents
        patientRepo.addAntecedentToPatient(p.getIdPatient(), a1.getIdAntecedent());
        patientRepo.addAntecedentToPatient(p.getIdPatient(), a2.getIdAntecedent());
        System.out.println("✅ Liaisons patient_antecedent ajoutées");

        // 6) vérifier : afficher les antécédents du patient
        List<Antecedent> antecedents = patientRepo.getAntecedentsOfPatient(p.getIdPatient());
        System.out.println("\n=== Antécédents du patient id=" + p.getIdPatient() + " ===");
        antecedents.forEach(System.out::println);
    }
}
