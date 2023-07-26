package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
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
  public void paging(){
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
  public void slicing(){
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
  public void listing(){
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
  public void findPageJoinByAge(){
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
    Page<Member> pageJoinByAge = memberRepository.findPageJoinByAge(age, pageRequest);
  }

  @Test
  public void findPageJoinCountByAge(){
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
    Page<Member> pageJoinByAge = memberRepository.findPageJoinCountByAge(age, pageRequest);

    // 페이지 내부 엔티티 dto로 변환
    Page<MemberDto> dtoPage = pageJoinByAge
        .map(m -> new MemberDto(m.getId(), m.getUsername(), null));
  }
}
