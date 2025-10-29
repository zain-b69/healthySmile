package ma.dentalTech.entities.secretaire;

public class Secretaire {
    private Long id;
    private Long staffId;
    private String competences;
    private Integer ancienneteMois;

    public Secretaire() {}
    public Secretaire(Long id, Long staffId, String competences, Integer ancienneteMois) {
        this.id = id;
        this.staffId = staffId;
        this.competences = competences;
        this.ancienneteMois = ancienneteMois;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getStaffId() { return staffId; }

        public void setStaffId(Long staffId) { this.staffId = staffId; }

        public String getCompetences() { return competences; }

        public void setCompetences(String competences) { this.competences = competences; }

        public Integer getAncienneteMois() { return ancienneteMois; }

        public void setAncienneteMois(Integer ancienneteMois) { this.ancienneteMois = ancienneteMois; }
}
