package school.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
public class TeacherSearchRequest {

    private String search;
    private String subject;
    private Boolean classTeacher;
    private Integer page;
    private Integer size;
    private String sortField;
    private String sortDirection;

    public PageRequest toPageable() {

        int pageNum = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? Math.min(size, 50) : 12;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection != null ? sortDirection : "ASC"),
                sortField != null ? sortField : "lastName");
        return PageRequest.of(pageNum, pageSize, sort);
    }
}
