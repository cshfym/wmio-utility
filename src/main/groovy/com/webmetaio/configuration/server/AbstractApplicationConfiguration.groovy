package com.webmetaio.configuration.server

import com.typesafe.config.Config

abstract class AbstractApplicationConfiguration implements ApplicationConfiguration {

  Config config

  String configBaseName

  AbstractApplicationConfiguration(String configBaseName, Config config) {
    this.config = config
    this.configBaseName = configBaseName
  }

  ServerConfig getServerConfig() {
    new ServerConfig(config.getConfig("${configBaseName}.server"))
  }

}