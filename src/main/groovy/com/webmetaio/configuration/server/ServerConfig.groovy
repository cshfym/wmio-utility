package com.webmetaio.configuration.server;

import com.typesafe.config.Config


class ServerConfig {

  String env

  HttpConfig http

  HttpsConfig https

  boolean logPayloads = false

  boolean monitoringEnabled = false

  String name

  List<Config> staticFiles

  Config accessLog


  ServerConfig() { }

  ServerConfig(Config config) {

    if (!config) {
      return
    }

    if (config.hasPath('http')) {
      this.http = new HttpConfig(config.getConfig('http'))
    }
    if (config.hasPath('https')) {
      this.https = new HttpsConfig(config.getConfig('https'))
    }

    if (config.hasPath('staticFiles')) {
      this.staticFiles = config.getConfigList('staticFiles')
    }

    if (config.hasPath('env')) {
      this.env = config.getConfig('env')
    }

    if (config.hasPath('name')) {
      this.name = config.getString('name')
    }

    if (config.hasPath('accessLog')) {
      this.accessLog = config.getConfig('accessLog')
    }

    if (config.hasPath('logPayloads')) {
      this.logPayloads = config.getBoolean('logPayloads')
    }

    if (config.hasPath('monitoringEnabled')) {
      this.monitoringEnabled = config.getBoolean('monitoringEnabled')
    }

  }

  public boolean isAtLeastOneEnabled() {
    http?.enabled || https?.enabled
  }

  List<HttpConfig> getConfigurations() {
    [http, https] - [null]
  }

}