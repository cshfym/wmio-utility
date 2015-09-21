package com.webmetaio.configuration.application

import com.webmetaio.configuration.file.AbstractExternalConfig


public abstract class AbstractEnvironmentAwareExternalConfig extends AbstractExternalConfig {

  protected AbstractEnvironmentAwareExternalConfig(String configBaseName) {
    super(configBaseName)
  }

  String getHomeFolderOfCurrentUser() {
    System.getProperty("user.home")
  }

  String getConfigLocationInHomeFolder() {
    homeFolderOfCurrentUser + '/' + configBaseName + 'Config.groovy'
  }

  String getConfigLocationEnvironmentVariableName() {
    String bashSafeEnvAppName = configBaseName.toUpperCase(Locale.ENGLISH).replaceAll(/-/, '_')
    bashSafeEnvAppName.toUpperCase() + '_CONFIG_LOCATION'
  }

  String getConfigLocationFromEnvironmentVariable() {
    System.getenv(configLocationEnvironmentVariableName)
  }

  File getPrioritizedConfigFile() {

    File ret = null
    if (configLocationFromEnvironmentVariable && new File(configLocationFromEnvironmentVariable).exists()) {
      ret = new File(configLocationFromEnvironmentVariable)
    }
    if (!ret && new File(configLocationInHomeFolder).exists()) {
      ret = new File(configLocationInHomeFolder)
    }
    ret

  }

}
