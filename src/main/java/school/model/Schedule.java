package school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
public class Schedule {
    @Id
    @GeneratedValue
    private Long id;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subject;

    @ManyToOne
    private Class schoolClass;

    @ManyToOne
    private Teacher teacher;

//    @ManyToOne
//    private Room room;
}
