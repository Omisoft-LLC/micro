package com.omisoft.micro.examples.entities;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_human_task")

public class HumanTask extends BaseMessage {

  @Enumerated(EnumType.STRING)
  private ActionEnum action;

  private String assignee;
  @OneToMany
  private Set<QuestionData> questionData;
}
