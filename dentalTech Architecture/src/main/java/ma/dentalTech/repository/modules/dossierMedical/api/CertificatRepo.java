package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Certificat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CertificatRepo {

    Certificat save(Certificat certificat);
    Certificat update(Certificat certificat);

    Optional<Certificat> findById(Long idCertif);
    List<Certificat> findAll();
    boolean deleteById(Long idCertif);

    List<Certificat> findByIdConsultation(Long idConsultation);
    List<Certificat> findByIdDm(Long idDm);
    List<Certificat> findByDateDebutBetween(LocalDate start, LocalDate end);
}
