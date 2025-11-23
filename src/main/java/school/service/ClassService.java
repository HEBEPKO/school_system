package school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.dto.ClassDTO;
import school.dto.StudentDTO;
import school.dto.TeacherDTO;
import school.model.Class;
import school.model.Student;
import school.model.Teacher;
import school.repository.ClassRepository;
import school.repository.StudentRepository;
import school.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public ClassDTO getClassDetails (Long classId) {
        Class schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));
        return convertToDTO(schoolClass);
    }

    @Transactional(readOnly = true)
    public ClassDTO getClassByClassName(String className) {
        Class schoolClass = classRepository.findByClassName(className)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));
        return convertToDTO(schoolClass);
    }

    private ClassDTO convertToDTO(Class schoolClass) {
        ClassDTO dto = new  ClassDTO();
        dto.setId(schoolClass.getId());
        dto.setClassName(schoolClass.getClassName());
        dto.setAcademicYear(schoolClass.getAcademicYear());

        if (schoolClass.getTeachers() != null) {
            dto.setClassTeacher(convertTeacherToDTO(schoolClass.getClassTeacher()));
        }

        dto.setStudents(schoolClass.getStudents().stream()
                .map(this::convertStudentToDTO)
                .collect(Collectors.toList()));

        dto.setTeachers(schoolClass.getTeachers().stream()
                .map(this::convertTeacherToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private StudentDTO convertStudentToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setSecondName(student.getSecondName());
        dto.setLastName(student.getLastName());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        return dto;
    }

    private TeacherDTO convertTeacherToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setSecondName(teacher.getSecondName());
        dto.setLastName(teacher.getLastName());
        dto.setFullName(teacher.getFullName());
        dto.setSubject(teacher.getSubject());
        dto.setEmail(teacher.getEmail());
        dto.setClassTeacher(teacher.isClassTeacher());
        return dto;
    }

    public List<Class> findAll() {
        return classRepository.findAll();
    }
}
