package study.datajpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

  @PersistenceContext // 스프링컨테이너가 영속성 컨텍스트에 있는 EntityManager를 주입해준다.
  private EntityManager em;

  public Member save(Member member) {
    em.persist(member);
    return member;
  }

  public Member find(Long id) {
    return em.find(Member.class, id);
  }
}
