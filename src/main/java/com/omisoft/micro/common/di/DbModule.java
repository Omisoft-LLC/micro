package com.omisoft.micro.common.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 16.03.17.
 */
@Slf4j
public class DbModule extends AbstractModule {
  public static  EntityManagerFactory emf;

  private final String persistenceUnitName;
  private final Properties props;


  public DbModule() {
    persistenceUnitName = null;
    props = null;
  }

  public DbModule(String persistenceUnitName, Properties props) {
    this.persistenceUnitName = persistenceUnitName;
    this.props = props;
//    requestStaticInjection(BaseEntity.class);
  }

  @Override
  protected void configure() {

  }

  @Provides
  @Singleton
  public EntityManagerFactory provideEntityManagerFactory() {
    EntityManagerFactory emf;
    if (props == null) {
      emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    } else {
      emf = Persistence.createEntityManagerFactory(persistenceUnitName, props);

    }
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
    DbModule.emf = emf;
    return emf;
  }

  @Provides
  public EntityManager providesEntityManager(EntityManagerFactory sessionFactory) {
    return sessionFactory.createEntityManager();
  }

}