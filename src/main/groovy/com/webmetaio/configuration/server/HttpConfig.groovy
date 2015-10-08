package com.webmetaio.configuration.server

import com.typesafe.config.Config
import groovy.util.logging.Slf4j

@Slf4j
class HttpConfig {

  boolean enabled

  String contextPath

  String listeningHost

  int port

  Integer corePoolSize

  Integer maxPoolSize

  Integer queueLimit

  public HttpConfig() {
  }

  public HttpConfig(Config config) {

    assignBooleanProperty(config, 'enabled')

    [
      'listeningHost',
      'contextPath'
    ].each { assignStringProperty(config, it) }

    [
      'listeningPort': 'port',
      'corePoolSize' : null,
      'maxPoolSize': null,
      'queueLimit' : null,
    ].each { key, prop -> assignIntegerProperty(config, key, prop) }

  }

  public URI getUri() {
    String path = contextPath ? '/' + contextPath : ''
    URI.create("${scheme}://${listeningHost}:${port ?: defaultPort}${path}")
  }

  protected String getScheme() {
    'http'
  }

  protected int getDefaultPort() {
    80
  }

  protected void assignStringProperty(Config config, String name) {
    if (config?.hasPath(name)) {
      this."${name}" = config.getString(name)
    }
  }

  protected void assignIntegerProperty(Config config, String keyName, String propName = null) {
    if (config?.hasPath(keyName)) {
      this."${propName ?: keyName}" = config.getInt(keyName)
    }
  }

  protected void assignBooleanProperty(Config config, String name) {
    if (config?.hasPath(name)) {
      this."${name}" = config.getBoolean(name)
    }
  }

}