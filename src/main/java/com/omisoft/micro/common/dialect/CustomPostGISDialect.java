package com.omisoft.server.common.dialect;

import java.sql.Types;

/**
 * Created by dido on 10.05.17.
 */
public class CustomPostGISDialect extends org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect {

  public CustomPostGISDialect() {
    registerHibernateType(Types.OTHER, "pg-uuid");
  }
}
