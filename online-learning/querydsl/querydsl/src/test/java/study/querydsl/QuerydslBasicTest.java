package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static study.querydsl.entity.QMember.member;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @Autowired
  EntityManager em;
  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    queryFactory = new JPAQueryFactory(em);

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
  }

  @Test
  public void startJPQL() {
    String sqlString = "select m from Member m where m.username = :username";
    Member findMember = em.createQuery(sqlString, Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void startQuerydsl() {

    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void search() {
    Member member = queryFactory.selectFrom(QMember.member)
        .where(QMember.member.username.eq("member1")
            .and(QMember.member.age.eq(10)))
        .fetchOne();

    assertThat(member.getUsername()).isEqualTo("member1");
  }

  @Test
  public void searchAndParam() {
    Member member = queryFactory.selectFrom(QMember.member)
        .where(QMember.member.username.eq("member1"), (QMember.member.age.between(10, 30)))
        .fetchOne();

    assertThat(member.getUsername()).isEqualTo("member1");
  }

  @Test
  public void resultFetch() {
    List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();

    // 결과가 2개 이상이기 때문에 exception이 터짐.
    assertThatThrownBy(() -> queryFactory
        .selectFrom(member)
        .fetchOne()).isInstanceOf(NonUniqueResultException.class);

    Member fetchFirst = queryFactory
        .selectFrom(member)
        .fetchFirst();

    QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();

    // 페이징을 위한 total count를 가져와야하기 때문에 쿼리가 2번 나간다.
    // 복잡하고 성능이 중요한 페이징 쿼리에서는 fetchResults를 쓰면 안되고, count 쿼리를 직접 날려야한다.
    long total = results.getTotal();
    List<Member> members = results.getResults();

    long count = queryFactory
        .selectFrom(member)
        .fetchCount();
  }
}
