package ma.dentalTech.mvc.ui.modules.catalogue;

import ma.dentalTech.mvc.controllers.modules.acte.api.ActeController;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;

import javax.swing.*;
import java.awt.*;

public class CataloguePanel extends JPanel {

    public CataloguePanel(ActeController acteController,
                          AntecedentController antecedentController) {
        super(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Actes", new ActeCataloguePanel(acteController));
        tabs.addTab("Antécédents", new AntecedentCataloguePanel(antecedentController));

        add(tabs, BorderLayout.CENTER);
    }
}
