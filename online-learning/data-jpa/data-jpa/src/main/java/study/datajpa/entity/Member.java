package study.datajpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

  @Id
  @GeneratedValue
  private Long id;
  private String userName;

  // jpa 표준스펙으로 entity를 만들 땐 default 생성자가 필요.
  // 또한 protected까진 열어놔야함
  // jpa 프록싱할 때 private으로 막아놓으면 제대로 동작하지 못함.
  protected Member() {
  }

  public Member(String userName) {
    this.userName = userName;
  }

  public void changeUsername(String userName) {
    this.userName = userName;
  }
}
