package study.datajpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

  List<Member> findByUsername(String username);

  // 파라미터가 2개가 넘어가면 메서드가 너무 길어진다. 이때는 직접 짜도록하자.
  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

  @Query("select m from Member m where m.username = :username and m.age = :age")
  List<Member> findUser(@Param("username") String username, @Param("age") int age);
}
