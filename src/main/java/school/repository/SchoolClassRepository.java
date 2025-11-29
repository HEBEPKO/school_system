package school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.model.SchoolClass;
import school.model.Teacher;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    Optional<SchoolClass> findByClassName(String className);
    boolean existsByClassName(String className);
    boolean existsByClassNameAndAcademicYear(@Param("className") String className,
                                             @Param("academicYear") String academicYear);
    boolean existsByClassTeacherAndAcademicYear(@Param("classTeacher") Teacher classTeacher ,
                                                @Param("academicYear") String academicYear);

    @Query("SELECT c FROM SchoolClass c WHERE " +
            "(:className IS NULL OR LOWER(c.className) LIKE LOWER(CONCAT('%', :className, '%'))) AND " +
            "(:academicYear IS NUlL OR c.academicYear = :academicYear)")
    List<SchoolClass> searchClasses(@Param("className") String className,
                                    @Param("academicYear") String academicYer);

}