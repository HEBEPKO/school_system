package school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.dto.ApiResponse;
import school.dto.StudentDTO;
import school.model.SchoolClass;
import school.model.Student;
import school.repository.SchoolClassRepository;
import school.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Transactional(readOnly = true)
    public Student getStudentWithClasses(Long studentId) {
        return studentRepository.findWithClassesById(studentId);
    }

    @Transactional(readOnly = true)
    public  void addStudentToClass(Long studentId, Long classId) {
        Student student = studentRepository.findWithClassesById(studentId);
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        student.addClass(schoolClass);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public void removeStudentFromClass(Long studentId, Long classId) {
        Student student = studentRepository.findWithClassesById(studentId);
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));
        student.removeClass(schoolClass);
        studentRepository.save(student);
    }



    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String className) {
        // Реализация поиска и пагинации студентов
        return null;
    }

}