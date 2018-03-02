package com.omisoft.micro.examples.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_comment")
public class Comment extends BaseMessage {

  private String comment;
}
