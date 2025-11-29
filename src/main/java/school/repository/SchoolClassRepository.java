package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.model.Class;
import school.model.Teacher;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

    Optional<Class> findByClassName(String className);
    boolean existsByClassName(String className);
    boolean existsByClassNameAndAcademicYear(@Param("className") String className,
                                             @Param("academicYear") String academicYear);
    boolean existsByClassTeacherAndAcademicYear(@Param("classTeacher") Teacher classTeacher ,
                                                @Param("academicYear") String academicYear);

    @Query("SELECT c FROM Class c WHERE " +
            "(:className IS NULL OR LOWER(c.className) LIKE LOWER(CONCAT('%', :className, '%') " +
            "(:academicYear IS NUUL OR c.academicYear = :academicYear)")
    List<Class> searchClasses(@Param("className") String className,
                              @Param("academicYear") String academicYer);

}