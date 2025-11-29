package school.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.dto.ApiResponse;
import school.dto.SchoolClassDTO;
import school.dto.request.CreateClassRequest;
import school.service.SchoolClassService;

import java.util.List;

@RestController
@RequestMapping(value = "api/classes")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class SchoolClassController {
    private final SchoolClassService schoolClassService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SchoolClassDTO>> getSchoolClassById(@PathVariable Long id) {
        try {
            SchoolClassDTO schoolClassDTO = schoolClassService.getClassDetails(id);
            return ResponseEntity.ok(ApiResponse.success(schoolClassDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/name/{className}")
    public ResponseEntity<ApiResponse<SchoolClassDTO>> getClassByName(@PathVariable String className) {
        try {
            SchoolClassDTO schoolClassDTO = schoolClassService.getClassByClassName(className);
            return ResponseEntity.ok(ApiResponse.success(schoolClassDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SchoolClassDTO>>> searchClasses(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String academicYear) {
//        Реализация поиска
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SchoolClassDTO>> createClass(@Valid @RequestBody CreateClassRequest request) {
        try {
            SchoolClassDTO createdClass = schoolClassService.createNewClass(request);

            ApiResponse<SchoolClassDTO> response = ApiResponse.<SchoolClassDTO>builder()
                    .success(true)
                    .message("Класс успешно создан")
                    .data(createdClass)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (school.exception.ValidationException e) {
            ApiResponse<SchoolClassDTO> response = ApiResponse.<SchoolClassDTO>builder()
                    .success(false)
                    .message("Ошибка валидацииЖ " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        } catch (school.exception.ResourceNotFoundException e) {
            ApiResponse<SchoolClassDTO> response = ApiResponse.<SchoolClassDTO>builder()
                    .success(false)
                    .message("Ресурс не найден: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<SchoolClassDTO> response = ApiResponse.<SchoolClassDTO>builder()
                    .success(false)
                    .message("Внутренняя ошибка серверк: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}