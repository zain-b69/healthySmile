package ma.dentalTech.service.modules.dashboard_statistiques.impl;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.repository.modules.facturation.api.FactureRepo;
import ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StatistiquesServiceImpl implements StatistiquesService {

    private final ConsultationRepo consultationRepo;
    private final InterventionMedecinRepo interventionRepo;
    private final FactureRepo factureRepo;

    public StatistiquesServiceImpl(ConsultationRepo consultationRepo,
                                   InterventionMedecinRepo interventionRepo,
                                   FactureRepo factureRepo) {
        this.consultationRepo = consultationRepo;
        this.interventionRepo = interventionRepo;
        this.factureRepo = factureRepo;
    }

    @Override
    public long nombreTotalConsultations() {
        return consultationRepo.findAll().size();
    }

    @Override
    public Map<String, Long> nombreConsultationsParStatut() {
        Map<String, Long> stats = new HashMap<>();
        for (StatutConsultation statut : StatutConsultation.values()) {
            stats.put(statut.name(), (long) consultationRepo.findByStatut(statut).size());
        }
        return stats;
    }

    @Override
    public double chiffreAffairesTotal() {
        return factureRepo.findAll()
                .stream()
                .mapToDouble(this::safeTotaleFacture)
                .sum();
    }

    // Si ton interface StatistiquesService demande LocalDate:
    @Override
    public double chiffreAffairesEntre(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) throw new IllegalArgumentException("debut/fin requis");
        if (fin.isBefore(debut)) throw new IllegalArgumentException("fin < debut");

        LocalDateTime start = debut.atStartOfDay();
        LocalDateTime end = fin.atTime(23, 59, 59);

        return factureRepo.findByDateFactureBetween(start, end)
                .stream()
                .mapToDouble(this::safeTotaleFacture)
                .sum();
    }

    @Override
    public long nombreTotalInterventions() {
        return interventionRepo.findAll().size();
    }

    // ---- Helpers ----
    private double safeTotaleFacture(Facture f) {
        Double t = f.getTotaleFacture();
        return t == null ? 0.0 : t;
    }
}
