package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

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
}
