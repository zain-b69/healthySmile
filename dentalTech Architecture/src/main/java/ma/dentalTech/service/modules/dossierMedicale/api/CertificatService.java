package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Certificat;

import java.time.LocalDate;
import java.util.List;

public interface CertificatService {
    Certificat create(Certificat certificat);
    Certificat update(Certificat certificat);
    Certificat getById(Long idCertif);
    List<Certificat> getAll();
    boolean delete(Long idCertif);

    List<Certificat> getByConsultation(Long idConsultation);
    List<Certificat> getByDossierMedical(Long idDm);
    List<Certificat> getByDateDebutBetween(LocalDate start, LocalDate end);
}
