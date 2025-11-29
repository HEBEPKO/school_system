package school.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private Integer count;
    private LocalDateTime timestamp;

    // Приватный конструктор для Builder
    @Builder
    private ApiResponse(boolean success, String message, T data, List<String> errors,
                        Integer count, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.count = count;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();

        // Автоматический подсчет количества элементов если data является списком
        if (this.count == null && data instanceof List) {
            this.count = ((List<?>) data).size();
        }
    }

    // Статический метод для создания Builder с указанием типа
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    // Вложенный класс Builder (генерируется Lombok)
    public static class ApiResponseBuilder<T> {
        // Lombok сгенерирует все необходимые методы
    }

    // ==== Фабричные методы для удобного создания ответов ====

    /**
     * Создает успешный ответ с данными
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Операция выполнена успешно");
    }

    /**
     * Создает успешный ответ с данными и кастомным сообщением
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    /**
     * Создает ответ об ошибке с сообщением
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Создает ответ об ошибке с несколькими сообщениями
     */
    public static <T> ApiResponse<T> error(List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message("Ошибки валидации")
                .errors(errors)
                .build();
    }

    /**
     * Создает ответ о не найденном ресурсе
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}