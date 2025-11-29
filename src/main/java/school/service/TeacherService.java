package school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.dto.TeacherDTO;
import school.model.SchoolClass;
import school.model.Teacher;
import school.repository.SchoolClassRepository;
import school.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository schoolClassRepository;

//    public Page<TeacherDTO> searchTeacher(TeacherSearchRequest request) {
//        Pageable pageable = request.toPageable();
//
//        Page<Teacher> teachers = teacherRepository.searchTeacher(
//                request.getSearch(),
//                request.getSubject(),
//                request.getClassTeacher(),
//                pageable
//        );
//
//        List<TeacherDTO> dtoList = teachers.getContent().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(dtoList, pageable, teachers.getTotalElements());
//    }

//    public List<String> getAvailableSubject() {
//        return teacherRepository.findDistinctSubjects();
//    }

    @Transactional(readOnly = true)
    public Teacher getTeacherWitchClasses(Long teacherId) {
        return teacherRepository.findWithClassesById(teacherId);
    }

    @Transactional(readOnly = true)
    public void addTeacherToClass(Long teacherId, Long classId) {
        Teacher teacher = teacherRepository.findWithClassesById(teacherId);
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));
        teacher.addClass(schoolClass);
        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public void removeTeacherFromClass(Long teacherId, Long classId) {
        Teacher teacher = teacherRepository.findWithClassesById(teacherId);
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        teacher.removeClass(schoolClass);
        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public void assignClassTeacher(Long teacherId, Long classId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Учитель не найден"));
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден"));

        if (schoolClass.getClassTeacher() != null) {
            schoolClass.getClassTeacher().setClassTeacher(false);
            schoolClass.getClassTeacher().setManagedClass(null);
        }
        teacher.setManagedClass(schoolClass);
        teacherRepository.save(teacher);
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setFullName(teacher.getFullName());
        dto.setSubject(teacher.getSubject());
        dto.setEmail(teacher.getEmail());
        dto.setClassTeacher(teacher.isClassTeacher());

       return dto;
    }
}
