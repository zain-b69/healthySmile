package ma.dentalTech.entities.antecedents;

import ma.dentalTech.entities.enums.NiveauDeRisque;

public class Antecedents {
    private Long id;
    private String nom;
    private String cat;
    private NiveauDeRisque niveauDeRisque;

    public Antecedents() {}
    
    public Antecedents(Long id, Long patientId, String nom, String cat, NiveauDeRisque niveauDeRisque) {
        this.id = id;
        this.nom = nom;
        this.cat = cat;
        this.niveauDeRisque = niveauDeRisque;
    }
    
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }

    public void setNom(String nom) { this.nom = nom; }

    public String getCat() { return cat; }

    public void setCat(String cat) { this.cat = cat; }
    
    public NiveauDeRisque getNiveauDeRisque() { return niveauDeRisque; }
    
    public void setNiveauDeRisque(NiveauDeRisque niveauDeRisque) { this.niveauDeRisque = niveauDeRisque; }
}
