package org.example.jpamappings.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="EMPLOYEES")
public class Employees {

    @Id
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "HIRE_DATE")
    private LocalDate hireDate;

    @Column(name = "JOB_ID")
    private Integer jobId;

    @Column(name = "SALARY")
    private BigDecimal salary;

    @Column(name = "COMMISSION_PCT")
    private BigDecimal commissionPct;

    @Column(name = "MANAGER_ID")
    private Integer managerId;

    @Column(name = "DEPARTMENT_ID")
    private Integer departmentId;
}
