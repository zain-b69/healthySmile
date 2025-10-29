package ma.dentalTech.entities.consultation;

import java.time.LocalDateTime;

public class Consultation {
    private Long id;
    private Long patientId;
    private Long dentistId;
    private LocalDateTime dateHeure;
    private String diagnostic;
    private String observation;

    public Consultation() {}
    public Consultation(Long id, Long patientId, Long dentistId, LocalDateTime dateHeure, String diagnostic, String observation) {
        this.id = id;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.dateHeure = dateHeure;
        this.diagnostic = diagnostic;
        this.observation = observation;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getPatientId() { return patientId; }

        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public Long getDentistId() { return dentistId; }

        public void setDentistId(Long dentistId) { this.dentistId = dentistId; }

        public LocalDateTime getDateHeure() { return dateHeure; }

        public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

        public String getDiagnostic() { return diagnostic; }

        public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

        public String getObservation() { return observation; }

        public void setObservation(String observation) { this.observation = observation; }
}
