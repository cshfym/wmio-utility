package com.webmetaio.configuration.application

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer

public class ApplicationConfigurationBasedPropertyPlaceholderConfigurer<AppConfigType extends ConfigurationMapProvider>
  extends PropertyPlaceholderConfigurer implements ConfigurationMapProvider {

  AppConfigType applicationConfiguration

  private Map configMap

  public void setApplicationConfiguration(AppConfigType appConfig) {
    applicationConfiguration = appConfig
    configMap = applicationConfiguration.configurationMap
  }

  @Override
  protected String resolveSystemProperty(String key) {
    configMap.get(key)
  }

  @Override
  Map<String, String> getConfigurationMap() {
    configMap
  }

}
