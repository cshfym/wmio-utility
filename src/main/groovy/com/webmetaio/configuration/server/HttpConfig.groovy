package com.webmetaio.configuration.server

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

  public HttpConfig(Map map) {

    assignBooleanProperty(map, 'enabled')

    [
      'listeningHost',
      'contextPath'
    ].each { assignStringProperty(map, it) }

    [
      'listeningPort': 'port',
      'corePoolSize' : null,
      'maxPoolSize': null,
      'queueLimit' : null,
    ].each { key, prop -> assignIntegerProperty(map, key, prop) }

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

  protected void assignStringProperty(Map map, String name) {
    if (map?.containsKey(name)) {
      this."${name}" = map."${name}"
    }
  }

  protected void assignIntegerProperty(Map map, String keyName, String propName = null) {
    if (!map?.containsKey(keyName)) {
      return
    }
    if (!map."${keyName}".toString().isInteger()) {
      log.error "Could not parse [${map."${keyName}"}] as valid ${propName ?: keyName}!"
      return
    }
    this."${propName ?: keyName}" = map."${keyName}".toInteger()
  }

  protected void assignBooleanProperty(Map map, String name) {
    if (map?.containsKey(name)) {
      this."${name}" = map."${name}" as boolean
    }
  }

}
