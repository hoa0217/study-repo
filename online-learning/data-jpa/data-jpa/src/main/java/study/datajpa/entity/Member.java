package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;
  private String username;
  private int age;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  public Member(String username) {
    this.username = username;
  }

  public Member(String username, int age, Team team) {
    this.username = username;
    this.age = age;
    if (team != null) {
      this.team = team;
    }
  }

  public Member(String username, int age) {
    this.username = username;
    this.age = age;
  }

  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }

  public void changeName(String username) {
    this.username = username;
  }
}
