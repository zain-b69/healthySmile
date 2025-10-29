package ma.dentalTech.entities.charge;

import java.time.LocalDate;

public class Charge {
    private Long id;
    private Long cabinetId;
    private String titre;
    private Double montant;
    private String description;
    private LocalDate date;

    public Charge() {}
    public Charge(Long id, Long cabinetId, String titre, Double montant, String description, LocalDate date) {
        this.id = id;
        this.cabinetId = cabinetId;
        this.titre = titre;
        this.montant = montant;
        this.description = description;
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

        public String getDescription() { return description; }

        public void setDescription(String description) { this.description = description; }

        public LocalDate getDate() { return date; }

        public void setDate(LocalDate date) { this.date = date; }
}
