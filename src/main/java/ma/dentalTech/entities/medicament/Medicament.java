package ma.dentalTech.entities.medicament;

public class Medicament {
    private Long id;
    private String nom;
    private String forme;
    private String dosageUnitaire;

    public Medicament() {}
    public Medicament(Long id, String nom, String forme, String dosageUnitaire) {
        this.id = id;
        this.nom = nom;
        this.forme = forme;
        this.dosageUnitaire = dosageUnitaire;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getNom() { return nom; }

        public void setNom(String nom) { this.nom = nom; }

        public String getForme() { return forme; }

        public void setForme(String forme) { this.forme = forme; }

        public String getDosageUnitaire() { return dosageUnitaire; }

        public void setDosageUnitaire(String dosageUnitaire) { this.dosageUnitaire = dosageUnitaire; }
}
