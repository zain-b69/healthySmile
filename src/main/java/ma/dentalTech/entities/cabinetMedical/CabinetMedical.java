package ma.dentalTech.entities.cabinetMedical;

public class CabinetMedical {
    private Long id;
    private String nom;
    private String adresse;
    private String ville;
    private String email;
    private String telephone;
    private String siteWeb;
    private String horaires;
    private String description;

    public CabinetMedical() {}
    public CabinetMedical(Long id, String nom, String adresse, String ville, String email, String telephone, String siteWeb, String horaires, String description) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.email = email;
        this.telephone = telephone;
        this.siteWeb = siteWeb;
        this.horaires = horaires;
        this.description = description;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getNom() { return nom; }

        public void setNom(String nom) { this.nom = nom; }

        public String getAdresse() { return adresse; }

        public void setAdresse(String adresse) { this.adresse = adresse; }

        public String getVille() { return ville; }

        public void setVille(String ville) { this.ville = ville; }

        public String getEmail() { return email; }

        public void setEmail(String email) { this.email = email; }

        public String getTelephone() { return telephone; }

        public void setTelephone(String telephone) { this.telephone = telephone; }

        public String getSiteWeb() { return siteWeb; }

        public void setSiteWeb(String siteWeb) { this.siteWeb = siteWeb; }

        public String getHoraires() { return horaires; }

        public void setHoraires(String horaires) { this.horaires = horaires; }

        public String getDescription() { return description; }

        public void setDescription(String description) { this.description = description; }
}
