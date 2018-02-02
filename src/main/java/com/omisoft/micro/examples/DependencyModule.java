package com.omisoft.micro.examples;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

/**
 * Created by dido on 06.04.17.
 */
@Slf4j
public class DependencyModule extends AbstractModule {

  /**
   * Bind your services here
   */
  @Override
  protected void configure() {

//    bind(FirebaseApp.class).asEagerSingleton();
  }

  @Provides
  Validator providesValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }

  @Provides
  ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    Condition<UUID, UUID> skipIds = new Condition<UUID, UUID>() {
      public boolean applies(MappingContext<UUID, UUID> context) {
        return !context.getMapping().getLastDestinationProperty().getName().equals("id");
      }
    };
    Condition<Object, Object> lazyLoadingCondition = new Condition<Object, Object>() {
      public boolean applies(MappingContext<Object, Object> context) {
        return !(context.getSource() instanceof PersistentCollection);
      }
    };

    modelMapper.getConfiguration().setPropertyCondition(skipIds)
        .setPropertyCondition(lazyLoadingCondition);
    return modelMapper;
  }

  @Provides
  @com.google.inject.Singleton
  public Executor createThreadPoolExecutor() {
    BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>();
    return new ThreadPoolExecutor(20, 100, 2, TimeUnit.MINUTES,
        queue);
  }

  @Provides
  @Singleton
  public Configuration freeMarkerProvider() {
    try {
      Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
      ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/ftl");
      conf.setTemplateLoader(ctl);
      conf.setDefaultEncoding("UTF-8");
      conf.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      return conf;
    } catch (Exception e) {
      log.error("ERROR WITH FREEMARKER:", e);
      return null;
    }

  }


}
