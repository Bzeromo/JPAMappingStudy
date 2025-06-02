package org.example.jpamappings.idmapping;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "MEMBER")
/**
 * ID를 이용한 연관관계 매핑 Member 엔티티
 * - 객체 참조(@ManyToOne) 대신 외래키 ID만 직접 저장
 * - 연관관계 매핑 어노테이션 없이 단순한 컬럼으로 처리
 * - 성능상 이점이 있지만 객체지향적 설계에서는 권장되지 않음
 */
public class IdMappingMember {

    /**
     * 멤버의 기본키 (Primary Key)
     * - IDENTITY 전략으로 데이터베이스가 자동으로 ID 생성
     * - 데이터베이스의 MEMBER_ID 컬럼과 매핑
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    /**
     * 멤버의 사용자명
     * - 데이터베이스의 USERNAME 컬럼과 매핑
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 소속 팀의 ID (외래키)
     * - 객체 참조(@ManyToOne) 대신 팀의 ID만 직접 저장
     * - JPA 연관관계 매핑 어노테이션 사용하지 않음
     * - Team 엔티티와의 관계를 개발자가 수동으로 관리해야 함
     * - 장점: 프록시 객체 없음, 지연 로딩 이슈 없음
     * - 단점: 객체지향적이지 않음, 연관 객체 조회 시 별도 쿼리 필요
     */
    @Column(name = "TEAM_ID")
    private Long teamId;  // 객체 참조 대신 ID만 저장

    /**
     * ID 매핑 멤버 생성자
     * @param username 멤버의 사용자명
     * @param teamId 소속 팀의 ID (외래키 값)
     */
    public IdMappingMember(String username, Long teamId) {
        this.username = username;
        this.teamId = teamId;
    }
}
