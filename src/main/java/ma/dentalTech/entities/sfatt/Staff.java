package ma.dentalTech.entities.sfatt;

import java.time.LocalDate;

public class Staff {
    private Long id;
    private Long cabinetId;
    private String role;
    private LocalDate dateEmbauche;
    private Double salaire;
    private Integer cumulHeuresMois;

    public Staff() {}
    public Staff(Long id, Long cabinetId, String role, LocalDate dateEmbauche, Double salaire, Integer cumulHeuresMois) {
        this.id = id;
        this.cabinetId = cabinetId;
        this.role = role;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.cumulHeuresMois = cumulHeuresMois;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getCabinetId() { return cabinetId; }

        public void setCabinetId(Long cabinetId) { this.cabinetId = cabinetId; }

        public String getRole() { return role; }

        public void setRole(String role) { this.role = role; }

        public LocalDate getDateEmbauche() { return dateEmbauche; }

        public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }

        public Double getSalaire() { return salaire; }

        public void setSalaire(Double salaire) { this.salaire = salaire; }

        public Integer getCumulHeuresMois() { return cumulHeuresMois; }

        public void setCumulHeuresMois(Integer cumulHeuresMois) { this.cumulHeuresMois = cumulHeuresMois; }
}
