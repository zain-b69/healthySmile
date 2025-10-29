package ma.dentalTech.mvc.controllers.modules.patient.batch_implentation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.service.modules.patient.api.PatientService;

@Data @AllArgsConstructor @NoArgsConstructor
public class PatientControllerImpl implements PatientController {

    private PatientService service;

    @Override
    public void showRecentPatients() {
        List<PatientDTO> dtos = service.getTodayPatientsAsDTO();
        if (dtos.isEmpty()) {
            System.out.println("Aucun patient ajouté aujourd'hui.");
            return;
        }
        System.out.println("=== Patients ajoutés aujourd'hui ===");
        dtos.forEach(dto -> System.out.printf("- %s | %d ans | ajouté le %s%n",
                dto.getNomComplet(), dto.getAge(), dto.getDateCreationFormatee()));
    }
}
