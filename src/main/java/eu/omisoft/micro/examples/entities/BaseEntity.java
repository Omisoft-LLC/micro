package eu.omisoft.micro.examples.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

  @Id
  protected Long id;
  @UpdateTimestamp
  @Column(
      name = "last_modified"
  )
  private Date lastModified;
  @CreationTimestamp
  @Column(
      name = "created_on"
  )
  private Date createdOn;

  @PrePersist
  protected void onCreate() {
  }

  @PreUpdate
  protected void onUpdate() {
  }
}
