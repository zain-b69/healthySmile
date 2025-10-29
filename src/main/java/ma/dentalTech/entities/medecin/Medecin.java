package ma.dentalTech.entities.medecin;

public class Medecin {
    private Long id;
    private Long staffId;
    private String specialite;
    private String numeroOrdre;

    public Medecin() {}
    public Medecin(Long id, Long staffId, String specialite, String numeroOrdre) {
        this.id = id;
        this.staffId = staffId;
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getStaffId() { return staffId; }

        public void setStaffId(Long staffId) { this.staffId = staffId; }

        public String getSpecialite() { return specialite; }

        public void setSpecialite(String specialite) { this.specialite = specialite; }

        public String getNumeroOrdre() { return numeroOrdre; }

        public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }
}
