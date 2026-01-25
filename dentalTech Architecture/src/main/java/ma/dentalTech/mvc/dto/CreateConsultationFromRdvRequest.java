package ma.dentalTech.mvc.dto;

public record CreateConsultationFromRdvRequest(
        Long rdvId,
        String observationMedecin,
        java.util.List<InterventionLine> interventions
) {
    public record InterventionLine(Long acteId, Double prixDePatient, Integer numDent) {}
}
