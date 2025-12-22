package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService;

import java.time.LocalDate;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepo consultationRepo;

    public ConsultationServiceImpl(ConsultationRepo consultationRepo) {
        this.consultationRepo = consultationRepo;
    }

    @Override
    public Consultation create(Consultation c) {
        if (c == null) throw new IllegalArgumentException("Consultation null");
        c.setIdConsultation(null);
        return consultationRepo.save(c);
    }

    @Override
    public Consultation update(Consultation c) {
        if (c == null || c.getIdConsultation() == null) throw new IllegalArgumentException("idConsultation requis");
        getById(c.getIdConsultation());
        return consultationRepo.update(c);
    }

    @Override
    public Consultation getById(Long idConsultation) {
        return consultationRepo.findById(idConsultation)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable: " + idConsultation));
    }

    @Override
    public List<Consultation> getAll() {
        return consultationRepo.findAll();
    }

    @Override
    public boolean delete(Long idConsultation) {
        return consultationRepo.deleteById(idConsultation);
    }

    @Override
    public List<Consultation> getByDossierMedical(Long idDm) {
        return consultationRepo.findByIdDm(idDm);
    }

    @Override
    public List<Consultation> getByMedecin(Long idMedecin) {
        return consultationRepo.findByIdMedecin(idMedecin);
    }

    @Override
    public Consultation getByRdv(Long idRdv) {
        return consultationRepo.findByIdRdv(idRdv)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable pour RDV: " + idRdv));
    }

    @Override
    public List<Consultation> getByDateBetween(LocalDate start, LocalDate end) {
        return consultationRepo.findByDateBetween(start, end);
    }

    @Override
    public List<Consultation> getByStatut(StatutConsultation statut) {
        return consultationRepo.findByStatut(statut);
    }
}
