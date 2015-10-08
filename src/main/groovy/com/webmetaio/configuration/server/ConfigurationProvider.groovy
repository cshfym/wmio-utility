package com.webmetaio.configuration.server

import com.typesafe.config.Config

interface ConfigurationProvider {

  Config getConfig()

}
