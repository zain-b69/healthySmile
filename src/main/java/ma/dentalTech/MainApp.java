package ma.dentalTech;


import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;

public class MainApp
{
    public static void main( String[] args )
    {
        ApplicationContext.getBean(PatientController.class).showRecentPatients();
    }
}
