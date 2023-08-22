package study.querydsl.repository;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public MemberRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<MemberTeamDto> search(MemberSearchCondition condition) {

    return queryFactory
        .select(new QMemberTeamDto(member.id.as("memberId")
            , member.username
            , member.age
            , member.team.id.as("teamId")
            , member.team.name.as("teamName")))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername())
            , teamNameEq(condition.getTeamName())
            , ageGoe(condition.getAgeGoe())
            , ageLoe(condition.getAgeLoe())
        )
        .fetch();
  }

  private BooleanExpression usernameEq(String username) {
    return hasText(username) ? member.username.eq(username) : null;
  }

  private BooleanExpression teamNameEq(String teamName) {
    return hasText(teamName) ? member.team.name.eq(teamName) : null;
  }

  private BooleanExpression ageGoe(Integer ageGoe) {
    return ageGoe != null ? member.age.goe(ageGoe) : null;
  }

  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe != null ? member.age.loe(ageLoe) : null;
  }

  @Override
  public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
    QueryResults<MemberTeamDto> results = queryFactory
        .select(new QMemberTeamDto(member.id.as("memberId")
            , member.username
            , member.age
            , member.team.id.as("teamId")
            , member.team.name.as("teamName")))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername())
            , teamNameEq(condition.getTeamName())
            , ageGoe(condition.getAgeGoe())
            , ageLoe(condition.getAgeLoe())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetchResults();

    List<MemberTeamDto> content = results.getResults();
    long total = results.getTotal();

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
    List<MemberTeamDto> content = queryFactory
        .select(new QMemberTeamDto(member.id.as("memberId")
            , member.username
            , member.age
            , member.team.id.as("teamId")
            , member.team.name.as("teamName")))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername())
            , teamNameEq(condition.getTeamName())
            , ageGoe(condition.getAgeGoe())
            , ageLoe(condition.getAgeLoe())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Member> countQuery = queryFactory
        .select(member)
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername())
            , teamNameEq(condition.getTeamName())
            , ageGoe(condition.getAgeGoe())
            , ageLoe(condition.getAgeLoe())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

//    return new PageImpl<>(content, pageable, total);
    return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
    // count쿼리 생략 가능한 경우 생략해서 처리 : ount 쿼리가 생략 가능한 경우 생략해서 처리
    // 1. 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
    // 2. 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함, 더 정확히는 마지막 페이지 이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때)
  }
}
