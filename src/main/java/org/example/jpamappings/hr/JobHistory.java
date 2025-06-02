package org.example.jpamappings.hr;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="JOB_HISTORY")
@IdClass(JobHistoryId.class)
public class JobHistory {

    @Id
    @Column(name="EMPLOYEE_ID")
    private Integer employeeId;

    @Column(name="START_DATE")
    private LocalDate startDate;

    @Column(name="END_DATE")
    private LocalDate endDate;

    @Column(name="JOB_ID")
    private String jobId;

    @Column(name="DEPARTMENT_ID")
    private Integer departmentId;

//    @ManyToOne(fetch=FetchType.LAZY)
//    @JoinColumn(name="DEPARTMENT_ID")
//    private Department department;
}
