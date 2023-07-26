package study.datajpa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class MemberDto {

  private Long id;
  private String username;
  private String teamName;

  public MemberDto(Long id, String username, String teamName) {
    this.id = id;
    this.username = username;
    this.teamName = teamName;
  }
}
