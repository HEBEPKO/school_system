package school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.ApiResponse;
import school.dto.TeacherDTO;
import school.repository.TeacherRepository;

@RestController
@RequestMapping(value = "api/teacher")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TeacherDTO>>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String subject) {
        // Реализация поиска и пагинации учителей
        return null;
    }
}
