package ma.dentalTech.mvc.controllers.modules.dossierMedical.util;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.service.modules.dossierMedicale.dto.InterventionInput;
import ma.dentalTech.service.modules.dossierMedicale.util.ConsultationWorkflowService;

public class ConsultationWorkflowController {

    private final ConsultationWorkflowService workflow;

    public ConsultationWorkflowController(ConsultationWorkflowService workflow) {
        this.workflow = workflow;
    }

    public Facture creerDepuisRdv(Long idRdv,
                                  Long idMedecin,
                                  Long idDm,
                                  Long idSf,
                                  java.util.List<InterventionInput> interventions) {
        return workflow.creerConsultationCompleteDepuisRdv(idRdv, idMedecin, idDm, idSf, interventions);
    }
}
