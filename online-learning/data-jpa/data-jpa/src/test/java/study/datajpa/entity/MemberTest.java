package study.datajpa.entity;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    em.flush(); // 강제로 쿼리 날리기
    em.clear(); // 강제로 영속성 컨텍스트 날리기

    List<Member> members = em.createQuery("select m  from Member m", Member.class)
        .getResultList();

    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("-> member.team = " + member.getTeam());
    }
  }

  @Test
  public void JPAEventBaseEntity() throws InterruptedException {
    // given
    Member member1 = new Member("member1");
    em.persist(member1);

    Thread.sleep(100);
    member1.changeName("member2");

    em.flush(); // @PreUpdate
    em.clear();

    // when
    Member findMember = em.find(Member.class, member1.getId());

    // then
    System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
    System.out.println("findMember.lastModifiedDate = " + findMember.getLastModifiedDate());
    System.out.println("findMember.createdBy = " + findMember.getCreatedBy());
    System.out.println("findMember.lastModifiedBy = " + findMember.getLastModifiedBy());
  }
}
