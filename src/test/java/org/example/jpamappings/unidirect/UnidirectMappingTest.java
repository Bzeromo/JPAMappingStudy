package org.example.jpamappings.unidirect;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
/**
 * JPA 단방향 다대일(N:1) 연관매핑 테스트 클래스
 * - Member(다) -> Team(일) 단방향 관계 테스트
 * - 지연 로딩, N+1 문제, 영속성 관리 등 JPA 핵심 개념 검증
 */
class UnidirectMappingTest {

    /**
     * JPA EntityManager 주입
     * - 영속성 컨텍스트 관리 및 데이터베이스 작업 수행
     */
    @Autowired
    private EntityManager em;

    /**
     * 기본적인 엔티티 저장 및 조회 기능 테스트
     * - Team과 Member 생성 후 연관관계 설정
     * - 영속성 컨텍스트 초기화 후 조회하여 데이터 무결성 확인
     */
    @Test
    @DisplayName("단방향 매핑 - 기본 저장 및 조회")
    void testBasicSaveAndFind() {
        // Given - 테스트 데이터 준비
        UnidirectTeam unidirectTeam = new UnidirectTeam("개발팀");
        em.persist(unidirectTeam); // Team 엔티티 영속화
        em.flush(); // INSERT 쿼리 즉시 실행 (쓰기 지연 저장소 비우기)

        UnidirectMember member = new UnidirectMember("홍길동");
        member.setUnidirectTeam(unidirectTeam); // 연관관계 설정 (외래키 매핑)
        em.persist(member); // Member 엔티티 영속화
        em.flush(); // INSERT 쿼리 즉시 실행

        em.clear(); // 영속성 컨텍스트 초기화 (1차 캐시 비우기)

        // When - 실제 테스트 실행
        UnidirectMember foundUnidirectMember = em.find(UnidirectMember.class, member.getMemberId());

        // Then - 결과 검증
        assertThat(foundUnidirectMember).isNotNull();
        assertThat(foundUnidirectMember.getUsername()).isEqualTo("홍길동");
        assertThat(foundUnidirectMember.getUnidirectTeam()).isNotNull();
        assertThat(foundUnidirectMember.getUnidirectTeam().getName()).isEqualTo("개발팀");

        // 실행되는 SQL 로그 확인용 출력
        System.out.println("=== Member 조회 시 UnidirectTeam 정보 ===");
        System.out.println("Member: " + foundUnidirectMember.getUsername());
        System.out.println("UnidirectTeam: " + foundUnidirectMember.getUnidirectTeam().getName());
    }

    /**
     * 지연 로딩(Lazy Loading) 동작 확인 테스트
     * - @ManyToOne(fetch = FetchType.LAZY) 설정 검증
     * - 프록시 객체 생성 및 실제 데이터 접근 시점 확인
     */
    @Test
    @DisplayName("단방향 매핑 - 지연 로딩 확인")
    void testLazyLoading() {
        // Given - 테스트 데이터 준비
        UnidirectTeam unidirectTeam = new UnidirectTeam("마케팅팀");
        em.persist(unidirectTeam);

        UnidirectMember member = new UnidirectMember("김철수");
        member.setUnidirectTeam(unidirectTeam);
        em.persist(member);

        em.flush(); // 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트 클리어

        // When - Member 조회 (Team은 아직 로딩되지 않음)
        UnidirectMember foundUnidirectMember = em.find(UnidirectMember.class, member.getMemberId());

        System.out.println("=== Member 조회 후 ===");
        System.out.println("Member ID: " + foundUnidirectMember.getMemberId());
        System.out.println("Member Name: " + foundUnidirectMember.getUsername());

        // 지연 로딩으로 인한 프록시 객체 확인
        UnidirectTeam memberUnidirectTeam = foundUnidirectMember.getUnidirectTeam();
        System.out.println("UnidirectTeam 프록시 클래스: " + memberUnidirectTeam.getClass().getName());

        // Then - 실제 Team 데이터 접근 시 지연 로딩 쿼리 실행
        System.out.println("=== UnidirectTeam 데이터 접근 시점 ===");
        String teamName = memberUnidirectTeam.getName(); // 이 시점에서 SELECT 쿼리 실행

        assertThat(teamName).isEqualTo("마케팅팀");
    }

