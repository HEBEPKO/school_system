package school.dto;

import lombok.Data;

@Data
public class TeacherDTO {
    private Long id;
    private String firstName;
    private String secondName;
    private String lastName;
    private String fullName;
    private String subject;
    private String email;
    private boolean classTeacher;

}
