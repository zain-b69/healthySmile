package ma.dentalTech.entities.facture;

import java.time.LocalDate;

public class Facture {
    private Long id;
    private Long patientId;
    private LocalDate dateFacture;
    private Double montantTTC;
    private Double remise;
    private String etatPaiement;

    public Facture() {}
    public Facture(Long id, Long patientId, LocalDate dateFacture, Double montantTTC, Double remise, String etatPaiement) {
        this.id = id;
        this.patientId = patientId;
        this.dateFacture = dateFacture;
        this.montantTTC = montantTTC;
        this.remise = remise;
        this.etatPaiement = etatPaiement;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public Long getPatientId() { return patientId; }

        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public LocalDate getDateFacture() { return dateFacture; }

        public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }

        public Double getMontantTTC() { return montantTTC; }

        public void setMontantTTC(Double montantTTC) { this.montantTTC = montantTTC; }

        public Double getRemise() { return remise; }

        public void setRemise(Double remise) { this.remise = remise; }

        public String getEtatPaiement() { return etatPaiement; }

        public void setEtatPaiement(String etatPaiement) { this.etatPaiement = etatPaiement; }
}
