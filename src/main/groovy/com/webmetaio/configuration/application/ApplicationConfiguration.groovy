package com.webmetaio.configuration.application

import sun.net.httpserver.ServerConfig

interface ApplicationConfiguration extends ConfigurationMapProvider {

  ServerConfig getServerConfig()

  Properties getLog4jConfig()

}
