package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

  List<Member> findByUsername(String username);

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

  @Query(value = "select m from Member m left join m.team t where m.age = :age"
      , countQuery = "select count(m) from Member m where m.age = :age")
  Page<Member> findPageJoinCountByAge(int age, Pageable pageable);
}
