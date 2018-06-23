package eu.omisoft.micro.examples.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_human_task")

public class HumanTask extends BaseMessage {

  @Enumerated(EnumType.STRING)
  private ActionEnum action;

  private String assignee;
  @OneToMany
  private Set<QuestionData> questionData;
}
