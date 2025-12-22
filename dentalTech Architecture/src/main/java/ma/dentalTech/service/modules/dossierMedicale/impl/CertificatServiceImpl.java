package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.CertificatService;

import java.time.LocalDate;
import java.util.List;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepo certificatRepo;

    public CertificatServiceImpl(CertificatRepo certificatRepo) {
        this.certificatRepo = certificatRepo;
    }

    @Override
    public Certificat create(Certificat c) {
        if (c == null) throw new IllegalArgumentException("Certificat null");
        c.setIdCertif(null);
        return certificatRepo.save(c);
    }

    @Override
    public Certificat update(Certificat c) {
        if (c == null || c.getIdCertif() == null) throw new IllegalArgumentException("idCertif requis");
        getById(c.getIdCertif());
        return certificatRepo.update(c);
    }

    @Override
    public Certificat getById(Long idCertif) {
        return certificatRepo.findById(idCertif)
                .orElseThrow(() -> new RuntimeException("Certificat introuvable: " + idCertif));
    }

    @Override
    public List<Certificat> getAll() {
        return certificatRepo.findAll();
    }

    @Override
    public boolean delete(Long idCertif) {
        return certificatRepo.deleteById(idCertif);
    }

    @Override
    public List<Certificat> getByConsultation(Long idConsultation) {
        return certificatRepo.findByIdConsultation(idConsultation);
    }

    @Override
    public List<Certificat> getByDossierMedical(Long idDm) {
        return certificatRepo.findByIdDm(idDm);
    }

    @Override
    public List<Certificat> getByDateDebutBetween(LocalDate start, LocalDate end) {
        return certificatRepo.findByDateDebutBetween(start, end);
    }
}
