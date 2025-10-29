package ma.dentalTech.entities.interventionMedicale;

public class InterventionMedicale {
    private Long id;
    private Long consultationId;
    private Long acteId;
    private Double cout;
    private Integer dureeMinutes;

    public InterventionMedicale() {}
    public InterventionMedicale(Long id, Long consultationId, Long acteId, Double cout, Integer dureeMinutes) {
        this.id = id;
        this.consultationId = consultationId;
        this.acteId = acteId;
        this.cout = cout;
        this.dureeMinutes = dureeMinutes;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getConsultationId() { return consultationId; }

        public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }

        public Long getActeId() { return acteId; }

        public void setActeId(Long acteId) { this.acteId = acteId; }

        public Double getCout() { return cout; }

        public void setCout(Double cout) { this.cout = cout; }

        public Integer getDureeMinutes() { return dureeMinutes; }

        public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
}