    /**
     * 단방향 매핑의 특성 확인 테스트
     * - Team에서 Member로 직접 접근할 수 없음을 검증
     * - JPQL을 사용한 역방향 조회 방법 제시
     */
    @Test
    @DisplayName("단방향 매핑 - Team에서 Member 직접 접근 불가")
    void testUnidirectionalCharacteristic() {
        // Given - 하나의 Team에 여러 Member 소속
        UnidirectTeam unidirectTeam = new UnidirectTeam("디자인팀");
        em.persist(unidirectTeam);

        UnidirectMember member1 = new UnidirectMember("이영희");
        member1.setUnidirectTeam(unidirectTeam);
        em.persist(member1);

        UnidirectMember member2 = new UnidirectMember("박민수");
        member2.setUnidirectTeam(unidirectTeam);
        em.persist(member2);

        em.flush();
        em.clear();

        // When - Team 조회
        UnidirectTeam foundUnidirectTeam = em.find(UnidirectTeam.class, unidirectTeam.getTeamId());

        // Then - Team 엔티티에는 Member 컬렉션이 없음 (단방향이므로)
        assertThat(foundUnidirectTeam).isNotNull();
        assertThat(foundUnidirectTeam.getName()).isEqualTo("디자인팀");

        // Team에서 Member 조회하려면 별도 JPQL 쿼리 필요
        List<UnidirectMember> members = em.createQuery(
                        "SELECT m FROM UnidirectMember m WHERE m.unidirectTeam.teamId = :teamId", UnidirectMember.class)
                .setParameter("teamId", foundUnidirectTeam.getTeamId())
                .getResultList();

        assertThat(members).hasSize(2);
        System.out.println("=== JPQL로 Team의 Members 조회 ===");
        members.forEach(m -> System.out.println("Member: " + m.getUsername()));
    }

    /**
     * 외래키 업데이트 테스트
     * - Member의 소속팀 변경 시 외래키 업데이트 동작 확인
     * - 연관관계 변경에 따른 UPDATE 쿼리 실행 검증
     */
    @Test
    @DisplayName("단방향 매핑 - 외래키 업데이트")
    void testForeignKeyUpdate() {
        // Given - 두 개의 Team과 하나의 Member 생성
        UnidirectTeam unidirectTeam1 = new UnidirectTeam("백엔드팀");
        UnidirectTeam unidirectTeam2 = new UnidirectTeam("프론트엔드팀");
        em.persist(unidirectTeam1);
        em.persist(unidirectTeam2);

        UnidirectMember member = new UnidirectMember("최개발");
        member.setUnidirectTeam(unidirectTeam1); // 초기 팀 설정
        em.persist(member);

        em.flush();
        em.clear();

        // When - Member의 소속팀 변경
        UnidirectMember foundUnidirectMember = em.find(UnidirectMember.class, member.getMemberId());
        UnidirectTeam newUnidirectTeam = em.find(UnidirectTeam.class, unidirectTeam2.getTeamId());

        System.out.println("=== 팀 변경 전 ===");
        System.out.println("현재 팀: " + foundUnidirectMember.getUnidirectTeam().getName());

        foundUnidirectMember.setUnidirectTeam(newUnidirectTeam); // 팀 변경
        em.flush(); // UPDATE 쿼리 실행 (외래키 값 변경)

        em.clear();

        // Then - 변경 결과 검증
        UnidirectMember updatedMember = em.find(UnidirectMember.class, member.getMemberId());
        assertThat(updatedMember.getUnidirectTeam().getName()).isEqualTo("프론트엔드팀");

        System.out.println("=== 팀 변경 후 ===");
        System.out.println("변경된 팀: " + updatedMember.getUnidirectTeam().getName());
    }

