package school.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String firstName;
    private String secondName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
}
