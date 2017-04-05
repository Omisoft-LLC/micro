package com.omisoft.server.common.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import static com.omisoft.server.common.entities.BaseEntity.IS_ACTIVE_FILTER_NAME;


/**
 * Base entity Created by nslavov on 3/17/16.
 */
@MappedSuperclass
@Getter
@Setter
@FilterDef(name = IS_ACTIVE_FILTER_NAME,
    parameters = @ParamDef(name = "isActive", type = "boolean"))
@Filters({@Filter(name = IS_ACTIVE_FILTER_NAME, condition = "is_active=:isActive")})
public abstract class BaseEntity implements Serializable {
  public static final String IS_ACTIVE_FILTER_NAME = "isActiveFilter";

  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Type(type = "pg-uuid")
  @Id
  protected UUID id;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @UpdateTimestamp
  @Column(name = "last_modified")
  private Date lastModified;
  @CreationTimestamp
  @Column(name = "created_on")
  private Date createdOn;

  @Version
  @GeneratedValue
  private Long version;

  @Column(name = "is_active", columnDefinition = "BOOLEAN default true")
  private Boolean isActive = Boolean.TRUE;


  public BaseEntity() {
  }

  public BaseEntity(String id) {
    this.id = UUID.fromString(id);
    ;
  }

  @PrePersist
  protected void onCreate() {

  }

  @PreUpdate
  protected void onUpdate() {

  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || !(o instanceof BaseEntity)) {

      return false;
    }

    BaseEntity other = (BaseEntity) o;

    // if the id is missing, return false
    if (id == null)
      return false;

    // equivalence by id
    return id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    if (id != null) {
      return id.hashCode();
    } else {
      return 42;
    }
  }

  @Override
  public String toString() {
    return this.getClass().getName() + "[id=" + id + "]";
  }

}
