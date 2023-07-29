package study.datajpa.repository;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@RequiredArgsConstructor
@Repository
public class MemberQueryRepository {

  private final EntityManager em;

  public List<Member> findAllMembers() {
    return em.createQuery("select m from Member m")
        .getResultList();
  }
}
