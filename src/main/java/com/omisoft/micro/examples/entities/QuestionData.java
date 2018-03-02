package com.omisoft.micro.examples.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_question_data")
public class QuestionData extends BaseEntity {

  private String name;
  private String value;
  @Enumerated(EnumType.STRING)
  private TypeEnum type;


}
