package school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.ApiResponse;
import school.dto.ClassDTO;
import school.service.ClassService;

import java.util.List;

@RestController
@RequestMapping(value = "api/classes")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassDTO>> getClassById(@PathVariable Long id) {
        try {
            ClassDTO classDTO = classService.getClassDetails(id);
            return ResponseEntity.ok(ApiResponse.success(classDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/name/{className}")
    public ResponseEntity<ApiResponse<ClassDTO>> getClassByName(@PathVariable String className) {
        try {
            ClassDTO classDTO = classService.getClassByClassName(className);
            return ResponseEntity.ok(ApiResponse.success(classDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ClassDTO>>> searchClasses(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String academicYear) {
//        Реализация поиска
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}