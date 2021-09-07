package com.tieto.pcm.gsso.srv.web;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@ComponentScan
public class Application extends SpringBootServletInitializer {

  /** The Constant PROFILE_WAS. */
  public static final String PROFILE_WAS = "WAS";


  public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class)
        .properties(createProperties())
        .run(args);
  }

  /**
   * Configure.
   *
   * @param builder the builder
   * @return the spring application builder
   */
  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
    return builder.properties(createProperties());
  }

  /**
   * Creates the spring application builder.
   *
   * @return the spring application builder
   */
  @Override
  protected SpringApplicationBuilder createSpringApplicationBuilder() {
    final SpringApplicationBuilder applicationBuilder = super.createSpringApplicationBuilder();
    if ( null != System.getProperty("was.install.root") && !"".equals(System.getProperty("was.install.root"))) {
      applicationBuilder.profiles(PROFILE_WAS);
    }
    return applicationBuilder;
  }

  /**
   * Creates the properties.
   *
   * @return the properties
   */
  static Properties createProperties() {
    final Properties properties = new Properties();
    properties.put(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY, "classpath:gsso/");
    return properties;

  }
}
