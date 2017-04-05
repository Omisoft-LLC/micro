package com.omisoft.server.common.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by dido on 16.03.17.
 */
public class DbModule extends AbstractModule {
  private final String packageName;
//  private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE
//      = new ThreadLocal<EntityManager>();

  public DbModule() {
    packageName = null;
  }

  public DbModule(String packageName) {
    this.packageName = packageName;
  }

  @Override
  protected void configure() {

  }

  @Provides
  @Singleton
  public SessionFactory provideEntityManagerFactory() {

    StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
        .configure("hibernate.cfg.xml")
        .build();
    MetadataSources ms = new MetadataSources(standardRegistry);
    if (packageName != null) {
      Reflections reflections = new Reflections(packageName);
      Set<Class<?>> classes = reflections.getTypesAnnotatedWith(javax.persistence.Entity.class);

      for (Class<?> clazz : classes) {
        ms.addAnnotatedClass(clazz);
      }
    }
    Metadata metadata = ms
        .getMetadataBuilder()
        .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
        .build();
    SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
        .build();
    return sessionFactory;
  }

  @Provides
  public Session providesHibernateSession(SessionFactory sessionFactory) {
    return sessionFactory.getCurrentSession();
  }

}