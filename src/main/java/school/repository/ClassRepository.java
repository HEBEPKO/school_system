package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.model.Class;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

    Optional<Class> findByClassName(String className);
    boolean existsByClassName(String className);

}