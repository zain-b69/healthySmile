package ma.dentalTech.mvc.controllers.modules.agenda.impl;

import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaMensuelController;
import ma.dentalTech.mvc.ui.modules.agenda.CreateAgendaMensuelDialog;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;

import javax.swing.*;

public class AgendaMensuelControllerImpl implements AgendaMensuelController {

    private final AgendaMensuelService agendaService;
    private final MedecinRepository medecinRepo;

    public AgendaMensuelControllerImpl(AgendaMensuelService agendaService,
                                       MedecinRepository medecinRepo) {
        this.agendaService = agendaService;
        this.medecinRepo = medecinRepo;
    }

    @Override
    public void showCreateAgendaDialog(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            CreateAgendaMensuelDialog dlg = new CreateAgendaMensuelDialog(
                    parent,
                    medecinRepo,
                    agendaService
            );
            dlg.setVisible(true);
        });
    }
}
