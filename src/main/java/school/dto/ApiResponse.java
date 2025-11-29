package school.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse <T>{
    private boolean success;
    private String messages;
    private T data;
    private List<String> errors;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;

    }

    public static <T> ApiResponse<T> error(String messages) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessages(messages);
        return response;
    }

    public static <T> ApiResponse<T> error(List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setErrors(errors);
        return response;
    }
}