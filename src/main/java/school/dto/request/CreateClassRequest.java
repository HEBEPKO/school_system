package school.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;

@Data
public class CreateClassRequest {
    @NotBlank(message = "Название класса обязательно")
    @Pattern(regexp = "^[5-9][А-Яа-я, Az]$", message = "Название класса должно быть в формате: 10А, 9Б и т.д.")
    private String className;

    @NotBlank(message = "Учебный год обязателен")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Учебный год должен быть в формате: 2024-2025")
    private String academicYear;

    @NotNull(message = "ID классного руководителя обязателен")
    private Long classTeacherId;

    private List<Long> studentsIds;
    private List<Long> teachersIds;

}
