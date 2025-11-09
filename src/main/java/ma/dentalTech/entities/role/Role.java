package ma.dentalTech.entities.role;

import ma.dentalTech.entities.enums.RoleLibelle;

public class Role {
    private Long id;
    private RoleLibelle libelle;
    private String droits;

    public Role() {}
    public Role(Long id, RoleLibelle libelle, String droits) {
        this.id = id;
        this.libelle = libelle;
        this.droits = droits;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public RoleLibelle getLibelle() { return libelle; }

        public void setLibelle(RoleLibelle libelle) { this.libelle = libelle; }

        public String getDroits() { return droits; }

        public void setDroits(String droits) { this.droits = droits; }
}
