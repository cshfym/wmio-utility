package com.webmetaio.configuration.server

class ServerConfig {

  String env

  HttpConfig http

  HttpsConfig https

  String name

  List<Map> staticFiles

  Properties accessLog

  boolean logPayloads = false

  ServerConfig() {
  }

  ServerConfig(Map map) {

    if (map?.containsKey('http')) {
      this.http = new HttpConfig(map.http)
    }
    if (map?.containsKey('https')) {
      this.https = new HttpsConfig(map.https)
    }

    if (map?.containsKey('staticFiles')) {
      this.staticFiles = map.staticFiles
    }

    if (map?.containsKey('env')) {
      this.env = map.env
    }

    if (map?.containsKey('accessLog')) {
      this.accessLog = map.accessLog.toProperties()
    }

    if (map?.containsKey('logPayloads')) {
      this.logPayloads = map.logPayloads.toBoolean()
    }

  }

  public boolean isAtLeastOneEnabled() {
    http?.enabled || https?.enabled
  }

  List<HttpConfig> getConfigurations() {
    [http, https] - [null]
  }

}

