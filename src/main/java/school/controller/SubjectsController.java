package school.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.dto.ApiResponse;

import java.util.List;

@RestController
@RequestMapping(value = "api/subject")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class SubjectsController {
//    private final SubjectsService service;

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableSubjects() {
        // Получение списка всех предметов

        return null;
    }

}
