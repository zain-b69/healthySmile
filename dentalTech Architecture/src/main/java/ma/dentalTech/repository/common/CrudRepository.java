

package ma.dentalTech.repository.common;

import java.sql.SQLException;
import java.util.List;


public interface CrudRepository<T, ID> {


    List<T> findAll();

    T findById(ID id);

    void create(T newElement) throws SQLException;

    void update(T newValuesElement);

    void delete(T patient);

    void deleteById(ID id);
}



