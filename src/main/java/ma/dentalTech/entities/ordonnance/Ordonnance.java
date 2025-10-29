package ma.dentalTech.entities.ordonnance;

import java.time.LocalDate;

public class Ordonnance {
    private Long id;
    private Long patientId;
    private LocalDate dateEmission;
    private String remarques;

    public Ordonnance() {}
    public Ordonnance(Long id, Long patientId, LocalDate dateEmission, String remarques) {
        this.id = id;
        this.patientId = patientId;
        this.dateEmission = dateEmission;
        this.remarques = remarques;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getPatientId() { return patientId; }

        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public LocalDate getDateEmission() { return dateEmission; }

        public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }

        public String getRemarques() { return remarques; }

        public void setRemarques(String remarques) { this.remarques = remarques; }
}
