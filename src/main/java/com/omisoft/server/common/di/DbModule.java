package com.omisoft.server.common.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by dido on 16.03.17.
 */
@Slf4j
public class DbModule extends AbstractModule {
  private final String persistenceUnitName;


  public DbModule() {
    persistenceUnitName = null;
  }

  public DbModule(String persistenceUnitName) {
    this.persistenceUnitName = persistenceUnitName;
  }

  @Override
  protected void configure() {

  }

  @Provides
  @Singleton
  public EntityManagerFactory provideEntityManagerFactory() {
    log.info("BEFORE PU CREATE");
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    log.info("AA CREATING EMF:" + persistenceUnitName);
    //    StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
//        .configure("hibernate.cfg.xml")
//        .build();
//    MetadataSources ms = new MetadataSources(standardRegistry);
//    if (packageName != null) {
//      Reflections reflections = new Reflections(packageName);
//      Set<Class<?>> classes = reflections.getTypesAnnotatedWith(javax.persistence.Entity.class);
//
//      for (Class<?> clazz : classes) {
//        ms.addAnnotatedClass(clazz);
//      }
//    }
//    Metadata metadata = ms
//        .getMetadataBuilder()
//        .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
//        .build();
//    SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
//        .build();
    return emf;
  }

  @Provides
  public EntityManager providesEntityManager(EntityManagerFactory sessionFactory) {
    return sessionFactory.createEntityManager();
  }

}