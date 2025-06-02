package org.example.jpamappings.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

// 복합키 클래스
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobHistoryId implements Serializable {

    private Integer employeeId;
    private LocalDate startDate;
}
