package ma.dentalTech.entities.role;

public class Role {
    private Long id;
    private String libelle;
    private String droits;

    public Role() {}
    public Role(Long id, String libelle, String droits) {
        this.id = id;
        this.libelle = libelle;
        this.droits = droits;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getLibelle() { return libelle; }

        public void setLibelle(String libelle) { this.libelle = libelle; }

        public String getDroits() { return droits; }

        public void setDroits(String droits) { this.droits = droits; }
}
