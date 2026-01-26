package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Certificat;

import java.time.LocalDate;
import java.util.List;

public interface CertificatService {

    // ---- Métier : génération / validation ----
    Certificat genererCertificat(Long idDm,
                                 Long idConsultation,
                                 LocalDate dateDebut,
                                 LocalDate dateFin,
                                 String noteMedecin);

    Certificat mettreAJourPeriode(Long idCertif, LocalDate dateDebut, LocalDate dateFin);

    Certificat ajouterNoteMedecin(Long idCertif, String note);

    // ---- Métier : consultation / historique ----
    List<Certificat> historiqueDossier(Long idDm);

    List<Certificat> certificatsDeConsultation(Long idConsultation);

    List<Certificat> certificatsDansPeriode(LocalDate start, LocalDate end);

    // ---- Métier : rendu texte ----
    String genererTexteCertificat(Certificat c);

    // ---- Utilitaires ----
    Certificat getById(Long idCertif);

    boolean deleteById(Long idCertif);
}
