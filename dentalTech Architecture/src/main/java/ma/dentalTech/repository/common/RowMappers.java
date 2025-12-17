package ma.dentalTech.repository.common;

import ma.dentalTech.entities.agenda.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.patient.*;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.cabinet.*;
import ma.dentalTech.entities.users.*;

import java.sql.*;

public final class RowMappers {

    private RowMappers(){}

    // ===== Helpers =====

    private static void fillAudit(BaseEntity e, ResultSet rs) throws SQLException {
        //tous les find…() qui utilisent ces mappers doivent JOIN entite.
        Date dc = rs.getDate("date_creation");
        e.setDateCreation(dc != null ? dc.toLocalDate() : null);

        Timestamp dm = rs.getTimestamp("date_derniere_modification");
        e.setDateDerniereModification(dm != null ? dm.toLocalDateTime() : null);

        e.setCreePar(rs.getString("cree_par"));
        e.setModifiePar(rs.getString("modifie_par"));
    }

    // ===== Patient =====
    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();

        p.setIdPatient(rs.getLong("id_patient"));
        p.setId(rs.getObject("id_entite", Long.class)); // BaseEntity.id = id_entite

        p.setNom(rs.getString("nom"));

        Date dn = rs.getDate("date_de_naissance");
        p.setDateNaissance(dn != null ? dn.toLocalDate() : null);

        String sexe = rs.getString("sexe");
        p.setSexe(sexe != null ? Sexe.valueOf(sexe) : null);

        p.setAdresse(rs.getString("adresse"));
        p.setTelephone(rs.getString("telephone"));

        String assurance = rs.getString("assurance");
        p.setAssurance(assurance != null ? Assurance.valueOf(assurance) : null);

