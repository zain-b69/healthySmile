package ma.dentalTech.repository.common;

import ma.dentalTech.entities.agenda.*;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.patient.*;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.cabinet.*;
import ma.dentalTech.entities.users.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class RowMappers {

    private RowMappers(){}

    // ------- Patient & Antecedent existants -------



    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getLong("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setAdresse(rs.getString("adresse"));
        p.setTelephone(rs.getString("telephone"));
        p.setEmail(rs.getString("email"));

        Date dn = rs.getDate("dateNaissance");
        if (dn != null) p.setDateNaissance(dn.toLocalDate());

        Date dc = rs.getDate("dateCreation");
        if (dc != null) p.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) p.setDateDerniereModification(dm.toLocalDateTime());

        p.setCreePar(rs.getString("creePar"));
        p.setModifiePar(rs.getString("modifiePar"));

        p.setSexe(Sexe.valueOf(rs.getString("sexe")));
        p.setAssurance(Assurance.valueOf(rs.getString("assurance")));

        // Maintenant c'est Lazy mais Si tu veux hydrater les antécédents directement :
        // p.setAntecedents(getAntecedentsOfPatient(p.getId()));

        return p;
    }

    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent a = new Antecedent();
        a.setId(rs.getLong("id"));
        a.setNom(rs.getString("nom"));
        a.setCategorie(CategorieAntecedent.valueOf(rs.getString("categorie")));
        a.setNiveauRisque(NiveauRisque.valueOf(rs.getString("niveauRisque")));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) a.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) a.setDateDerniereModification(dm.toLocalDateTime());

        a.setCreePar(rs.getString("creePar"));
        a.setModifiePar(rs.getString("modifiePar"));

        return a;
    }



    // ------- Helpers BaseEntity -------

    private static LocalDate readLocalDate(ResultSet rs, String column) throws SQLException {
        Date d = rs.getDate(column);
        return (d != null) ? d.toLocalDate() : null;
    }

    private static LocalDateTime readLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp t = rs.getTimestamp(column);
        return (t != null) ? t.toLocalDateTime() : null;
    }

    // ------- CabinetMedical -------

    public static CabinetMedical mapCabinetMedical(ResultSet rs) throws SQLException {
        CabinetMedical c = new CabinetMedical();

        c.setId(rs.getLong("id"));
        c.setNom(rs.getString("nom"));
        c.setEmail(rs.getString("email"));
        c.setLogo(rs.getString("logo"));
        c.setAdresse(rs.getString("adresse"));
        c.setCin(rs.getString("cin"));
        c.setTel1(rs.getString("tel1"));
        c.setTel2(rs.getString("tel2"));
        c.setSiteWeb(rs.getString("siteWeb"));
        c.setInstagram(rs.getString("instagram"));
        c.setFacebook(rs.getString("facebook"));
        c.setDescription(rs.getString("description"));

        c.setDateCreation(readLocalDate(rs, "dateCreation"));
        c.setDateDerniereModification(readLocalDateTime(rs, "dateDerniereModification"));
        c.setCreePar(rs.getString("creePar"));
        c.setModifiePar(rs.getString("modifiePar"));

        // Les listes staff / charges / revenues / statistiques seront chargées
        // via les repositories correspondants si besoin (lazy).
        return c;
    }

    // ------- Charges -------

    public static Charges mapCharges(ResultSet rs) throws SQLException {
        Charges ch = new Charges();

        ch.setId(rs.getLong("id"));
        ch.setTitre(rs.getString("titre"));
        ch.setDescription(rs.getString("description"));
        double montant = rs.getDouble("montant");
        if (!rs.wasNull()) ch.setMontant(montant);

        Timestamp dateTs = rs.getTimestamp("date");
        if (dateTs != null) ch.setDate(dateTs.toLocalDateTime());

        ch.setDateCreation(readLocalDate(rs, "dateCreation"));
        ch.setDateDerniereModification(readLocalDateTime(rs, "dateDerniereModification"));
        ch.setCreePar(rs.getString("creePar"));
        ch.setModifiePar(rs.getString("modifiePar"));

        // Cabinet: on ne met que l'id (lazy) – si tu veux, tu peux instancier un CabinetMedical minimal
        long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            CabinetMedical c = new CabinetMedical();
            c.setId(cabinetId);
            ch.setCabinet(c);
        }

        return ch;
    }

    // ------- Revenues -------

    public static Revenues mapRevenues(ResultSet rs) throws SQLException {
        Revenues rev = new Revenues();

        rev.setId(rs.getLong("id"));
        rev.setTitre(rs.getString("titre"));
        rev.setDescription(rs.getString("description"));
        double montant = rs.getDouble("montant");
        if (!rs.wasNull()) rev.setMontant(montant);

        Timestamp dateTs = rs.getTimestamp("date");
        if (dateTs != null) rev.setDate(dateTs.toLocalDateTime());

        rev.setDateCreation(readLocalDate(rs, "dateCreation"));
        rev.setDateDerniereModification(readLocalDateTime(rs, "dateDerniereModification"));
        rev.setCreePar(rs.getString("creePar"));
        rev.setModifiePar(rs.getString("modifiePar"));

        long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            CabinetMedical c = new CabinetMedical();
            c.setId(cabinetId);
            rev.setCabinet(c);
        }

        return rev;
    }

    // ------- Statistiques -------

    public static Statistiques mapStatistiques(ResultSet rs) throws SQLException {
        Statistiques st = new Statistiques();

        st.setId(rs.getLong("id"));
        st.setNom(rs.getString("nom"));

        String cat = rs.getString("categorie");
        if (cat != null) st.setCategorie(CategorieStatistique.valueOf(cat));

        double chiffre = rs.getDouble("chiffre");
        if (!rs.wasNull()) st.setChiffre(chiffre);

        Date d = rs.getDate("dateCalcul");
        if (d != null) st.setDateCalcul(d.toLocalDate());

        st.setDateCreation(readLocalDate(rs, "dateCreation"));
        st.setDateDerniereModification(readLocalDateTime(rs, "dateDerniereModification"));
        st.setCreePar(rs.getString("creePar"));
        st.setModifiePar(rs.getString("modifiePar"));

        long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            CabinetMedical c = new CabinetMedical();
            c.setId(cabinetId);
            st.setCabinet(c);
        }

        return st;
    }



    // ... mapPatient & mapAntecedent déjà présents

    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setAdresse(rs.getString("adresse"));
        u.setCin(rs.getString("cin"));
        u.setTel(rs.getString("tel"));
        u.setSexe(Sexe.valueOf(rs.getString("sexe")));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("motDePasse"));

        Date ld = rs.getDate("lastLoginDate");
        if (ld != null) u.setLastLoginDate(ld.toLocalDate());
        Date dn = rs.getDate("dateNaissance");
        if (dn != null) u.setDateNaissance(dn.toLocalDate());

        Date dc = rs.getDate("dateCreation");
        if (dc != null) u.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) u.setDateDerniereModification(dm.toLocalDateTime());
        u.setCreePar(rs.getString("creePar"));
        u.setModifiePar(rs.getString("modifiePar"));

        return u;
    }

    public static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setId(rs.getLong("id"));
        r.setLibelle(rs.getString("libelle"));
        r.setType(RoleType.valueOf(rs.getString("type")));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) r.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) r.setDateDerniereModification(dm.toLocalDateTime());
        r.setCreePar(rs.getString("creePar"));
        r.setModifiePar(rs.getString("modifiePar"));

        return r;
    }

    public static Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setTitre(TitreNotification.valueOf(rs.getString("titre")));
        n.setMessage(rs.getString("message"));
        n.setDate(rs.getDate("date").toLocalDate());
        n.setTime(rs.getTime("time").toLocalTime());
        n.setType(TypeNotification.valueOf(rs.getString("type")));
        n.setPriorite(PrioriteNotification.valueOf(rs.getString("priorite")));
        n.setLue(rs.getBoolean("lue"));

        Long userId = rs.getLong("utilisateur_id");
        Utilisateur u = new Utilisateur();
        u.setId(userId);
        n.setUtilisateur(u);

        Date dc = rs.getDate("dateCreation");
        if (dc != null) n.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) n.setDateDerniereModification(dm.toLocalDateTime());
        n.setCreePar(rs.getString("creePar"));
        n.setModifiePar(rs.getString("modifiePar"));

        return n;
    }

    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel a = new AgendaMensuel();
        a.setId(rs.getLong("id"));
        a.setMois(Mois.valueOf(rs.getString("mois")));
        a.setAnnee(rs.getInt("annee"));
        a.setMedecinId(rs.getLong("medecin_id"));
        // joursNonDisponibles & rendezVous seront remplis par le Repository si besoin
        return a;
    }

    public static RDV mapRDV(ResultSet rs) throws SQLException {
        RDV r = new RDV();
        r.setId(rs.getLong("id"));
        r.setDate(rs.getDate("date").toLocalDate());
        r.setHeure(rs.getTime("heure").toLocalTime());
        r.setMotif(rs.getString("motif"));
        r.setStatut(StatutRDV.valueOf(rs.getString("statut")));
        r.setNoteMedecin(rs.getString("noteMedecin"));

        Long pid = rs.getLong("patient_id");
        if (!rs.wasNull()) {
            ma.dentalTech.entities.patient.Patient p = new ma.dentalTech.entities.patient.Patient();
            p.setId(pid);
            r.setPatient(p);
        }

        Long mid = rs.getLong("medecin_id");
        if (!rs.wasNull()) {
            ma.dentalTech.entities.users.Medecin m = new ma.dentalTech.entities.users.Medecin();
            m.setId(mid);
            r.setMedecin(m);
        }

        Long did = rs.getLong("dossier_id");
        if (!rs.wasNull()) {
            DossierMedical d = new DossierMedical();
            d.setId(did);
            r.setDossierMedical(d);
        }

        Date dc = rs.getDate("dateCreation");
        if (dc != null) r.setDateCreation(dc.toLocalDate());
        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) r.setDateDerniereModification(dm.toLocalDateTime());
        r.setCreePar(rs.getString("creePar"));
        r.setModifiePar(rs.getString("modifiePar"));

        return r;
    }

    // ------- Staff (pour la navigation Cabinet <-> Staff) -------
    public static Staff mapStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();

        // Champs Utilisateur hérités
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setCin(rs.getString("cin"));
        s.setTel(rs.getString("tel"));
        s.setSexe(Sexe.valueOf(rs.getString("sexe")));
        s.setLogin(rs.getString("login"));
        s.setMotDePasse(rs.getString("motDePasse"));

        var lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) s.setLastLoginDate(lastLogin.toLocalDate());
        var dn = rs.getDate("dateNaissance");
        if (dn != null) s.setDateNaissance(dn.toLocalDate());

        var dc = rs.getDate("dateCreation");
        if (dc != null) s.setDateCreation(dc.toLocalDate());
        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) s.setDateDerniereModification(dm.toLocalDateTime());
        s.setCreePar(rs.getString("creePar"));
        s.setModifiePar(rs.getString("modifiePar"));

        // Champs Staff
        double salaire = rs.getDouble("salaire");
        if (!rs.wasNull()) s.setSalaire(salaire);

        double prime = rs.getDouble("prime");
        if (!rs.wasNull()) s.setPrime(prime);

        var dr = rs.getDate("dateRecrutement");
        if (dr != null) s.setDateRecrutement(dr.toLocalDate());

        int solde = rs.getInt("soldeConge");
        if (!rs.wasNull()) s.setSoldeCongé(solde);

        return s;
    }

    public static Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        // On réutilise la même logique que Staff
        // (Admin n'ajoute pas de champs)
        // => on remplit les champs hérités "à la main"

        a.setId(rs.getLong("id"));
        a.setNom(rs.getString("nom"));
        a.setEmail(rs.getString("email"));
        a.setAdresse(rs.getString("adresse"));
        a.setCin(rs.getString("cin"));
        a.setTel(rs.getString("tel"));
        a.setSexe(Sexe.valueOf(rs.getString("sexe")));
        a.setLogin(rs.getString("login"));
        a.setMotDePasse(rs.getString("motDePasse"));

        var lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) a.setLastLoginDate(lastLogin.toLocalDate());
        var dn = rs.getDate("dateNaissance");
        if (dn != null) a.setDateNaissance(dn.toLocalDate());

        var dc = rs.getDate("dateCreation");
        if (dc != null) a.setDateCreation(dc.toLocalDate());
        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) a.setDateDerniereModification(dm.toLocalDateTime());
        a.setCreePar(rs.getString("creePar"));
        a.setModifiePar(rs.getString("modifiePar"));

        double salaire = rs.getDouble("salaire");
        if (!rs.wasNull()) a.setSalaire(salaire);

        double prime = rs.getDouble("prime");
        if (!rs.wasNull()) a.setPrime(prime);

        var dr = rs.getDate("dateRecrutement");
        if (dr != null) a.setDateRecrutement(dr.toLocalDate());

        int solde = rs.getInt("soldeConge");
        if (!rs.wasNull()) a.setSoldeCongé(solde);


        return a;
    }


    public static Medecin mapMedecin(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();

        // Champs Utilisateur + Staff
        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setEmail(rs.getString("email"));
        m.setAdresse(rs.getString("adresse"));
        m.setCin(rs.getString("cin"));
        m.setTel(rs.getString("tel"));
        m.setSexe(Sexe.valueOf(rs.getString("sexe")));
        m.setLogin(rs.getString("login"));
        m.setMotDePasse(rs.getString("motDePasse"));

        var lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) m.setLastLoginDate(lastLogin.toLocalDate());
        var dn = rs.getDate("dateNaissance");
        if (dn != null) m.setDateNaissance(dn.toLocalDate());

        var dc = rs.getDate("dateCreation");
        if (dc != null) m.setDateCreation(dc.toLocalDate());
        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) m.setDateDerniereModification(dm.toLocalDateTime());
        m.setCreePar(rs.getString("creePar"));
        m.setModifiePar(rs.getString("modifiePar"));

        double salaire = rs.getDouble("salaire");
        if (!rs.wasNull()) m.setSalaire(salaire);

        double prime = rs.getDouble("prime");
        if (!rs.wasNull()) m.setPrime(prime);

        var dr = rs.getDate("dateRecrutement");
        if (dr != null) m.setDateRecrutement(dr.toLocalDate());

        int solde = rs.getInt("soldeConge");
        if (!rs.wasNull()) m.setSoldeCongé(solde);

        // Spécifique medecin
        m.setSpecialite(rs.getString("specialite"));

        return m;
    }

    public static Secretaire mapSecretaire(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();

        // Champs Utilisateur + Staff
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setCin(rs.getString("cin"));
        s.setTel(rs.getString("tel"));
        s.setSexe(Sexe.valueOf(rs.getString("sexe")));
        s.setLogin(rs.getString("login"));
        s.setMotDePasse(rs.getString("motDePasse"));

        var lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) s.setLastLoginDate(lastLogin.toLocalDate());
        var dn = rs.getDate("dateNaissance");
        if (dn != null) s.setDateNaissance(dn.toLocalDate());

        var dc = rs.getDate("dateCreation");
        if (dc != null) s.setDateCreation(dc.toLocalDate());
        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) s.setDateDerniereModification(dm.toLocalDateTime());
        s.setCreePar(rs.getString("creePar"));
        s.setModifiePar(rs.getString("modifiePar"));

        double salaire = rs.getDouble("salaire");
        if (!rs.wasNull()) s.setSalaire(salaire);

        double prime = rs.getDouble("prime");
        if (!rs.wasNull()) s.setPrime(prime);

        var dr = rs.getDate("dateRecrutement");
        if (dr != null) s.setDateRecrutement(dr.toLocalDate());

        int solde = rs.getInt("soldeConge");
        if (!rs.wasNull()) s.setSoldeCongé(solde);

        // Spécifique Secretaire
        s.setNumCNSS(rs.getString("numCNSS"));

        double commission = rs.getDouble("commission");
        if (!rs.wasNull()) s.setCommission(commission);

        return s;
    }

    // ... autres map si besoin
}
