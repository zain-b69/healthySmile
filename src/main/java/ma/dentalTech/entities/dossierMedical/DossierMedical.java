package ma.dentalTech.entities.dossierMedical;

import java.time.LocalDate;

public class DossierMedical {
    private Long id;
    private Long patientId;
    private LocalDate dateCreation;
    private String observations;

    public DossierMedical() {}
    public DossierMedical(Long id, Long patientId, LocalDate dateCreation, String observations) {
        this.id = id;
        this.patientId = patientId;
        this.dateCreation = dateCreation;
        this.observations = observations;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getPatientId() { return patientId; }

        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public LocalDate getDateCreation() { return dateCreation; }

        public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

        public String getObservations() { return observations; }

        public void setObservations(String observations) { this.observations = observations; }
}
