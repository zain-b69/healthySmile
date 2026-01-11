package ma.dentalTech.service.modules.dashboard_statistiques.impl;

import ma.dentalTech.service.modules.dashboard_statistiques.api.DashboardService;
import ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DashboardServiceImpl implements DashboardService {

    private final StatistiquesService statistiquesService;

    public DashboardServiceImpl(StatistiquesService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    @Override
    public Map<String, Object> chargerDonneesDashboard() {

        Map<String, Object> data = new HashMap<>();

        data.put("nbConsultations",
                statistiquesService.nombreTotalConsultations());

        data.put("consultationsParStatut",
                statistiquesService.nombreConsultationsParStatut());

        data.put("nbInterventions",
                statistiquesService.nombreTotalInterventions());

        data.put("chiffreAffairesTotal",
                statistiquesService.chiffreAffairesTotal());

        data.put("chiffreAffairesMois",
                statistiquesService.chiffreAffairesEntre(
                        LocalDate.now().withDayOfMonth(1),
                        LocalDate.now()
                ));

        return data;
    }
}
