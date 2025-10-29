package ma.dentalTech.entities.situationFinanciere;

import java.time.LocalDate;

public class SituationFinanciere {
    private Long id;
    private Double montantDu;
    private Double montantPaye;
    private LocalDate dateMaj;
    private String etat;

    public SituationFinanciere() {}
    public SituationFinanciere(Long id, Double montantDu, Double montantPaye, LocalDate dateMaj, String etat) {
        this.id = id;
        this.montantDu = montantDu;
        this.montantPaye = montantPaye;
        this.dateMaj = dateMaj;
        this.etat = etat;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Double getMontantDu() { return montantDu; }

        public void setMontantDu(Double montantDu) { this.montantDu = montantDu; }

        public Double getMontantPaye() { return montantPaye; }

        public void setMontantPaye(Double montantPaye) { this.montantPaye = montantPaye; }

        public LocalDate getDateMaj() { return dateMaj; }

        public void setDateMaj(LocalDate dateMaj) { this.dateMaj = dateMaj; }

        public String getEtat() { return etat; }

        public void setEtat(String etat) { this.etat = etat; }
}
