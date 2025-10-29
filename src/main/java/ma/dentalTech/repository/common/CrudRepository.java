package ma.dentalTech.repository.common;

import java.util.List;

public interface CrudRepository<T, ID> {

    List<T> findAll();

    T findById(ID id);

    void create(T patient);

    void update(T patient);

    void delete(T patient);

    void deleteById(ID id);
}
