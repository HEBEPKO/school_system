package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.model.Teacher;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findBySubject(String subject);

    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.classes c WHERE c.id = :classId")
    List<Teacher> findByClassId(@Param("classId") Long classId);

    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.classes WHERE t.id = :teacherId")
    Teacher findWithClassesById(@Param("teacherId") Long teacherId);

    List<Teacher> findByClassTeacherTrue();
}