package ma.dentalTech.mvc.controllers.modules.statistiques.impl;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.mvc.controllers.modules.statistiques.api.StatistiquesController;
import ma.dentalTech.service.modules.cabinet.api.StatistiquesService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StatistiquesControllerImpl implements StatistiquesController {

    private final StatistiquesService service;

    public StatistiquesControllerImpl(StatistiquesService service) {
        if (service == null) throw new IllegalArgumentException("StatistiquesService requis");
        this.service = service;
    }

    @Override
    public List<Statistiques> findAll() {
        // ton service n’a pas findAll ? -> on utilise “consulterStatistiquesCabinet” si besoin
        // ici on laisse simple: si tu veux vraiment findAll, mets-le dans service+repo.
        throw new UnsupportedOperationException("findAll non exposé par le service actuel");
    }

    @Override
    public Statistiques findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id requis");
        return service.consulterStatistiquesParId(id);
    }

    @Override
    public Statistiques create(Statistiques s) {
        try {
            return service.genererStatistiques(s); // crée + reload
        } catch (SQLException e) {
            throw new RuntimeException("Erreur génération statistiques", e);
        }
    }

    @Override
    public Statistiques update(Statistiques s) {
        return service.mettreAJourStatistiques(s);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id requis");
        service.supprimerStatistiques(id);
    }

    @Override
    public List<Statistiques> findByCabinet(Long idCabinet) {
        return service.consulterStatistiquesCabinet(idCabinet, null, null);
    }

    @Override
    public List<Statistiques> findByCategorie(CategorieStatistique categorie) {
        // ton service a analyserStatistiquesParCategorie mais nécessite idCabinet
        throw new UnsupportedOperationException("Utilise findByCabinetAndCategorie (idCabinet requis)");
    }

    @Override
    public List<Statistiques> findByCabinetAndCategorie(Long idCabinet, CategorieStatistique categorie) {
        return service.analyserStatistiquesParCategorie(idCabinet, categorie);
    }

    @Override
    public List<Statistiques> findByDate(LocalDate dateFin) {
        // repo le supporte, service non exposé — si tu veux, on peut l’ajouter
        throw new UnsupportedOperationException("findByDate non exposé par le service actuel");
    }

    @Override
    public Statistiques generer(Statistiques s) {
        return create(s);
    }

    @Override
    public String rapportPerformance(Long idCabinet, LocalDate debut, LocalDate fin) {
        if (idCabinet == null) throw new IllegalArgumentException("idCabinet requis");
        return service.genererRapportPerformance(idCabinet, debut, fin);
    }

    @Override
    public String rapportComparatif(Long idCabinet,
                                    LocalDate debutCourant, LocalDate finCourant,
                                    LocalDate debutPrec, LocalDate finPrec) {
        if (idCabinet == null) throw new IllegalArgumentException("idCabinet requis");
        return service.genererRapportComparatif(idCabinet, debutCourant, finCourant, debutPrec, finPrec);
    }
}
