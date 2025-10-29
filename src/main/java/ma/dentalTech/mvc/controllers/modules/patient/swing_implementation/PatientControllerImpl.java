package ma.dentalTech.mvc.controllers.modules.patient.swing_implementation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;
import ma.dentalTech.service.modules.patient.api.PatientService;

@Data @AllArgsConstructor @NoArgsConstructor
public class PatientControllerImpl implements PatientController {

    private PatientService service;

    @Override
    public void showRecentPatients() {
        List<PatientDTO> dtos = service.getTodayPatientsAsDTO();
        PatientView.showAsync(dtos);
    }
}
