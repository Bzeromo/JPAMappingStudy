
package org.example.jpamappings.unidirect;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "TEAM")
/**
 * 단방향 다대일(N:1) 연관관계에서 "One" 쪽을 담당하는 Team 엔티티
 * - 여러 명의 멤버가 하나의 팀에 소속될 수 있음
 * - 단방향 관계이므로 Team에서는 Member를 참조하지 않음
 */
public class UnidirectTeam {

    /**
     * 팀의 기본키 (Primary Key)
     * - IDENTITY 전략으로 데이터베이스가 자동으로 ID 생성
     * - 데이터베이스의 TEAM_ID 컬럼과 매핑
     * - Member 엔티티의 외래키로 참조됨
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_ID")
    private Long teamId;

    /**
     * 팀의 이름
     * - 데이터베이스의 NAME 컬럼과 매핑
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 팀 생성자
     * @param name 팀의 이름
     */
    public UnidirectTeam(String name) {
        this.name = name;
    }
}