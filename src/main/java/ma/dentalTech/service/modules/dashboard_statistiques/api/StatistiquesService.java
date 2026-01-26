package ma.dentalTech.service.modules.dashboard_statistiques.api;

import java.time.LocalDate;
import java.util.Map;

public interface StatistiquesService {

    // --- Consultations ---
    long nombreTotalConsultations();
    Map<String, Long> nombreConsultationsParStatut();

    // --- Financier ---
    double chiffreAffairesTotal();
    double chiffreAffairesEntre(LocalDate debut, LocalDate fin);

    // --- Activité médicale ---
    long nombreTotalInterventions();
}
