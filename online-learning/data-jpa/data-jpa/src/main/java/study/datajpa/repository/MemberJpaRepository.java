package study.datajpa.repository;

import java.util.List;
import java.util.Optional;
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

  public void delete(Member member) {
    em.remove(member);
  }

  public List<Member> findAll() {
    // JPQL
    return em.createQuery("select m from Member m", Member.class)
        .getResultList();
  }

  public Optional<Member> findById(Long id) {
    return Optional.ofNullable(em.find(Member.class, id));
  }

  public Long count() {
    return em.createQuery("select count(m) from Member m", Long.class)
        .getSingleResult();
  }

  public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
    return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
        .setParameter("username", username)
        .setParameter("age", age)
        .getResultList();
  }

  public List<Member> findByPage(int age, int offset, int limit) {
    return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
        .setParameter("age", age)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }

  public long totalCount(int age) {
    return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
        .setParameter("age", age)
        .getSingleResult();
  }

  public int bulkAgePlus(int age) {
    return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
        .setParameter("age", age)
        .executeUpdate();
  }
}
