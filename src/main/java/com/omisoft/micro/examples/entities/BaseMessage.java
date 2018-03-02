package com.omisoft.micro.examples.entities;

import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseMessage extends BaseEntity {

  private String taskId;
  private String user;
}
