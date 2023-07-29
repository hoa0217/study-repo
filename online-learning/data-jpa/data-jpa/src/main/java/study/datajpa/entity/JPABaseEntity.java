package study.datajpa.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;

@MappedSuperclass
@Getter
public class JPABaseEntity {

  @Column(updatable = false)
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    createdDate = now;
    updatedDate = now;
  }

  @PreUpdate
  public void preUpdate() {
    LocalDateTime now = LocalDateTime.now();
    updatedDate = now;
  }
}
