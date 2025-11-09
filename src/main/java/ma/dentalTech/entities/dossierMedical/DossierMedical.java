package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDate;

public class DossierMedical {
    private Long id;
    private Long idEntite; // AJOUTÉ : Clé pour l'héritage logique
    private Long patientId;
    private LocalDate dateCreation;
    private String observations;

    public DossierMedical() {}

    // Constructeur mis à jour pour inclure idEntite
    public DossierMedical(Long id, Long idEntite, Long patientId, LocalDate dateCreation, String observations) {
        this.id = id;
        this.idEntite = idEntite;
        this.patientId = patientId;
        this.dateCreation = dateCreation;
        this.observations = observations;
    }

    // --- Getters et Setters pour les attributs existants ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    // --- Getters et Setters AJOUTÉS pour idEntite ---

    public Long getIdEntite() { return idEntite; }
    public void setIdEntite(Long idEntite) { this.idEntite = idEntite; }

}