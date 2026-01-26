package ma.dentalTech.service.modules.dossierMedicale.dto;

/**
 * Données minimales pour ajouter une intervention (acte) à une consultation.
 */
public record InterventionInput(
        Long idActe,
        Double prix,
        Integer numDent
) {
    public InterventionInput {
        if (idActe == null) throw new IllegalArgumentException("idActe obligatoire");
        if (prix != null && prix < 0) throw new IllegalArgumentException("prix invalide");
        if (numDent != null && (numDent < 1 || numDent > 48))
            throw new IllegalArgumentException("numDent invalide (1..48)");
    }
}
