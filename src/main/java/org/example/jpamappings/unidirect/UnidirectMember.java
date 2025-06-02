package org.example.jpamappings.unidirect;

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
 * 단방향 다대일(N:1) 연관관계를 나타내는 Member 엔티티
 * - 여러 명의 멤버가 하나의 팀에 소속될 수 있음
 * - Member에서 Team으로만 참조 가능 (단방향)
 */
public class UnidirectMember {

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
     * 멤버가 소속된 팀 (다대일 연관관계)
     * - @ManyToOne: 여러 멤버(Many)가 하나의 팀(One)에 소속
     * - FetchType.LAZY: 지연 로딩 설정으로 성능 최적화
     * - @JoinColumn: 외래키 컬럼명을 TEAM_ID로 지정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private UnidirectTeam unidirectTeam;

    /**
     * 멤버 생성자
     * @param username 멤버의 사용자명
     */
    public UnidirectMember(String username) {
        this.username = username;
    }
}