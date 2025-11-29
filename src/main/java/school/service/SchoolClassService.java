package school.service;

import school.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.dto.ClassDTO;
import school.dto.request.CreateClassRequest;
import school.dto.StudentDTO;
import school.dto.TeacherDTO;
import school.exception.ResourceNotFoundException;
import school.model.Class;
import school.model.Student;
import school.model.Teacher;
import school.repository.ClassRepository;
import school.repository.StudentRepository;
import school.repository.TeacherRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public ClassDTO getClassDetails(Long classId) {
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

    public ClassDTO createNewClass(CreateClassRequest request) {
        // 1. Валидация входных данных
        validateCreatedRequest(request);
        // 2. Проверка существования класса с таким же названием в этом учебном году
        if (classRepository.existsByClassNameAndAcademicYear(request.getClassName(), request.getAcademicYear())) {
            throw new ValidationException(String.format(
                    "Класс '%s' уже существует в учебном году '%s' ",
                    request.getClassName(), request.getAcademicYear()
            ));
        }

        // 3. Получение классного руководителя
        Teacher classTeacher = teacherRepository.findById(request.getClassTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Классный руководитель с ID %d не найден", request.getClassTeacherId())
                ));

        if (!classTeacher.isClassTeacher()) {
            throw new ValidationException(
                    String.format("Учитель '%s' не является классным руководителем", classTeacher.getFullName())
            );
        }

        // 4. Проверка, не назначен ли этот учитель уже классным руководителем другого класса в том же учебном году
        if (classRepository.existsByClassTeacherAndAcademicYear(classTeacher, request.getAcademicYear())) {
            throw new ValidationException(
                    String.format("Учитель '%s' уже является классным руководителем другого класса в учебном году %s",
                            classTeacher.getFullName(), request.getAcademicYear())
            );
        }

        // 5. Получение учеников (если указаны)
        Set<Student> students = new HashSet<>();
        if (request.getStudentsIds() != null && !request.getStudentsIds().isEmpty()) {
            students = new HashSet<>(studentRepository.findAllById(request.getStudentsIds()));
            if (students.size() != request.getStudentsIds().size()) {
                List<Long> foundIds = students.stream().map(Student::getId)
                        .toList();
                List<Long> missingIds = request.getStudentsIds().stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();
                throw new ResourceNotFoundException(
                        String.format("Ученики с ID %s не найдены", missingIds)
                );
            }
        }

        // 6. Получение учителей-предметников (если указаны)
        Set<Teacher> teachers = new HashSet<>();
        if(request.getTeachersIds() != null && !request.getTeachersIds().isEmpty()) {
            teachers = new HashSet<>(teacherRepository.findAllById(request.getTeachersIds()));
            if (teachers.size() != request.getTeachersIds().size()) {
                List<Long> foundIds = teachers.stream().map(Teacher::getId).toList();
                List<Long> missingIds = request.getTeachersIds().stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();

                throw new ResourceNotFoundException(
                        String.format("Учителя с ID %s не найдены", missingIds)
                );
            }
        }

        // 7. Создание и сохранение нового класса
        Class newClass = new Class();
        newClass.setClassName(request.getClassName());
        newClass.setAcademicYear(request.getAcademicYear());
        newClass.setClassTeacher(classTeacher);
        newClass.setStudents(students);
        newClass.setTeachers(teachers);
        newClass.setCreatedAt(LocalDateTime.now());
        newClass.setUpdatedAt(LocalDateTime.now());

        Class savedClass = classRepository.save(newClass);

        // 8. Обновление связей у учеников и учителей (опционально, зависит от вашей логики)
        updateStudentClassReferences(savedClass, students);
        updateTeacherClassReferences(savedClass, teachers);

        return convertToDTO(savedClass);
    }

    private void validateCreatedRequest(CreateClassRequest request) {
        if (request == null) {
            throw new ValidationException("Данные для создания класса не предоставлены");
        }

        // Проверка длины названия класса
        if (request.getClassName() != null && request.getClassName().length() > 5) {
            throw new ValidationException("Название класса не должно превышать 5 символов");
        }

        // Проверка корректности учебного года
        if (request.getAcademicYear() != null) {
            String[] year = request.getAcademicYear().split("-");
            if (year.length == 2) {
                try {
                    int startYear = Integer.parseInt(year[0]);
                    int endYear = Integer.parseInt(year[1]);

                    if (endYear != startYear + 1) {
                        throw new ValidationException(
                                "Учебный год должен быть в формате: текущий-следующий (например, 2024-2025)"
                        );
                    }

                    // Проверка разумного диапазона лет
                    int currentYear = java.time.Year.now().getValue();
                    if (startYear < currentYear - 10 || startYear > currentYear + 1) {
                        throw new ValidationException(
                                String.format("Некорректный учебный год. Год должен быть в диапазоне %d-%d",
                                        currentYear - 10, currentYear + 1)
                        );
                    }
                } catch (NumberFormatException e) {
                    throw new ValidationException("Учебный год должен содержать только цифры");
                }
            }
        }
    }

    private void updateStudentClassReferences(Class schoolClass, Set<Student> students) {
        for (Student student : students) {
            // Если у ученика уже есть классы, добавляем новый
            Set<Class> studentClasses = student.getClasses();
            if (studentClasses == null) {
                studentClasses = new HashSet<>();
                student.setClasses(studentClasses);
            }
            studentClasses.add(schoolClass);
            studentRepository.save(student);
        }
    }

    private void updateTeacherClassReferences(Class schoolClass, Set<Teacher> teachers) {
        for (Teacher teacher : teachers) {
            // Если учитель не является классным руководителем, добавляем его в класс
            if (!teacher.equals(schoolClass.getClassTeacher())) {
                Set<Class> teacherClasses = teacher.getClasses();
                if (teacherClasses == null) {
                    teacherClasses = new HashSet<>();
                    teacher.setClasses(teacherClasses);
                }

                teacherClasses.add(schoolClass);
                teacherRepository.save(teacher);
            }
        }
    }

    private ClassDTO convertToDTO(Class schoolClass) {
        ClassDTO dto = new ClassDTO();
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