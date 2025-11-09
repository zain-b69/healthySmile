package ma.dentalTech.mvc.dto;

import lombok.*;

/**
 * DTO optimisé pour afficher un résumé du Dossier Médical dans une liste.
 */
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DossierMedicaleDTO {

    private Long idDossier;
    private String statut; // Ex: Actif, Archivé
    private String dateCreation; // Formatée pour l'affichage (Ex: 09/11/2025)
    private String dernierActe;   // Le dernier acte ou consultation enregistré(e)
    private int nombreConsultations;
    private String situationFinanciere; // Ex: Soldé, Crédit de 200 DH

}