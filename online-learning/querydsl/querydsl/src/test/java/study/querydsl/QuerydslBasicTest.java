package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
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

  /**
   * 회원 정렬 순서 1. 회원 나이 내림차순(desc) 2. 회원 이름 올림차순(asc) 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
   */
  @Test
  public void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();

    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);
    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
  }

  @Test
  public void paging1() {
    List<Member> result = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1) //0부터 시작(zero index)
        .limit(2) //최대 2건 조회
        .fetch();

    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void paging2() {
    QueryResults<Member> queryResults = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1) //0부터 시작(zero index)
        .limit(2) //최대 2건 조회
        .fetchResults();

    // 조회쿼리는 복잡한데 count쿼리는 단순한 경우 성능상 분리해서 작성하자.
    assertThat(queryResults.getTotal()).isEqualTo(4);
    assertThat(queryResults.getLimit()).isEqualTo(2);
    assertThat(queryResults.getOffset()).isEqualTo(1);
    assertThat(queryResults.getResults().size()).isEqualTo(2);
  }

  @Test
  public void aggregation() {
    List<Tuple> result = queryFactory
        .select(member.count(),
            member.age.sum(),
            member.age.avg(),
            member.age.max(),
            member.age.min())
        .from(member)
        .fetch();

    Tuple tuple = result.get(0);
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
  }

  /**
   * 팀의 이름과 팀의 평균 연령을 구하라
   */
  @Test
  public void groupBy() {
    List<Tuple> result = queryFactory
        .select(team.name, member.age.avg())
        .from(member)
        .join(member.team, team)
        .groupBy(team.name)
        .fetch();

    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15); // 10 + 20 / 2
    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35); // 30 + 40 / 2
  }

  /**
   * 팀A에 소속된 모든 회원
   */
  @Test
  public void join() {

    List<Member> result = queryFactory
        .selectFrom(member)
        .leftJoin(member.team, team)
        .where(team.name.eq("teamA"))
        .fetch();

    assertThat(result)
        .extracting("username")
        .containsExactly("member1", "member2");
  }

  /**
   * 세타 조인(연관관계가 없는 필드로 조인) 회원의 이름이 팀 이름과 같은 회원 조회
   **/
  @Test
  public void theta_join() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    em.persist(new Member("teamC"));

    List<Member> result = queryFactory
        .select(member)
        .from(member, team)
        .where(member.username.eq(team.name))
        .fetch();
    assertThat(result)
        .extracting("username")
        .containsExactly("teamA", "teamB");
  }

  /**
   * 회원과 팀을 조인하면서, 팀이름이 teamA인 팀만 조인, 회원은 모두 조회 JPQL : select m, t from Member m left join m.team t
   * on t.name = 'teamA'
   */
  @Test
  public void join_on_filterling() {
    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(member.team, team).on(team.name.eq("teamA"))
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  /**
   * 연관관계 없는 엔티티 외부조인 회원의 이름이 팀 이름과 같은 대상 외부 조인
   */
  @Test
  public void join_on_no_relation() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    em.persist(new Member("teamC"));

    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .join(team).on(member.username.eq(team.name))
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  @PersistenceUnit
  EntityManagerFactory emf;

  @Test
  public void fetchJoinNo(){
    em.flush();
    em.clear();

    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isFalse();
  }

  @Test
  public void fetchJoinUse(){
    em.flush();
    em.clear();

    Member findMember = queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin()
        .where(member.username.eq("member1"))
        .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 적용").isTrue();
  }
}