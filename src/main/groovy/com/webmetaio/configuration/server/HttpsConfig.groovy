package com.webmetaio.configuration.server

import com.typesafe.config.Config
import groovy.util.logging.Slf4j
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource

@Slf4j
class HttpsConfig extends HttpConfig {

  private static final List<String> CIPHER_SUITES = [
    'TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA',
    'TLS_RSA_WITH_AES_256_CBC_SHA',
    'TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA',
    'TLS_RSA_WITH_AES_128_CBC_SHA',
    'TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA',
    'TLS_EMPTY_RENEGOTIATION_INFO_SCSV'
  ]

  private static final List<String> PROTOCOLS = ['TLSv1.2', 'TLSv1.1', 'TLSv1']

  List<String> overrideCipherSuites

  List<String> overrideProtocols

  byte[] keystoreContent

  String keystorePassphrase

  String keystoreType

  String scheme = 'https'

  byte[] truststoreContent

  String truststorePassphrase

  String truststoreType

  boolean clientMode = false
  boolean wantClientAuth = false
  boolean needClientAuth = false

  int defaultPort = 443

  public HttpsConfig() {
  }

  public HttpsConfig(Config config) {

    super(config)

    [
      'keystoreFile': 'keystoreContent',
      'truststoreFile': 'truststoreContent'
    ].each { key, prop -> assignFileContentProperty(config, key, prop) }

    [
      'keystorePassphrase',
      'keystoreType',
      'truststorePassphrase',
      'truststoreType'
    ].each { assignStringProperty(config, it) }

    [
      'clientMode',
      'wantClientAuth',
      'needClientAuth'
    ].each { assignBooleanProperty(config, it) }

    [
      'overrideCipherSuites',
      'overrideProtocols'
    ].each { assignCommaSeparatedListProperty(config, it) }

  }

  public String[] getCipherSuites() {
    (overrideCipherSuites ?: CIPHER_SUITES).toArray(new String[0])
  }

  public String[] getProtocols() {
    (overrideProtocols ?: PROTOCOLS).toArray(new String[0])
  }

  protected static byte[] loadStoreContents(String filename) {

    Resource resource = new DefaultResourceLoader().getResource(filename)

    if (resource.exists() && resource.readable) {
      return resource.inputStream.bytes
    } else {
      File file = new File(filename)
      if (file.exists() && file.canRead()) {
        return new FileInputStream(file).bytes
      }
      log.error "File [${filename}] to be loaded as keystore cannot be read or does not exist"
    }

  }

  protected void assignCommaSeparatedListProperty(Config config, String name) {
    if (config?.hasPath(name)) {
      this."${name}" = config.getStringList(name).collect { it.trim() }
    }
  }

  protected void assignFileContentProperty(Config config, String keyName, String propName = null) {
    if (config?.hasPath(keyName)) {
      String fileName = config.getString(keyName)
      String propertyName = propName ?: keyName
      log.info "Loading file [${fileName}] for property [${propertyName}]"
      this."${propertyName}" = loadStoreContents(fileName)
    }
  }

}
