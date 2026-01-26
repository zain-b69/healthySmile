

package ma.dentalTech.repository.common;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;

import java.sql.SQLException;
import java.util.List;


public interface CrudRepository<T, ID> {


    List<T> findAll();

    T findById(ID id);

    T create(T newElement) throws SQLException;

    T update(T newValuesElement);

    void delete(T patient);

    void deleteById(ID id);
}



