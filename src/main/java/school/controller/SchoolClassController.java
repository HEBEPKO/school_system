package school.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.ApiResponse;
import school.dto.ClassDTO;
import school.dto.request.CreateClassRequest;
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

    @PostMapping
    public ResponseEntity<ApiResponse<ClassDTO>> createClass(@Valid @RequestBody CreateClassRequest request) {
        try {
            ClassDTO createdClass = classService.createNewClass(request);

            ApiResponse<ClassDTO> response = ApiResponse.<ClassDTO>builder()
                    .success(true)
                    .message("Класс успешно создан")
                    .data(createdClass)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (school.exception.ValidationException e) {
            ApiResponse<ClassDTO> response = ApiResponse.<ClassDTO>builder()
                    .success(false)
                    .message("Ошибка валидацииЖ " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        } catch (school.exception.ResourceNotFoundException e) {
            ApiResponse<ClassDTO> response = ApiResponse.<ClassDTO>builder()
                    .success(false)
                    .message("Ресурс не найден: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<ClassDTO> response = ApiResponse.<ClassDTO>builder()
                    .success(false)
                    .message("Внутренняя ошибка серверк: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}