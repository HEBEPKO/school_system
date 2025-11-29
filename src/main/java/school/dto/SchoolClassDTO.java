package school.dto;

import lombok.Data;

import java.util.List;

@Data
public class SchoolClassDTO {
    private Long id;
    private String className;
    private String academicYear;
    private TeacherDTO classTeacher;
    private List<StudentDTO> students;
    private List<TeacherDTO> teachers;
}
