package com.omisoft.server.common.dialect;

import java.sql.Types;
import org.hibernate.dialect.PostgreSQL95Dialect;

/**
 * Created by dido on 18.03.17.
 */

public class CustomPostgreSQL95Dialect extends PostgreSQL95Dialect {

  public CustomPostgreSQL95Dialect() {
    registerHibernateType(Types.OTHER, "pg-uuid");
  }
}

