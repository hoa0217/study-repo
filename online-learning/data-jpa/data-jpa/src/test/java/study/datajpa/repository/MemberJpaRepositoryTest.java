package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional // 트랜잭션이 없으면 데이터를 변경할 수 없다.
@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  private MemberJpaRepository memberJpaRepository;

  @Test
  public void save() {
    Member member = new Member("memberA");
    Member savedMember = memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.find(savedMember.getId());

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member); // 같은 트랜잭션 안에서 같은 엔티티는 같은 인스턴스임을 보장한다.
  }
}
