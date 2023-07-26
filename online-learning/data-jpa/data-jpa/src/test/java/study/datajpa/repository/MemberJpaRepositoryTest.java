package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  private MemberJpaRepository memberJpaRepository;

  @Test
  public void save() {
    Member member = new Member("memberA");
    Member savedMember = memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member); // 같은 트랜잭션 안에서 같은 엔티티는 같은 인스턴스임을 보장한다.
  }

  @Test
  public void basicCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    // 단건조회 검증
    Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
    Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberJpaRepository.findAll();
    assertThat(all).hasSize(2);

    // 카운트 검증
    Long count = memberJpaRepository.count();
    assertThat(count).isEqualTo(2);

    memberJpaRepository.delete(member1);
    memberJpaRepository.delete(member2);

    Long deletedCount = memberJpaRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  public void findByUsernameAndAgeGreaterThan() {
    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("AAA", 20);
    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

    assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    assertThat(result.get(0).getAge()).isEqualTo(20);
    assertThat(result).hasSize(1);
  }

  @Test
  public void paging(){
    // given
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 10));
    memberJpaRepository.save(new Member("member3", 10));
    memberJpaRepository.save(new Member("member4", 10));
    memberJpaRepository.save(new Member("member5", 10));
    memberJpaRepository.save(new Member("member6", 10));

    int age = 10;
    int offset = 1;
    int limit = 3;

    // when
    List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
    long totalCount = memberJpaRepository.totalCount(age);

    // then
    assertThat(members.size()).isEqualTo(limit);
    assertThat(totalCount).isEqualTo(6);
  }

  @Test
  public void bulkAgePlus(){
    // given
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 19));
    memberJpaRepository.save(new Member("member3", 20));
    memberJpaRepository.save(new Member("member4", 21));
    memberJpaRepository.save(new Member("member5", 40));

    int resultCount = memberJpaRepository.bulkAgePlus(20);

    assertThat(resultCount).isEqualTo(3);
  }
}
