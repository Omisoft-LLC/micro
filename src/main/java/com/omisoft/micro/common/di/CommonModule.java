package com.omisoft.micro.common.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.omisoft.micro.common.auth.UserAuthority;

/**
 * Created by dido on 03.05.17.
 */
public class CommonModule extends AbstractModule {

  @Singleton
  @Provides
  public UserAuthority providesUserAuthority() {
    return UserAuthority.getInstance();
  }

  @Override
  protected void configure() {

  }
}
