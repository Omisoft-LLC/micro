package com.omisoft.micro.examples.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
