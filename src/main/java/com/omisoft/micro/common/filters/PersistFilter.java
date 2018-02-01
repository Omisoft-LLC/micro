package com.omisoft.micro.common.filters;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;

@Slf4j
@Singleton
public class PersistFilter implements Filter {

  @Inject
  private EntityManagerFactory emf;

  public void doFilter(ServletRequest request,
      ServletResponse response,
      FilterChain chain)
      throws IOException, ServletException {
    EntityManager em = null;
    try {
      log.debug("Starting a database transaction");
      em = emf.createEntityManager();
      em.getTransaction().begin();

      // Call the next filter (continue request processing)
      chain.doFilter(request, response);

      // Commit and cleanup
      log.debug("Committing the database transaction");
      em.getTransaction().commit();

    } catch (StaleObjectStateException staleEx) {
      log.error("This interceptor does not implement optimistic concurrency control!");
      log.error("Your application will not work until you add compensation actions!");
      // Rollback, close everything, possibly compensate for any permanent changes
      // during the conversation, and finally restart business conversation. Maybe
      // give the user of the application a chance to merge some of his work with
      // fresh data... what you do here depends on your applications design.
      throw staleEx;
    } catch (Throwable ex) {
      // Rollback only
      ex.printStackTrace();
      try {
        if (em.getTransaction().isActive()) {
          log.debug("Trying to rollback database transaction after exception");
          em.getTransaction().rollback();
        }
      } catch (Throwable rbEx) {
        log.error("Could not rollback transaction after exception!", rbEx);
      }

      // Let others handle it... maybe another interceptor for com.omisoft.micro.common.exceptions?
      throw new ServletException(ex);
    }
  }

  public void init(FilterConfig filterConfig) throws ServletException {
    log.debug("Initializing filter...");
    log.debug("Obtaining SessionFactory from static HibernateUtil singleton");
//        emf = HibernateUtil.getSessionFactory();
  }

  public void destroy() {
  }

}  