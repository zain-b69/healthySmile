package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.CertificatService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CertificatServiceImpl implements CertificatService {

    private final CertificatRepo certificatRepo;

    public CertificatServiceImpl(CertificatRepo certificatRepo) {
        if (certificatRepo == null) throw new IllegalArgumentException("CertificatRepo requis");
        this.certificatRepo = certificatRepo;
    }

    // -------------------------
    // Métier : génération
    // -------------------------

    @Override
    public Certificat genererCertificat(Long idDm,
                                        Long idConsultation,
                                        LocalDate dateDebut,
                                        LocalDate dateFin,
                                        String noteMedecin) {

        if (idDm == null) throw new IllegalArgumentException("idDm requis");
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        if (dateDebut == null || dateFin == null) throw new IllegalArgumentException("dateDebut/dateFin requises");
        if (dateFin.isBefore(dateDebut)) throw new IllegalArgumentException("Période invalide: dateFin < dateDebut");

        Certificat c = new Certificat();
        c.setIdCertif(null);
        c.setIdDm(idDm);
        c.setIdConsultation(idConsultation);
        c.setDateDebut(dateDebut);
        c.setDateFin(dateFin);
        c.setDuree(calculerDureeJours(dateDebut, dateFin)); // logique métier
        c.setNoteMedecin(normalizeNote(noteMedecin));

        return certificatRepo.save(c);
    }

    @Override
    public Certificat mettreAJourPeriode(Long idCertif, LocalDate dateDebut, LocalDate dateFin) {
        if (idCertif == null) throw new IllegalArgumentException("idCertif requis");
        if (dateDebut == null || dateFin == null) throw new IllegalArgumentException("dateDebut/dateFin requises");
        if (dateFin.isBefore(dateDebut)) throw new IllegalArgumentException("Période invalide: dateFin < dateDebut");

        Certificat c = getById(idCertif);
        c.setDateDebut(dateDebut);
        c.setDateFin(dateFin);
        c.setDuree(calculerDureeJours(dateDebut, dateFin));

        return certificatRepo.update(c);
    }

    @Override
    public Certificat ajouterNoteMedecin(Long idCertif, String note) {
        if (idCertif == null) throw new IllegalArgumentException("idCertif requis");

        Certificat c = getById(idCertif);
        c.setNoteMedecin(normalizeNote(note));

        return certificatRepo.update(c);
    }

    // -------------------------
    // Métier : historique
    // -------------------------

    @Override
    public List<Certificat> historiqueDossier(Long idDm) {
        if (idDm == null) throw new IllegalArgumentException("idDm requis");
        return certificatRepo.findByIdDm(idDm);
    }

    @Override
    public List<Certificat> certificatsDeConsultation(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        return certificatRepo.findByIdConsultation(idConsultation);
    }

    @Override
    public List<Certificat> certificatsDansPeriode(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Dates start/end requises");
        if (end.isBefore(start)) throw new IllegalArgumentException("Intervalle invalide: end < start");
        return certificatRepo.findByDateDebutBetween(start, end);
    }

    // -------------------------
    // Métier : rendu texte
    // -------------------------

    @Override
    public String genererTexteCertificat(Certificat c) {
        if (c == null) throw new IllegalArgumentException("Certificat null");

        // On construit un texte simple, prêt pour UI/PDF plus tard
        StringBuilder sb = new StringBuilder();
        sb.append("CERTIFICAT MÉDICAL\n\n");
        sb.append("Dossier médical : ").append(c.getIdDm()).append("\n");
        sb.append("Consultation : ").append(c.getIdConsultation()).append("\n\n");
        sb.append("Période : du ").append(c.getDateDebut()).append(" au ").append(c.getDateFin()).append("\n");
        sb.append("Durée : ").append(c.getDuree() == null ? "N/A" : c.getDuree() + " jour(s)").append("\n\n");

        if (c.getNoteMedecin() != null && !c.getNoteMedecin().isBlank()) {
            sb.append("Note du médecin : ").append(c.getNoteMedecin()).append("\n");
        }

        sb.append("\nSignature / Cachet\n");
        return sb.toString();
    }

    // -------------------------
    // Utilitaires
    // -------------------------

    @Override
    public Certificat getById(Long idCertif) {
        if (idCertif == null) throw new IllegalArgumentException("idCertif requis");
        return certificatRepo.findById(idCertif)
                .orElseThrow(() -> new RuntimeException("Certificat introuvable: " + idCertif));
    }

    // -------------------------
    // Helpers métier
    // -------------------------

    private Integer calculerDureeJours(LocalDate debut, LocalDate fin) {
        // choix métier: certificat du 01 au 01 = 1 jour
        long days = ChronoUnit.DAYS.between(debut, fin) + 1;
        if (days <= 0) throw new IllegalArgumentException("Durée invalide");
        return (int) days;
    }

    private String normalizeNote(String note) {
        if (note == null) return null;
        String n = note.trim();
        return n.isBlank() ? null : n;
    }
}
