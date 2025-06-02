package org.example.jpamappings.idmapping;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
/**
 * JPA ID 연관매핑 테스트 클래스
 * - 객체 참조 대신 ID만을 사용한 연관관계 테스트
 * - JPA 연관관계 어노테이션 없이 순수 ID 기반 관계 관리
 * - 성능 특성, 수동 관계 관리, 데이터 일관성 등을 검증
 */
public class IdMappingTest {

    /**
     * JPA EntityManager 주입
     * - 영속성 컨텍스트 관리 및 데이터베이스 작업 수행
     */
    @Autowired
    private EntityManager em;

    /**
     * ID 매핑의 기본적인 저장 및 조회 기능 테스트
     * - Team과 Member를 ID로만 연결
     * - 연관 객체 조회 시 별도 쿼리 필요함을 확인
     */
    @Test
    @DisplayName("ID 매핑 - 기본 저장 및 조회")
    void testBasicSaveAndFind() {
        // Given - 테스트 데이터 준비
        IdMappingTeam team = new IdMappingTeam("개발팀");
        em.persist(team); // Team 엔티티 영속화
        em.flush(); // Team INSERT 쿼리 즉시 실행

        Long teamId = team.getTeamId(); // 생성된 Team ID 확보

        // Member 생성 시 Team 객체 대신 teamId만 전달
        IdMappingMember member = new IdMappingMember("홍길동", teamId);
        em.persist(member); // Member 엔티티 영속화
        em.flush(); // Member INSERT 쿼리 즉시 실행

        em.clear(); // 영속성 컨텍스트 초기화 (1차 캐시 비우기)

        // When - Member 조회
        IdMappingMember foundMember = em.find(IdMappingMember.class, member.getMemberId());

        // Then - 결과 검증
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getUsername()).isEqualTo("홍길동");
        assertThat(foundMember.getTeamId()).isEqualTo(teamId);

        // ID 매핑은 객체 참조가 없음 - Team 정보는 별도 조회 필요
        IdMappingTeam foundTeam = em.find(IdMappingTeam.class, foundMember.getTeamId());
        assertThat(foundTeam.getName()).isEqualTo("개발팀");

