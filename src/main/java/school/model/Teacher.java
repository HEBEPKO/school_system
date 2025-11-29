package school.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    private String secondName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String subject;

    @Column (unique = true, nullable = false)
    private String email;

    private String phone;

    @Column (name = "is_class_teacher")
    private boolean classTeacher;

    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SchoolClass> classes = new HashSet<>();

    @OneToOne(mappedBy = "classTeacher", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SchoolClass managedClass;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public String getFullName() {
        return String.format("%s %s %s", firstName, lastName, secondName != null ? secondName : "").trim();
    }

    public void addClass(SchoolClass schoolClass) {
        if (classes == null) {
            classes = new HashSet<>();
        }
        classes.add(schoolClass);
        schoolClass.getTeachers().add(this);
    }

    public void removeClass(SchoolClass schoolClass) {
        classes.remove(schoolClass);
        schoolClass.getTeachers().remove(this);
    }

    public void setManagedClass(SchoolClass schoolClass) {
        this.managedClass = schoolClass;
        if (schoolClass != null) {
            schoolClass.setClassTeacher(this);
            this.classTeacher = true;
        }
    }
}