        fillAudit(p, rs); // nécessite JOIN entite
        return p;
    }

    // ===== Antecedent =====
    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent a = new Antecedent();

        a.setId(rs.getObject("id_entite", Long.class)); // BaseEntity.id = id_entite
        a.setIdAntecedent(rs.getLong("id_antecedent"));
        a.setNom(rs.getString("nom"));
        a.setCategorie(rs.getString("categorie"));

        String nr = rs.getString("niveau_de_risque");
        a.setNiveauDeRisque(nr != null ? NiveauRisque.valueOf(nr) : null);

        fillAudit(a, rs); // nécessite JOIN entite
        return a;
    }

    // ===== Statistiques =====
    public static Statistiques mapStatistiques(ResultSet rs) throws SQLException {
        Statistiques st = new Statistiques();

        st.setIdStatistique(rs.getLong("id_statistique"));
        st.setId(rs.getObject("id_entite", Long.class));      // BaseEntity.id = id_entite
        st.setIdCabinet(rs.getObject("id_cabinet", Long.class));

        st.setPeriode(rs.getString("periode"));

        Date dd = rs.getDate("date_debut");
        st.setDateDebut(dd != null ? dd.toLocalDate() : null);

        Date df = rs.getDate("date_fin");
        st.setDateFin(df != null ? df.toLocalDate() : null);

        st.setNbPatients(rs.getObject("nb_patients", Integer.class));
        st.setNbRdv(rs.getObject("nb_rdv", Integer.class));
        st.setNbConsultation(rs.getObject("nb_consultation", Integer.class));
        st.setRevenuTotal(rs.getObject("revenu_total", Double.class));
        st.setTauxAnnulation(rs.getObject("taux_annulation", Double.class));
        st.setActePlusEffectif(rs.getString("acte_plus_effectif"));

        fillAudit(st, rs); // nécessite JOIN entite

        Long idCabinet = st.getIdCabinet();
        if (idCabinet != null) {
            CabinetMedical c = new CabinetMedical();
            c.setIdCabinet(idCabinet);
            st.setCabinet(c);
        }

        return st;
    }

    // ===== Utilisateur (selon SHOW CREATE TABLE utilisateur) =====
    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();

        u.setIdUser(rs.getLong("id_user"));
        u.setId(rs.getObject("id_entite", Long.class)); // BaseEntity.id = id_entite

        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setAdresse(rs.getString("adresse"));
        u.setCin(rs.getString("cin"));

        // DB telephone -> Java tel
        u.setTel(rs.getString("telephone"));

        String sexe = rs.getString("sexe");
        u.setSexe(sexe != null ? Sexe.valueOf(sexe) : null);

        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("mot_de_passe"));

        Date dl = rs.getDate("dernier_login");
        u.setLastLoginDate(dl != null ? dl.toLocalDate() : null);

        Date dn = rs.getDate("date_naissance");
        u.setDateNaissance(dn != null ? dn.toLocalDate() : null);

        Long idRole = rs.getObject("id_role", Long.class);
        if (idRole != null) {
            Role r = new Role();
            r.setIdRole(idRole); // à remplacer par setIdRole si tu ajoutes idRole dans Role
            u.setRole(r);
        }

        fillAudit(u, rs); // nécessite JOIN entite
        return u;
    }

    // ===== Notification =====
    public static Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        n.setIdNotification(rs.getLong("id_notification"));          // PK
        n.setId(rs.getObject("id_entite", Long.class));              // BaseEntity.id = id_entite

        String titre = rs.getString("titre");
        n.setTitre(titre != null ? TitreNotification.valueOf(titre) : null);

        n.setMessage(rs.getString("message"));

        String type = rs.getString("type_notification");
        n.setType(type != null ? TypeNotification.valueOf(type) : null);

        Timestamp ts = rs.getTimestamp("date_notification");
        if (ts != null) {
            var ldt = ts.toLocalDateTime();
            n.setDate(ldt.toLocalDate());
            n.setTime(ldt.toLocalTime());
        }

        n.setLue(rs.getBoolean("lu"));

        Long idUser = rs.getObject("id_utilisateur", Long.class);
        if (idUser != null) {
            Utilisateur u = new Utilisateur();
            u.setIdUser(idUser);
            n.setUtilisateur(u);
        }

        fillAudit(n, rs); // uniquement si JOIN entite
        return n;
    }



    // ===== AgendaMensuel =====
    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel a = new AgendaMensuel();
        a.setIdAgenda(rs.getLong("id_agenda"));
        a.setMois(rs.getObject("mois", Integer.class));
        a.setAnnee(rs.getObject("annee", Integer.class));
        a.setIdMedecin(rs.getObject("id_medecin", Long.class));
        return a;
    }

    // ===== RDV =====
    public static RDV mapRDV(ResultSet rs) throws SQLException {
        RDV r = new RDV();

        r.setIdRdv(rs.getLong("id_rdv"));

        Long idEntite = rs.getObject("id_entite", Long.class);
        r.setId(idEntite);
        r.setIdEntite(idEntite); // si tu gardes ce champ (doublon)

        r.setIdPatient(rs.getLong("id_patient"));
        r.setIdMedecin(rs.getLong("id_medecin"));
        r.setIdDm(rs.getLong("id_dm"));

        Date d = rs.getDate("date");
        r.setDate(d != null ? d.toLocalDate() : null);

        r.setHeure(rs.getString("heure"));
        r.setMotif(rs.getString("motif"));

        String statut = rs.getString("statut");
        r.setStatut(statut != null ? StatutRDV.valueOf(statut) : null);

        r.setNoteMedecin(rs.getString("note_medecin"));
        r.setIdCreneau(rs.getObject("id_creneau", Long.class));

        fillAudit(r, rs); // nécessite JOIN entite

        // lazy objects
        Patient p = new Patient();
        p.setIdPatient(r.getIdPatient());
        r.setPatient(p);

        Medecin m = new Medecin();
        m.setId(r.getIdMedecin());
        r.setMedecin(m);

        DossierMedical dossier = new DossierMedical();
        dossier.setId(r.getIdDm());
        r.setDossierMedical(dossier);

        return r;
    }

    public static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();

        r.setIdRole(rs.getLong("id_role"));                         // PK
        r.setId(rs.getObject("id_entite", Long.class));              // BaseEntity.id = id_entite

        String lib = rs.getString("libelle");                        // ADMIN/MEDECIN/...
        r.setType(lib != null ? RoleType.valueOf(lib) : null);

        fillAudit(r, rs); // uniquement si JOIN entite
        return r;
    }


    public static CabinetMedical mapCabinetMedical(ResultSet rs) throws SQLException {
        CabinetMedical c = new CabinetMedical();

        c.setIdCabinet(rs.getLong("id_cabinet"));
        c.setIdUser(rs.getLong("id_user"));

        c.setNom(rs.getString("nom"));
        c.setEmail(rs.getString("email"));
        c.setLogo(rs.getString("logo"));
        c.setAdresse(rs.getString("adresse"));
        c.setCin(rs.getString("cin"));
        c.setTel1(rs.getString("tel1"));
        c.setTel2(rs.getString("tel2"));
        c.setSiteWeb(rs.getString("site_web"));
        c.setInstagram(rs.getString("instagram"));
        c.setFacebook(rs.getString("facebook"));
        c.setDescription(rs.getString("description"));

        return c;
    }


    public static Staff mapStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();

        // utilisateur
        s.setIdUser(rs.getLong("id_user"));
        s.setId(rs.getObject("id_entite", Long.class)); // BaseEntity.id = id_entite

        s.setNom(rs.getString("nom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setCin(rs.getString("cin"));
        s.setTel(rs.getString("telephone"));

        String sexe = rs.getString("sexe");
        s.setSexe(sexe != null ? Sexe.valueOf(sexe) : null);

        s.setLogin(rs.getString("login"));
        s.setMotDePasse(rs.getString("mot_de_passe"));

        Date dl = rs.getDate("dernier_login");
        s.setLastLoginDate(dl != null ? dl.toLocalDate() : null);

        Date dn = rs.getDate("date_naissance");
        s.setDateNaissance(dn != null ? dn.toLocalDate() : null);

        // role lazy (id_role existe dans utilisateur)
        Long idRole = rs.getObject("id_role", Long.class);
        if (idRole != null) {
            Role r = new Role();
            r.setIdRole(idRole); // idéalement: r.setIdRole(idRole) si tu ajoutes idRole dans Role
            s.setRole(r);
        }

        fillAudit(s, rs);

        // staff
        s.setSalaire(rs.getObject("salaire", Double.class));
        s.setPrime(rs.getObject("prime", Double.class));

        Date dr = rs.getDate("date_recrutement");
        s.setDateRecrutement(dr != null ? dr.toLocalDate() : null);

        s.setSoldeCongé(rs.getObject("solde_conge", Integer.class));

        Long cabinetId = rs.getObject("cabinet_id", Long.class);
        if (cabinetId != null) {
            CabinetMedical cab = new CabinetMedical();
            // si CabinetMedical a setIdCabinet:
            // cab.setIdCabinet(cabinetId);
            // sinon (si CabinetMedical étend BaseEntity et utilise id comme PK):
            cab.setIdCabinet(cabinetId);
            s.setCabinetMedical(cab);
        }

        return s;
    }


    public static Medecin mapMedecin(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();

        // même mapping que Staff (copie identique)
        m.setIdUser(rs.getLong("id_user"));
        m.setId(rs.getObject("id_entite", Long.class));

        m.setNom(rs.getString("nom"));
        m.setEmail(rs.getString("email"));
        m.setAdresse(rs.getString("adresse"));
        m.setCin(rs.getString("cin"));
        m.setTel(rs.getString("telephone"));

        String sexe = rs.getString("sexe");
        m.setSexe(sexe != null ? Sexe.valueOf(sexe) : null);

        m.setLogin(rs.getString("login"));
        m.setMotDePasse(rs.getString("mot_de_passe"));

        Date dl = rs.getDate("dernier_login");
        m.setLastLoginDate(dl != null ? dl.toLocalDate() : null);

        Date dn = rs.getDate("date_naissance");
        m.setDateNaissance(dn != null ? dn.toLocalDate() : null);

        Long idRole = rs.getObject("id_role", Long.class);
        if (idRole != null) {
            Role r = new Role();
            r.setIdRole(idRole);
            m.setRole(r);
        }

        fillAudit(m, rs);

        m.setSalaire(rs.getObject("salaire", Double.class));
        m.setPrime(rs.getObject("prime", Double.class));

        Date dr = rs.getDate("date_recrutement");
        m.setDateRecrutement(dr != null ? dr.toLocalDate() : null);

        m.setSoldeCongé(rs.getObject("solde_conge", Integer.class));

        Long cabinetId = rs.getObject("cabinet_id", Long.class);
        if (cabinetId != null) {
            CabinetMedical cab = new CabinetMedical();
            cab.setIdCabinet(cabinetId);
            m.setCabinetMedical(cab);
        }

        // spécifique medecin
        m.setSpecialite(rs.getString("specialite"));

        // agenda mensuel: pas de FK direct dans ta table medecin/staff -> à charger via service
        m.setAgendaMensuel(null);

        return m;
    }


    public static Secretaire mapSecretaire(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();

        // même mapping que Staff
        s.setIdUser(rs.getLong("id_user"));
        s.setId(rs.getObject("id_entite", Long.class));

        s.setNom(rs.getString("nom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setCin(rs.getString("cin"));
        s.setTel(rs.getString("telephone"));

        String sexe = rs.getString("sexe");
        s.setSexe(sexe != null ? Sexe.valueOf(sexe) : null);

        s.setLogin(rs.getString("login"));
        s.setMotDePasse(rs.getString("mot_de_passe"));

        Date dl = rs.getDate("dernier_login");
        s.setLastLoginDate(dl != null ? dl.toLocalDate() : null);

        Date dn = rs.getDate("date_naissance");
        s.setDateNaissance(dn != null ? dn.toLocalDate() : null);

        Long idRole = rs.getObject("id_role", Long.class);
        if (idRole != null) {
            Role r = new Role();
            r.setIdRole(idRole);
            s.setRole(r);
        }

        fillAudit(s, rs);

        s.setSalaire(rs.getObject("salaire", Double.class));
        s.setPrime(rs.getObject("prime", Double.class));

        Date dr = rs.getDate("date_recrutement");
        s.setDateRecrutement(dr != null ? dr.toLocalDate() : null);

        s.setSoldeCongé(rs.getObject("solde_conge", Integer.class));

        Long cabinetId = rs.getObject("cabinet_id", Long.class);
        if (cabinetId != null) {
            CabinetMedical cab = new CabinetMedical();
            cab.setIdCabinet(cabinetId);
            s.setCabinetMedical(cab);
        }

        // spécifique secretaire
        s.setNumCNSS(rs.getString("num_cnss"));
        s.setCommission(rs.getObject("commission", Double.class));

        return s;
    }


    public static Charges mapCharges(ResultSet rs) throws SQLException {
        Charges ch = new Charges();

        // PK
        ch.setIdCharge(rs.getLong("id_charge"));

        // FK entite -> BaseEntity.id
        ch.setId(rs.getObject("id_entite", Long.class));

        // FK cabinet
        Long idCabinet = rs.getObject("id_cabinet", Long.class);
        ch.setIdCabinet(idCabinet);

        if (idCabinet != null) {
            CabinetMedical cab = new CabinetMedical();
            // si CabinetMedical a idCabinet => cab.setIdCabinet(idCabinet);
            cab.setIdCabinet(idCabinet);
            ch.setCabinet(cab);
        }

        // champs métier
        ch.setTitre(rs.getString("titre"));
        ch.setMontant(rs.getObject("montant", Double.class));
        ch.setDescription(rs.getString("description"));

        Date d = rs.getDate("date_charge");
        ch.setDate(d != null ? d.toLocalDate().atStartOfDay() : null);

        // audit entite (si JOIN entite)
        fillAudit(ch, rs);

        return ch;
    }


    public static Revenues mapRevenues(ResultSet rs) throws SQLException {
        Revenues rev = new Revenues();

        // PK
        rev.setIdRevenu(rs.getLong("id_revenu"));

        // FK entite -> BaseEntity.id
        rev.setId(rs.getObject("id_entite", Long.class));

        // FK cabinet
        Long idCabinet = rs.getObject("id_cabinet", Long.class);
        rev.setIdCabinet(idCabinet);

        if (idCabinet != null) {
            CabinetMedical cab = new CabinetMedical();
            cab.setIdCabinet(idCabinet);
            rev.setCabinet(cab);
        }

        // champs métier
        rev.setTitre(rs.getString("titre"));
        rev.setMontant(rs.getObject("montant", Double.class));
        rev.setDescription(rs.getString("description"));

        Date d = rs.getDate("date_revenu");
        rev.setDate(d != null ? d.toLocalDate().atStartOfDay() : null);

        // audit entite (si JOIN entite)
        fillAudit(rev, rs);

        return rev;
    }

}
