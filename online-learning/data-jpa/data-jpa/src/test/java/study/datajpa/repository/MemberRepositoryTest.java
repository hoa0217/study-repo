package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Test
  public void save() {
    Member member = new Member("memberA");
    Member savedMember = memberRepository.save(member);

    // 인터페이스 밖에 없는데 왜 동작할까?
    Member findMember = memberRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
    assertThat(findMember).isEqualTo(member);
  }
}
