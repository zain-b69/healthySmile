package ma.dentalTech.service.modules.dossierMedicale.util;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.service.modules.caisse.api.CaisseService;
import ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService;
import ma.dentalTech.service.modules.dossierMedicale.dto.InterventionInput;

import java.time.LocalDate;
import java.util.List;

public class ConsultationWorkflowService {

    private final ConsultationService consultationService;
    private final InterventionMedecinService interventionService;
    private final CaisseService caisseService;

    public ConsultationWorkflowService(
            ConsultationService consultationService,
            InterventionMedecinService interventionService,
            CaisseService caisseService
    ) {
        this.consultationService = consultationService;
        this.interventionService = interventionService;
        this.caisseService = caisseService;
    }

    public Facture creerConsultationCompleteDepuisRdv(
            Long idRdv,
            Long idMedecin,
            Long idDm,
            Long idSituationFinanciere,
            List<InterventionInput> interventions
    ) {
        // 1. Consultation
        Consultation c = consultationService.ouvrirDepuisRdv(
                idRdv, idMedecin, idDm, LocalDate.now()
        );

        // 2. Interventions
        for (InterventionInput i : interventions) {
            interventionService.ajouterIntervention(
                    c.getIdConsultation(),
                    i.idActe(),
                    i.numDent(),
                    i.prix()
            );
        }

        // 3. Facture
        return caisseService.creerFactureDepuisConsultation(
                c.getIdConsultation(),
                idSituationFinanciere
        );
    }
}
