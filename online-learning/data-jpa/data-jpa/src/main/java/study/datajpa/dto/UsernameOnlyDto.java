package study.datajpa.dto;

public class UsernameOnlyDto {

  private final String username;

  public UsernameOnlyDto(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
