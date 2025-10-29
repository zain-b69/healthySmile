package ma.dentalTech.entities.prescription;

public class Prescription {
    private Long id;
    private Long ordonnanceId;
    private Long medicamentId;
    private String dose;
    private String frequence;
    private String duree;
    private String remarques;

    public Prescription() {}
    public Prescription(Long id, Long ordonnanceId, Long medicamentId, String dose, String frequence, String duree, String remarques) {
        this.id = id;
        this.ordonnanceId = ordonnanceId;
        this.medicamentId = medicamentId;
        this.dose = dose;
        this.frequence = frequence;
        this.duree = duree;
        this.remarques = remarques;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getOrdonnanceId() { return ordonnanceId; }

        public void setOrdonnanceId(Long ordonnanceId) { this.ordonnanceId = ordonnanceId; }

        public Long getMedicamentId() { return medicamentId; }

        public void setMedicamentId(Long medicamentId) { this.medicamentId = medicamentId; }

        public String getDose() { return dose; }

        public void setDose(String dose) { this.dose = dose; }

        public String getFrequence() { return frequence; }

        public void setFrequence(String frequence) { this.frequence = frequence; }

        public String getDuree() { return duree; }

        public void setDuree(String duree) { this.duree = duree; }

        public String getRemarques() { return remarques; }

        public void setRemarques(String remarques) { this.remarques = remarques; }
}
