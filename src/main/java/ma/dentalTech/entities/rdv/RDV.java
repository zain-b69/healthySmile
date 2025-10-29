package ma.dentalTech.entities.rdv;

import java.time.LocalDateTime;

public class RDV {
    private Long id;
    private Long patientId;
    private Long dentistId;
    private LocalDateTime dateHeure;
    private String statut;
    private String motif;
    private String remarque;

    public RDV() {}
    public RDV(Long id, Long patientId, Long dentistId, LocalDateTime dateHeure, String statut, String motif, String remarque) {
        this.id = id;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.dateHeure = dateHeure;
        this.statut = statut;
        this.motif = motif;
        this.remarque = remarque;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getPatientId() { return patientId; }

        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public Long getDentistId() { return dentistId; }

        public void setDentistId(Long dentistId) { this.dentistId = dentistId; }

        public LocalDateTime getDateHeure() { return dateHeure; }

        public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

        public String getStatut() { return statut; }

        public void setStatut(String statut) { this.statut = statut; }

        public String getMotif() { return motif; }

        public void setMotif(String motif) { this.motif = motif; }

        public String getRemarque() { return remarque; }

        public void setRemarque(String remarque) { this.remarque = remarque; }
}
