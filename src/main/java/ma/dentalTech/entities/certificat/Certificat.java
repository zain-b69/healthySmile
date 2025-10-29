package ma.dentalTech.entities.certificat;

import java.time.LocalDate;

public class Certificat {
    private Long id;
    private Long patientId;
    private String typeCertificat;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Certificat() {}
    public Certificat(Long id, Long patientId, String typeCertificat, String description, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.patientId = patientId;
        this.typeCertificat = typeCertificat;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getPatientId() { return patientId; }

        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public String getTypeCertificat() { return typeCertificat; }

        public void setTypeCertificat(String typeCertificat) { this.typeCertificat = typeCertificat; }

        public String getDescription() { return description; }

        public void setDescription(String description) { this.description = description; }

        public LocalDate getDateDebut() { return dateDebut; }

        public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

        public LocalDate getDateFin() { return dateFin; }

        public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
