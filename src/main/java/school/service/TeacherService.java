package school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.dto.TeacherDTO;
import school.dto.TeacherSearchRequest;
import school.model.Teacher;
import school.repository.ClassRepository;
import school.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;

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
