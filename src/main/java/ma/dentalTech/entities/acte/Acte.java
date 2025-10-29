package ma.dentalTech.entities.acte;

public class Acte {
    private Long id;
    private String cat;
    private String libelle;
    private Double prix;

    public Acte() {}
    public Acte(Long id, String cat, String libelle, Double prix) {
        this.id = id;
        this.cat = cat;
        this.libelle = libelle;
        this.prix = prix;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getCat() { return cat; }

        public void setCat(String cat) { this.cat = cat; }

        public String getLibelle() { return libelle; }

        public void setLibelle(String libelle) { this.libelle = libelle; }

        public Double getPrix() { return prix; }

        public void setPrix(Double prix) { this.prix = prix; }
}