    /**
     * N+1 문제 발생 및 해결방법 테스트
     * - 지연 로딩으로 인한 N+1 문제 확인
     * - Fetch Join을 사용한 해결방법 제시
     */
    @Test
    @DisplayName("단방향 매핑 - N+1 문제 확인")
    void testNPlusOneProblem() {
        // Given - 여러 Member와 Team 생성 (N+1 문제 재현 환경)
        UnidirectTeam unidirectTeam1 = new UnidirectTeam("팀A");
        UnidirectTeam unidirectTeam2 = new UnidirectTeam("팀B");
        em.persist(unidirectTeam1);
        em.persist(unidirectTeam2);

        // 5명의 멤버 생성 (3명은 팀A, 2명은 팀B)
        for (int i = 1; i <= 5; i++) {
            UnidirectMember member = new UnidirectMember("회원" + i);
            member.setUnidirectTeam(i <= 3 ? unidirectTeam1 : unidirectTeam2);
            em.persist(member);
        }

        em.flush();
        em.clear();

        // When - N+1 문제 발생 케이스
        System.out.println("=== N+1 문제 발생 케이스 ===");
        List<UnidirectMember> members = em.createQuery("SELECT m FROM UnidirectMember m", UnidirectMember.class)
                .getResultList(); // 1번의 Member 조회 쿼리

        // 각 Member의 Team 정보 접근 시마다 추가 SELECT 쿼리 실행 (N번)
        members.forEach(m -> {
            System.out.println(m.getUsername() + " - " + m.getUnidirectTeam().getName()); // N번의 추가 쿼리
        });

        em.clear();

        // Then - Fetch Join으로 N+1 문제 해결
        System.out.println("=== Fetch Join으로 해결 ===");
        List<UnidirectMember> membersWithTeam = em.createQuery(
                        "SELECT m FROM UnidirectMember m JOIN FETCH m.unidirectTeam", UnidirectMember.class)
                .getResultList(); // Member와 Team을 한 번의 조인 쿼리로 조회

        // Team 정보 접근 시 추가 쿼리 없음 (이미 페치됨)
        membersWithTeam.forEach(m -> {
            System.out.println(m.getUsername() + " - " + m.getUnidirectTeam().getName()); // 추가 쿼리 없음
        });

        assertThat(membersWithTeam).hasSize(5);
    }

    /**
     * 영속성 전이(Cascade) 없음 확인 테스트
     * - 연관된 엔티티의 영속성 상태 관리 필요성 검증
     * - 비영속 엔티티 참조 시 예외 발생 확인
     */
    @Test
    @DisplayName("단방향 매핑 - 영속성 전이 없음 확인")
    void testNoCascade() {
        // Given - Team을 영속화하지 않은 상태
        UnidirectTeam unidirectTeam = new UnidirectTeam("테스트팀");
        // team을 persist 하지 않음 (비영속 상태)

        UnidirectMember member = new UnidirectMember("테스터");
        member.setUnidirectTeam(unidirectTeam); // 비영속 Team 참조

        // When & Then - 비영속 Team을 참조하는 Member 저장 시 예외 발생
        assertThrows(IllegalStateException.class, () -> {
            em.persist(member);
            em.flush(); // 외래키 제약조건 위반으로 예외 발생
        });

        // 올바른 순서: Team 먼저 영속화 후 Member 영속화
        em.persist(unidirectTeam); // Team을 먼저 영속 상태로 만듦
        em.persist(member);        // 그 다음 Member 영속화
        em.flush();               // 정상적으로 INSERT 쿼리 실행

        // 양쪽 엔티티 모두 정상적으로 저장됨
        assertThat(member.getMemberId()).isNotNull();
        assertThat(unidirectTeam.getTeamId()).isNotNull();
    }
}
