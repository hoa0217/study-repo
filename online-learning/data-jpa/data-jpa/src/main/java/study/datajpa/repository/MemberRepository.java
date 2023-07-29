package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  // 파라미터가 2개가 넘어가면 메서드가 너무 길어진다. 이때는 직접 짜도록하자.
  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

  @Query("select m from Member m where m.username = :username and m.age = :age")
  List<Member> findUser(@Param("username") String username, @Param("age") int age);

  @Query("select m.username from Member m")
  List<String> findUsernameList();

  @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
  List<MemberDto> findMemberDto();

  @Query("select m from Member m where m.username in :names")
  List<Member> findByNames(@Param("names") Collection<String> names);

  List<Member> findListByUsername(String username); // 컬렉션

  Member findMemberByUsername(String username); // 단건

  Optional<Member> findOptionalByUsername(String username); // 단건 Optional

  Page<Member> findPageByAge(int age, Pageable pageable);

  Slice<Member> findSliceByAge(int age, Pageable pageable);

  List<Member> findListByAge(int age, Pageable pageable);

  @Query(value = "select m from Member m left join m.team t where m.age = :age")
  Page<Member> findPageJoinByAge(int age, Pageable pageable);

  @Query(value = "select m from Member m left join  m.team t where m.age = :age"
      , countQuery = "select count(m) from Member m where m.age = :age")
  Page<Member> findPageJoinCountByAge(int age, Pageable pageable);

  @Query(value = "select m from Member m join m.team t where m.age = :age")
  Page<Member> findPageFetchJoinCountByAge(int age, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
  int bulkAgePlus(int age);

  // 한번에 join해서 쿼리가 1번 나감 (지연 로딩X)
  @Query("select m from Member m left join fetch m.team")
  List<Member> findMemberFetchJoin();

  // 결과적으로 fetch join
  @EntityGraph(attributePaths = {"team"})
  List<Member> findAll();

  // 쿼리를 짰으나 fetch join을 추가하고 싶을 때
  @EntityGraph(attributePaths = {"team"})
  @Query("select m from Member m")
  List<Member> findEntityGraph();

  List<Member> findByUsername(String username);

  // 기본적으로 fetch join 시 left outer join이 나감
  // 너무 복잡한 쿼리일 땐 JPQL fetch join문을 짜자
  @EntityGraph(attributePaths = {"team"})
  @Query("select m from Member m where m.username = :username")
  List<Member> findEntityGraphByUsername(String username);

  // 대부분 복잡한 쿼리 자체가 잘못돼서 장애가 남,, 트래픽이 많지 않은데 넣어봤자 최적화 많이 안됨. 성능테스트 해보고 결정.
  // 그리고 느리면 대부분 redis를 깔지 이걸 먼저 하지는 않음. 처음부터 튜닝을 깐다? 좋지 않은 선택이다.
  @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
  Member findReadOnlyByUsername(String username);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Member> findLockByUsername(String username);
}
