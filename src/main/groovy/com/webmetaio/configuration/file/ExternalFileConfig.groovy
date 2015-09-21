package com.webmetaio.configuration.file

import com.webmetaio.configuration.application.AbstractEnvironmentAwareExternalConfig
import groovy.util.logging.Slf4j

/**
 * Provides the capability to load a configuration from an external configuration file.
 * Loads the configuration either from the home folder of the user that is running the application. If an environment
 * variable
 */
@Slf4j
class ExternalFileConfig extends AbstractEnvironmentAwareExternalConfig {

  private ConfigObject config

  /**
   * Loads the configuration
   * This behavior can be overwritten by setting an environment variable called 'taulia.forceConfig' with value
   * 'default'
   * @return
   */
  public ExternalFileConfig(String configBaseName, ExternalConfigDefaultsProvider defaultsProvider) {

    super(configBaseName)

    File configFile = prioritizedConfigFile

    def forceDefault = false
    if (System.getProperty('taulia.forceConfig','') == 'default') {
      forceDefault = true
      log.info "Determined that the default configuration should be enforced based on environment variable"
      config = defaultsProvider.getDefaultConfigObject(this)
      return
    }

    if (!configFile) {
      log.debug "Loading defaults configuration as no configuration file was found"
      config = defaultsProvider.getDefaultConfigObject(this)
      return
    }

    log.info "Loading configuration file at [${configFile.absolutePath}]"
    config = new ConfigSlurper().parse(configFile.toURL())
    config = defaultsProvider.getDefaultConfigObject(this).merge(config)

  }

  synchronized ConfigObject getConfig() {
    return config
  }

}