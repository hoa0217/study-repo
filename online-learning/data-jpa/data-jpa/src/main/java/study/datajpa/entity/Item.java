package study.datajpa.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item implements Persistable<String> {

  @Id
  // @GeneratedValue
  private String id;

  @CreatedDate
  private LocalDateTime createdDate;

  public Item(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public boolean isNew() {
    return createdDate == null;
  }
}
