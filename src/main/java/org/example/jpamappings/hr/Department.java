package org.example.jpamappings.hr;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="DEPARTMENTS")
public class Department {

    @Id
    @Column(name="DEPARTMENT_ID")
    private Integer departmentId;

    @Column(name="DEPARTMENT_NAME")
    private String departmentName;

    @Column(name="MANAGER_ID")
    private Integer managerId;

    @Column(name="LOCATION_ID")
    private Integer locationId; // 외래키관계

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="LOCATION_ID")
//    private Location location; // 객체 참조
}
