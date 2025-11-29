package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.model.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByLastNameStartingWith(String prefix);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.classes c WHERE c.id = :classId")
    List<Student> findByClassId(@Param("classId") Long classId);

    @Query("SELECT s FROM Student s LEFT JOIN s.classes WHERE s.id = :studentId")
    Student findWithClassesById(@Param("studentId") Long studentId);
}

