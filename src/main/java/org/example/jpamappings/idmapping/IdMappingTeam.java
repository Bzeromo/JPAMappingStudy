package org.example.jpamappings.idmapping;

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
 * ID를 이용한 연관관계 매핑에서 Team 엔티티
 * - Member와 ID 기반 연관관계를 가짐 (객체 참조 없음)
 * - 연관관계 매핑 어노테이션 없이 단순한 엔티티로 구성
 * - Member에서 teamId 필드로 이 엔티티의 ID를 참조함
 */
public class IdMappingTeam {

    /**
     * 팀의 기본키 (Primary Key)
     * - IDENTITY 전략으로 데이터베이스가 자동으로 ID 생성
     * - 데이터베이스의 TEAM_ID 컬럼과 매핑
     * - IdMappingMember의 teamId 필드에서 이 값을 외래키로 참조
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
     * ID 매핑 팀 생성자
     * @param name 팀의 이름
     */
    public IdMappingTeam(String name) {
        this.name = name;
    }
}
