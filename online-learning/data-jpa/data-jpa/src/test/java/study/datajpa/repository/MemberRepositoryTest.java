package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TeamRepository teamRepository;

  @PersistenceContext
  EntityManager em;

  @Autowired
  private MemberQueryRepository memberQueryRepository;

  @Test
  public void save() {
    System.out.println("memberRepository = " + memberRepository
        .getClass()); // memberRepository = class com.sun.proxy.$Proxy122
    Member member = new Member("memberA");
    Member savedMember = memberRepository.save(member);

    // 인터페이스 밖에 없는데 왜 동작할까?
    Member findMember = memberRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }

  @Test
  public void basicCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberRepository.save(member1);
    memberRepository.save(member2);

    // 단건조회 검증
    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberRepository.findAll();
    assertThat(all).hasSize(2);

    // 카운트 검증
    Long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    memberRepository.delete(member1);
    memberRepository.delete(member2);

    Long deletedCount = memberRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  public void findByUsernameAndAgeGreaterThan() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

    assertThat(result.get(0).getUsername()).isEqualTo("BBB");
    assertThat(result.get(0).getAge()).isEqualTo(20);
    assertThat(result).hasSize(1);
  }

  @Test
  public void findUser() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> result = memberRepository.findUser("AAA", 10);
    assertThat(result.get(0)).isEqualTo(member1);
  }

  @Test
  public void findUsernameList() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<String> usernameList = memberRepository.findUsernameList();
    for (String s : usernameList) {
      System.out.println("s = " + s);
    }
  }

  @Test
  public void findMemberDto() {
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member member = new Member("AAA", 10, team);
    memberRepository.save(member);

    List<MemberDto> memberDto = memberRepository.findMemberDto();
    for (MemberDto dto : memberDto) {
      System.out.println("dto = " + dto);
    }
  }

  @Test
  public void findByNames() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
    for (Member member : byNames) {
      System.out.println("member = " + member);
    }
  }

  @Test
  public void returnType() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("AAA", 20);
    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> aaa = memberRepository.findListByUsername("AAA");
    assertThat(aaa).hasSize(2);

    assertThatThrownBy(() -> memberRepository.findMemberByUsername("AAA"))
        .isInstanceOf(IncorrectResultSizeDataAccessException.class);
    assertThatThrownBy(() -> memberRepository.findOptionalByUsername("AAA"))
        .isInstanceOf(IncorrectResultSizeDataAccessException.class);
  }

  @Test
  public void paging() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

    // when
    Page<Member> page = memberRepository.findPageByAge(age, pageRequest);

    // then
    List<Member> pageContent = page.getContent();
    assertThat(pageContent.size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(6);
    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  public void slicing() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

    // when
    Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

    // then
    List<Member> sliceContent = slice.getContent();
    assertThat(sliceContent.size()).isEqualTo(3);
    // assertThat(slice.getTotalElements()).isEqualTo(6);
    assertThat(slice.getNumber()).isEqualTo(0);
    // assertThat(slice.getTotalPages()).isEqualTo(2);
    assertThat(slice.isFirst()).isTrue();
    assertThat(slice.hasNext()).isTrue();
  }

  @Test
  public void listing() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

    // when
    List<Member> members = memberRepository.findListByAge(age, pageRequest);

    // then
    assertThat(members).hasSize(3);
  }

  @Test
  public void findPageJoinByAge() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
    Page<Member> page = memberRepository.findPageJoinByAge(age, pageRequest);
  }

  @Test
  public void findPageJoinCountByAge() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
    Page<Member> page = memberRepository.findPageJoinCountByAge(age, pageRequest);

    // 페이지 내부 엔티티 dto로 변환
    Page<MemberDto> dtoPage = page
        .map(m -> new MemberDto(m.getId(), m.getUsername(), null));
  }

  @Test
  public void findPageFetchJoinCountByAge() {
    // given
    Team team = new Team("teamA");
    teamRepository.save(team);
    memberRepository.save(new Member("member1", 10, team));
    memberRepository.save(new Member("member2", 10, team));
    memberRepository.save(new Member("member3", 10, team));
    memberRepository.save(new Member("member4", 10, team));
    memberRepository.save(new Member("member5", 10, team));
    memberRepository.save(new Member("member6", 10, team));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
    Page<Member> page = memberRepository.findPageFetchJoinCountByAge(age, pageRequest);

    // then
    List<Member> pageContent = page.getContent();
    assertThat(pageContent.size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(6);
    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  public void bulkAgePlus() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));

    // 벌크연산은 영속성컨텍스트를 무시하고 바로 db에 반영한다. (mybatis를 함께 쓸 때도 마찬가지)
    // 따라서 벌크연산 이후엔 영속성 컨텍스트를 날려야한다.
    // 같은 트랜잭션에서 로직이 일어나면 큰일난다!
    int resultCount = memberRepository.bulkAgePlus(20);
    // em.clear(); 또는 @Modifying(clearAutomatically = true)옵션 추가 필요

    assertThat(resultCount).isEqualTo(3);
    Member member5 = memberRepository.findMemberByUsername("member5");
    assertThat(member5.getAge()).isEqualTo(41);
  }

  @Test
  public void findMemberLazy() {
    // given
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);
    Member memberA = new Member("memberA", 10, teamA);
    Member memberB = new Member("memberB", 10, teamB);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    em.flush(); // db 반영
    em.clear(); // 영속성 컨텍스트 날리기

    // when N(2) + 1
    // select member 1
    List<Member> members = memberRepository.findAll();
    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("member.teamClass = " + member.getTeam().getClass());
      System.out.println("member.team = " + member.getTeam());
    }
  }

  @Test
  public void findMemberFetchJoin() {
    // given
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);
    Member memberA = new Member("memberA", 10, teamA);
    Member memberB = new Member("memberB", 10, teamB);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    em.flush(); // db 반영
    em.clear(); // 영속성 컨텍스트 날리기

    List<Member> members = memberRepository.findMemberFetchJoin();
    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("member.teamClass = " + member.getTeam().getClass());
      System.out.println("member.team = " + member.getTeam());
    }
  }

  @Test
  public void findEntityGraphByUsername() {
    // given
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);
    Member memberA = new Member("memberA", 10, teamA);
    Member memberB = new Member("memberA", 10, teamB);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    em.flush(); // db 반영
    em.clear(); // 영속성 컨텍스트 날리기

    List<Member> members = memberRepository.findEntityGraphByUsername("memberA");
    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("member.teamClass = " + member.getTeam().getClass());
      System.out.println("member.team = " + member.getTeam());
    }
  }

  @Test
  public void queryHint() {
    // given
    Member member1 = memberRepository.save(new Member("member1", 10));
    em.flush();
    em.clear();

    // when
    // 변경감지 -> 데이터를 2개를 가지고 있어야한다.
    // 원본을 알고있어야 더티체킹이 일어날 수 있음. 결국 비용 즉, 메모리를 많이 먹는다.
    // 만약 조회용으로만 쓸거야! 한다면? 쿼리힌트를 사용해보자
    // Member findMember = memberRepository.findById(member1.getId()).get();
    Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
    findMember.changeName("member2");

    em.flush();
  }

  @Test
  public void lock() {
    // given
    Member member1 = memberRepository.save(new Member("member1", 10));
    em.flush();
    em.clear();

    // when
    List<Member> lockByUsername = memberRepository.findLockByUsername(member1.getUsername());

    /**
     *     select
     *         member0_.member_id as member_i1_0_,
     *         member0_.age as age2_0_,
     *         member0_.team_id as team_id4_0_,
     *         member0_.username as username3_0_
     *     from
     *         member member0_
     *     where
     *         member0_.username=? for update (jpa가 제공하는 lock 사용, 책으로 공부하셈)
     */

    // 실시간 트래픽이 많은 서비스에서 lock은 웬만하면 걸지마라. 다른 방법으로 해결하는 걸 권장함.
  }

  @Test
  public void callCustom() {
    List<Member> memberCustom = memberRepository.findMemberCustom(); // custom repository
    List<Member> allMembers = memberQueryRepository.findAllMembers(); // 클래스가 쪼개진 repository
  }

  @Test
  public void projections() {
    // given
    Team teamA = new Team("teamA");
    teamRepository.save(teamA);

    Member memberA = new Member("memberA", 10, teamA);
    Member memberB = new Member("memberB", 10, teamA);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    em.flush(); // db 반영
    em.clear(); // 영속성 컨텍스트 날리기

    // when
    List<NestedClosedProjections> result = memberRepository
        .findProjectionsByUsername("memberA", NestedClosedProjections.class);

    for (NestedClosedProjections userNameOnly : result) {
        System.out.println("userNameOnly.getUsername = " + userNameOnly.getUsername());
      System.out.println("userNameOnly.getTeam = " + userNameOnly.getTeam());
    }
  }
}
