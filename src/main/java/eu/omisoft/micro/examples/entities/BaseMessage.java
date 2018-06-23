package eu.omisoft.micro.examples.entities;

import lombok.Data;

import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class BaseMessage extends BaseEntity {

  private String taskId;
  private String user;
}
