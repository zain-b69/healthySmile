package ma.dentalTech.repository.modules.patient.fileBase_implementation;

import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implémentation FILE-BASED via NIO
 * Lecture initiale depuis src/main/resources/fileBase/patients.psv
 * Sauvegarde modifiable dans ~/.dentaltech/fileBase/patients.psv
 * Format :
 * ID|Nom|Prenom|Adresse|Telephone|Email|DateNaissance|DateCreation|Sexe|Assurance
 */
public class PatientRepositoryImpl implements PatientRepository {

    private static final String CLASSPATH_FILE = "fileBase/patients.psv";
    private static final String HEADER = "ID|Nom|Prenom|Adresse|Telephone|Email|DateNaissance|DateCreation|Sexe|Assurance";


    private final Path localFilePath =
            Paths.get(System.getProperty("user.home"), ".dentaltech", "fileBase", "patients.psv");

    public PatientRepositoryImpl() {
        initializeLocalCopy();
    }

    /** Copie initiale de /resources/fileBase → ~/.dentaltech/fileBase */
    // après utilisation de l'application, si vous modifiez le fichier patients.psv
    // il faut supprimer la copie crée dans Target avec :
    // rm -f ~/.dentaltech/fileBase/patients.psv
    // puis relancez
    private void initializeLocalCopy() {
        try {
            if (!Files.exists(localFilePath)) {
                Files.createDirectories(localFilePath.getParent());
                URL resource = getClass().getClassLoader().getResource(CLASSPATH_FILE);
                if (resource != null) {
                    Path src = Paths.get(resource.toURI());
                    Files.copy(src, localFilePath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.write(localFilePath, List.of(HEADER), StandardCharsets.UTF_8);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Erreur d’initialisation du fichier patients.psv", e);
        }
    }


    private List<String> readAllLines() {
        try {
            return Files.readAllLines(localFilePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du fichier patients.psv", e);
        }
    }

    private void writeAllLines(List<String> lines) {
        try {
            Files.write(localFilePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erreur d’écriture dans patients.psv", e);
        }
    }

    // ===================== CRUD =====================

    @Override
    public List<Patient> findAll() {
        return readAllLines().stream()
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(this::toPatient)
                .collect(Collectors.toList());
    }

    @Override
    public Patient findById(Long id) {
        return findAll().stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void create(Patient patient) {
        List<Patient> patients = findAll();
        long newId = patients.stream()
                             .mapToLong(p -> p.getId() == null ? 0 : p.getId())
                             .max().orElse(0) + 1;
        patient.setId(newId);
        if (patient.getDateCreation() == null)
            patient.setDateCreation(LocalDateTime.now());
        patients.add(patient);
        saveAll(patients);
    }

    @Override
    public void update(Patient patient) {
        List<Patient> patients = findAll();
        for (int i = 0; i < patients.size(); i++) {
            if (Objects.equals(patients.get(i).getId(), patient.getId())) {
                patients.set(i, patient);
                saveAll(patients);
                return;
            }
        }
        throw new RuntimeException("Patient avec ID " + patient.getId() + " introuvable.");
    }

    @Override
    public void delete(Patient patient) {
        if (patient != null && patient.getId() != null)
            deleteById(patient.getId());
    }

    @Override
    public void deleteById(Long id) {
        List<Patient> patients = findAll().stream()
                .filter(p -> !Objects.equals(p.getId(), id))
                .collect(Collectors.toList());
        saveAll(patients);
    }

    // ===================== UTILITAIRES =====================

    private void saveAll(List<Patient> patients) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Patient p : patients) {
            lines.add(String.join("|",
                    stringOrNull(p.getId()),
                    stringOrNull(p.getNom()),
                    stringOrNull(p.getPrenom()),
                    stringOrNull(p.getAdresse()),
                    stringOrNull(p.getTelephone()),
                    stringOrNull(p.getEmail()),
                    stringOrNull(p.getDateNaissance()),
                    stringOrNull(p.getDateCreation()),
                    stringOrNull(p.getSexe()),
                    stringOrNull(p.getAssurance())
            ));
        }
        writeAllLines(lines);
    }

    private Patient toPatient(String line) {
        String[] t = line.split("\\|", -1);
        Patient p = new Patient();
        p.setId(parseLong(t[0]));
        p.setNom(parseNullableString(t[1]));
        p.setPrenom(parseNullableString(t[2]));
        p.setAdresse(parseNullableString(t[3]));
        p.setTelephone(parseNullableString(t[4]));
        p.setEmail(parseNullableString(t[5]));
        p.setDateNaissance(parseNullableLocalDate(t[6]));
        p.setDateCreation(parseNullableLocalDateTime(t[7]));
        p.setSexe(parseSexe(t[8]));
        p.setAssurance(parseAssurance(t[9]));
        return p;
    }

    // ===================== Parsing helpers =====================

    private String parseNullableString(String s) {
        return (s == null || s.isBlank() || s.equalsIgnoreCase("null")) ? null : s.trim();
    }

    private LocalDate parseNullableLocalDate(String s) {
        if (s == null || s.isBlank() || s.equalsIgnoreCase("null")) return null;
        return LocalDate.parse(s.trim());
    }

    private LocalDateTime parseNullableLocalDateTime(String s) {
        if (s == null || s.isBlank() || s.equalsIgnoreCase("null")) return null;
        return LocalDateTime.parse(s.trim());
    }

    private Sexe parseSexe(String s) {
        if (s == null || s.isBlank() || s.equalsIgnoreCase("null")) return null;
        try {
            return Sexe.valueOf(capitalize(s.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    private Assurance parseAssurance(String s) {
        if (s == null || s.isBlank() || s.equalsIgnoreCase("null")) return null;
        try {
            return Assurance.valueOf(capitalize(s.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    // ===================== Conversion helpers =====================

    private String stringOrNull(Object o) {
        if (o == null) return "null";
        if (o instanceof Enum<?>) return ((Enum<?>) o).name();
        return o.toString();
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isBlank() || s.equalsIgnoreCase("null")) ? null : Long.parseLong(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
