package com.webmetaio.configuration.application

import com.webmetaio.configuration.file.ExternalConfigDefaultsProvider
import com.webmetaio.configuration.file.ExternalFileConfig
import sun.net.httpserver.ServerConfig


abstract class AbstractApplicationConfiguration extends ExternalFileConfig implements ApplicationConfiguration {

  AbstractApplicationConfiguration(String configBaseName, ExternalConfigDefaultsProvider defaultsProvider) {
    super(configBaseName, defaultsProvider)
  }

  ServerConfig getServerConfig() {
    def standalone = config.taulia?."${configBaseName}"?.standalone
    if (!standalone || !(standalone instanceof Map)) {
      throw new IllegalArgumentException("Configuration doesn't provide server config at [taulia.${configBaseName}.standalone]")
    }
    new ServerConfig(standalone)
  }

  @Override
  Properties getLog4jConfig() {
    def props = config.taulia?.log4j
    if (!props || !(props instanceof ConfigObject)) {
      throw new IllegalArgumentException("Configuration doesn't provide log4j config at [taulia.log4j]")
    }
    props.toProperties('log4j')
  }

  @Override
  Map<String, String> getConfigurationMap() {
    config.flatten()
  }

}
