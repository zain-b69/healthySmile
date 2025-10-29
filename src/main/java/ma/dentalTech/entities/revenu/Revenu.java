package ma.dentalTech.entities.revenu;

import java.time.LocalDate;

public class Revenu {
    private Long id;
    private Long cabinetId;
    private String titre;
    private Double montant;
    private LocalDate date;

    public Revenu() {}
    public Revenu(Long id, Long cabinetId, String titre, Double montant, LocalDate date) {
        this.id = id;
        this.cabinetId = cabinetId;
        this.titre = titre;
        this.montant = montant;
        this.date = date;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getCabinetId() { return cabinetId; }

        public void setCabinetId(Long cabinetId) { this.cabinetId = cabinetId; }

        public String getTitre() { return titre; }

        public void setTitre(String titre) { this.titre = titre; }

        public Double getMontant() { return montant; }

        public void setMontant(Double montant) { this.montant = montant; }

        public LocalDate getDate() { return date; }

        public void setDate(LocalDate date) { this.date = date; }
}
