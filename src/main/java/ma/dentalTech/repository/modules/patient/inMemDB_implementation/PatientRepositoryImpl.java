package ma.dentalTech.repository.modules.patient.inMemDB_implementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

public class PatientRepositoryImpl implements PatientRepository {

    private final List<Patient> data = new ArrayList<>();

    public PatientRepositoryImpl() {
        // Données d'exemple : 3 patients d'aujourd'hui, 1 d'hier
        LocalDateTime now = LocalDateTime.now();
        data.add(Patient.builder()
                .id(1L).nom("Amal").prenom("Z.")
                .email("amal@example.com").telephone("0611-111111")
                .dateNaissance(LocalDate.of(1995, 5, 12))
                .dateCreation(now.minusMinutes(5))
                .sexe(Sexe.Femme).assurance(Assurance.CNSS)
                .build());

        data.add(Patient.builder()
                .id(2L).nom("Hassan").prenom("B.")
                .email("hassan@example.com").telephone("0622-222222")
                .dateNaissance(LocalDate.of(1989, 9, 23))
                .dateCreation(now.minusHours(1))
                .sexe(Sexe.Homme).assurance(Assurance.CNOPS)
                .build());

        data.add(Patient.builder()
                .id(3L).nom("Nour").prenom("C.")
                .email("nour@example.com").telephone("0633-333333")
                .dateNaissance(LocalDate.of(2000, 2, 2))
                .dateCreation(now.minusMinutes(30))
                .sexe(Sexe.Femme).assurance(Assurance.Autre)
                .build());

        data.add(Patient.builder()
                .id(4L).nom("Youssef").prenom("D.")
                .email("youssef@example.com").telephone("0644-444444")
                .dateNaissance(LocalDate.of(1992, 11, 1))
                .dateCreation(now.minusDays(1)) // hier → ne doit pas s'afficher
                .sexe(Sexe.Homme).assurance(Assurance.Aucune)
                .build());

        // Tri stable par id pour cohérence (findAll renverra trié par date desc via service)
        data.sort(Comparator.comparing(Patient::getId));
    }

    @Override
    public List<Patient> findAll() { return List.copyOf(data); }

    @Override
    public Patient findById(Long id) {
        return data.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void create(Patient patient) { data.add(patient); }

    @Override
    public void update(Patient patient) {
        deleteById(patient.getId());
        data.add(patient);
    }

    @Override
    public void delete(Patient patient) { data.removeIf(p -> p.getId().equals(patient.getId())); }

    @Override
    public void deleteById(Long id) { data.removeIf(p -> p.getId().equals(id)); }
}