        System.out.println("=== ID 매핑 조회 결과 ===");
        System.out.println("Member: " + foundMember.getUsername());
        System.out.println("Team ID: " + foundMember.getTeamId());
        System.out.println("Team Name: " + foundTeam.getName());
    }

    /**
     * ID 매핑의 즉시 로딩 특성 확인 테스트
     * - 프록시 객체 없이 실제 ID 값이 바로 로딩됨
     * - 지연 로딩 이슈가 발생하지 않음을 검증
     */
    @Test
    @DisplayName("ID 매핑 - 즉시 로딩(프록시 없음)")
    void testEagerLoadingCharacteristic() {
        // Given - 테스트 데이터 준비
        IdMappingTeam team = new IdMappingTeam("마케팅팀");
        em.persist(team);
        em.flush();

        IdMappingMember member = new IdMappingMember("김철수", team.getTeamId());
        em.persist(member);
        em.flush();
        em.clear();

        // When - Member 조회
        System.out.println("=== Member 조회 시작 ===");
        IdMappingMember foundMember = em.find(IdMappingMember.class, member.getMemberId());

        // Then - Team ID는 프록시 없이 즉시 로딩됨
        Long teamId = foundMember.getTeamId();
        assertThat(teamId).isNotNull();

        System.out.println("Member ID: " + foundMember.getMemberId());
        System.out.println("Member Name: " + foundMember.getUsername());
        System.out.println("Team ID: " + teamId + " (즉시 로딩)");

        // Team 객체 정보는 별도 쿼리로만 조회 가능
        System.out.println("=== Team 조회를 위한 별도 쿼리 ===");
        IdMappingTeam team2 = em.find(IdMappingTeam.class, teamId);
        assertThat(team2.getName()).isEqualTo("마케팅팀");
    }

    /**
     * ID 매핑에서 수동 연관관계 관리 테스트
     * - 개발자가 직접 ID 값을 변경하여 연관관계 수정
     * - 객체 참조 없이 순수 ID 기반으로 관계 변경
     */
    @Test
    @DisplayName("ID 매핑 - 수동 연관관계 관리")
    void testManualRelationshipManagement() {
        // Given - 두 개의 Team 생성
        IdMappingTeam team1 = new IdMappingTeam("백엔드팀");
        IdMappingTeam team2 = new IdMappingTeam("프론트엔드팀");
        em.persist(team1);
        em.persist(team2);
        em.flush();

        // Member를 team1에 소속시킴
        IdMappingMember member = new IdMappingMember("최개발", team1.getTeamId());
        em.persist(member);
        em.flush();
        em.clear();

        // When - 팀 변경 (수동으로 ID 업데이트)
        IdMappingMember foundMember = em.find(IdMappingMember.class, member.getMemberId());
        System.out.println("=== 팀 변경 전 ===");
        System.out.println("현재 Team ID: " + foundMember.getTeamId());

        // Team 객체 참조 없이 직접 ID 변경
        foundMember.setTeamId(team2.getTeamId());
        em.flush(); // UPDATE 쿼리 실행 (외래키 값 변경)

        em.clear();

        // Then - 변경 결과 검증
        IdMappingMember updatedMember = em.find(IdMappingMember.class, member.getMemberId());
        IdMappingTeam newTeam = em.find(IdMappingTeam.class, updatedMember.getTeamId());

        assertThat(updatedMember.getTeamId()).isEqualTo(team2.getTeamId());
        assertThat(newTeam.getName()).isEqualTo("프론트엔드팀");

        System.out.println("=== 팀 변경 후 ===");
        System.out.println("변경된 Team ID: " + updatedMember.getTeamId());
        System.out.println("변경된 Team Name: " + newTeam.getName());
    }

    /**
     * 네이티브 SQL 조인 쿼리를 사용한 연관 데이터 조회 테스트
     * - ID 매핑에서 조인을 통한 효율적인 데이터 조회 방법
     * - 여러 테이블의 데이터를 한 번의 쿼리로 조회
     */
    @Test
    @DisplayName("ID 매핑 - 조인 쿼리로 연관 데이터 조회")
    void testJoinQuery() {
        // Given - 여러 Team과 Member 생성
        IdMappingTeam team1 = new IdMappingTeam("디자인팀");
        IdMappingTeam team2 = new IdMappingTeam("기획팀");
        em.persist(team1);
        em.persist(team2);

        // 디자인팀에 2명, 기획팀에 1명 소속
        IdMappingMember member1 = new IdMappingMember("이영희", team1.getTeamId());
        IdMappingMember member2 = new IdMappingMember("박민수", team1.getTeamId());
        IdMappingMember member3 = new IdMappingMember("정수진", team2.getTeamId());

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.flush();
        em.clear();

        // When - 네이티브 SQL 조인 쿼리로 특정 팀의 멤버들 조회
        System.out.println("=== 네이티브 SQL 조인 쿼리 ===");
        List<Object[]> results = em.createNativeQuery(
                        "SELECT m.member_id, m.username, t.team_id, t.name " +
                                "FROM member m " +
                                "JOIN team t ON m.team_id = t.team_id " +
                                "WHERE t.name = ?")
                .setParameter(1, "디자인팀")
                .getResultList();

        // Then - 결과 검증 및 출력
        assertThat(results).hasSize(2);
        for (Object[] result : results) {
            Long memberId = ((Number) result[0]).longValue();
            String memberName = (String) result[1];
            Long teamId = ((Number) result[2]).longValue();
            String teamName = (String) result[3];

            System.out.println(String.format("Member ID: %d, Name: %s, Team ID: %d, Team: %s",
                    memberId, memberName, teamId, teamName));
        }
    }

    /**
     * JPQL을 사용한 연관 데이터 조회 테스트
     * - ID 기반으로 특정 조건의 데이터 조회
     * - 각 Member의 Team 정보는 별도 조회 필요함을 확인
     */
    @Test
    @DisplayName("ID 매핑 - JPQL로 연관 데이터 조회")
    void testJPQLQuery() {
        // Given - 하나의 Team에 여러 Member 소속
        IdMappingTeam team = new IdMappingTeam("테스트팀");
        em.persist(team);
        em.flush();

        // 3명의 멤버를 동일한 팀에 소속시킴
        for (int i = 1; i <= 3; i++) {
            IdMappingMember member = new IdMappingMember("회원" + i, team.getTeamId());
            em.persist(member);
        }

        em.flush();
        em.clear();

        // When - JPQL로 특정 팀의 모든 멤버 조회
        System.out.println("=== JPQL로 팀의 멤버들 조회 ===");
        List<IdMappingMember> members = em.createQuery(
                        "SELECT m FROM IdMappingMember m WHERE m.teamId = :teamId", IdMappingMember.class)
                .setParameter("teamId", team.getTeamId())
                .getResultList();

        // Then - 결과 검증
        assertThat(members).hasSize(3);

        // 각 멤버의 팀 정보를 별도로 조회해야 함 (N+1 문제 대신 수동 제어)
        for (IdMappingMember member : members) {
            IdMappingTeam memberTeam = em.find(IdMappingTeam.class, member.getTeamId());
            System.out.println(String.format("Member: %s, Team: %s",
                    member.getUsername(), memberTeam.getName()));
        }
    }

    /**
     * 데이터 일관성 검증 테스트
     * - 존재하지 않는 Team ID 설정 시 외래키 제약조건 확인
     * - ID 기반 매핑에서의 데이터 무결성 문제점 검증
     */
    @Test
    @DisplayName("ID 매핑 - 데이터 일관성 검증")
    void testDataConsistency() {
        // Given - 정상적인 Team과 Member 생성
        IdMappingTeam team = new IdMappingTeam("일관성팀");
        em.persist(team);
        em.flush();

        IdMappingMember member = new IdMappingMember("테스터", team.getTeamId());
        em.persist(member);
        em.flush();
        em.clear();

        // When - 존재하지 않는 Team ID로 변경 시도
        IdMappingMember foundMember = em.find(IdMappingMember.class, member.getMemberId());
        foundMember.setTeamId(999L); // 존재하지 않는 Team ID

        // 외래키 제약조건 위반으로 예외 발생 예상
        assertThrows(Exception.class, () -> {
            em.flush(); // ConstraintViolationException 또는 DataIntegrityViolationException
        });
        em.clear();

        // Then - 데이터 일관성 문제 확인
        IdMappingMember inconsistentMember = em.find(IdMappingMember.class, member.getMemberId());
        assertThat(inconsistentMember.getTeamId()).isEqualTo(team.getTeamId()); // 원래 값 유지

        // 존재하지 않는 Team 조회 시 null 반환
        IdMappingTeam nonExistentTeam = em.find(IdMappingTeam.class, 999L);
        assertThat(nonExistentTeam).isNull();

        System.out.println("=== 데이터 일관성 확인 ===");
        System.out.println("Member Team ID: " + inconsistentMember.getTeamId());
        System.out.println("존재하지 않는 Team 조회 결과: " + (nonExistentTeam != null));
    }

    /**
     * ID 매핑의 성능 특성 확인 테스트
     * - N+1 문제가 발생하지 않음을 검증
     * - 필요한 경우에만 연관 데이터 조회하는 특성 확인
     */
    @Test
    @DisplayName("ID 매핑 - 성능 특성 확인")
    void testPerformanceCharacteristics() {
        // Given - 하나의 Team에 여러 Member 생성
        IdMappingTeam team = new IdMappingTeam("성능팀");
        em.persist(team);
        em.flush();

        List<IdMappingMember> members = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            IdMappingMember member = new IdMappingMember("회원" + i, team.getTeamId());
            em.persist(member);
            members.add(member);
        }

        em.flush();
        em.clear();

        // When - 모든 멤버 조회 (N+1 문제 발생하지 않음)
        System.out.println("=== ID 매핑: N+1 문제 없음 ===");
        List<IdMappingMember> allMembers = em.createQuery(
                        "SELECT m FROM IdMappingMember m", IdMappingMember.class)
                .getResultList();

        // Team 정보가 필요한 경우에만 별도 조회 (개발자가 제어)
        Long teamId = allMembers.get(0).getTeamId();
        IdMappingTeam teamInfo = em.find(IdMappingTeam.class, teamId);

        // Then - 성능 특성 확인
        assertThat(allMembers).hasSize(10);
        assertThat(teamInfo.getName()).isEqualTo("성능팀");

        System.out.println("조회된 멤버 수: " + allMembers.size());
        System.out.println("Team 조회 쿼리: 필요 시에만 실행");
    }

    /**
     * 서비스 레이어 활용 패턴 테스트
     * - ID 매핑에서 연관 데이터를 조합하는 서비스 메서드 패턴
     * - DTO를 활용한 데이터 전달 방식 검증
     */
    @Test
    @DisplayName("ID 매핑 - 서비스 레이어 활용")
    void testServiceLayerIntegration() {
        // Given - 테스트 데이터 준비
        IdMappingTeam team = new IdMappingTeam("서비스팀");
        em.persist(team);
        em.flush();

        IdMappingMember member = new IdMappingMember("서비스유저", team.getTeamId());
        em.persist(member);
        em.flush();
        em.clear();

        // When - 서비스 메서드를 통한 연관 데이터 조회
        MemberWithTeamInfo result = getMemberWithTeamInfo(member.getMemberId());

        // Then - 서비스 레이어 결과 검증
        assertThat(result.getMemberName()).isEqualTo("서비스유저");
        assertThat(result.getTeamName()).isEqualTo("서비스팀");

        System.out.println("=== 서비스 레이어 결과 ===");
        System.out.println("Member: " + result.getMemberName());
        System.out.println("Team: " + result.getTeamName());
    }

    /**
     * 서비스 메서드 시뮬레이션
     * - Member와 Team 데이터를 개별 조회 후 DTO로 조합
     * - ID 매핑에서 연관 데이터 처리하는 일반적인 패턴
     *
     * @param memberId 조회할 멤버의 ID
     * @return Member와 Team 정보를 포함한 DTO
     */
    private MemberWithTeamInfo getMemberWithTeamInfo(Long memberId) {
        // 1. Member 조회
        IdMappingMember member = em.find(IdMappingMember.class, memberId);
        // 2. Member의 teamId로 Team 조회
        IdMappingTeam team = em.find(IdMappingTeam.class, member.getTeamId());

        // 3. DTO로 조합하여 반환
        return new MemberWithTeamInfo(member.getUsername(), team.getName());
    }

    /**
     * Member와 Team 정보를 함께 전달하는 DTO 클래스
     * - ID 매핑에서 연관 데이터를 조합할 때 사용
     * - 객체 참조 없이 필요한 데이터만 포함
     */
    static class MemberWithTeamInfo {
        private String memberName;
        private String teamName;

        /**
         * DTO 생성자
         * @param memberName 멤버 이름
         * @param teamName 팀 이름
         */
        public MemberWithTeamInfo(String memberName, String teamName) {
            this.memberName = memberName;
            this.teamName = teamName;
        }

        public String getMemberName() { return memberName; }
        public String getTeamName() { return teamName; }
    }
}

