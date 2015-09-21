package com.webmetaio.configuration.server

import groovy.util.logging.Slf4j

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

  List<String> additionalCipherSuites

  List<String> additionalProtocols

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

  public HttpsConfig(Map map) {

    super(map)

    [
      'keystoreFile': 'keystoreContent',
      'truststoreFile': 'truststoreContent'
    ].each { key, prop -> assignFileContentProperty(map, key, prop) }

    [
      'keystorePassphrase',
      'keystoreType',
      'truststorePassphrase',
      'truststoreType'
    ].each { assignStringProperty(map, it) }

    [
      'clientMode',
      'wantClientAuth',
      'needClientAuth'
    ].each { assignBooleanProperty(map, it) }

    [
      'additionalCipherSuites',
      'additionalProtocols'
    ].each { assignCommaSeparatedListProperty(map, it) }

  }

  public String[] getCipherSuites() {
    (CIPHER_SUITES + (additionalCipherSuites ?: [])).toArray(new String[0])
  }

  public String[] getProtocols() {
    (PROTOCOLS + (additionalProtocols ?: [])).toArray(new String[0])
  }

  protected byte[] loadStoreContents(filename){
    def file = new File(filename)
    if (file.exists() && file.canRead()) {
      return file.bytes
    } else {
      log.error "Could not read keystore file [${filename}]"
    }
  }

  protected void assignCommaSeparatedListProperty(Map map, String name) {
    if (map?.containsKey(name)) {
      this."${name}" = map."${name}".split(',').collect { it.trim() }
    }
  }

  protected void assignFileContentProperty(Map map, String keyName, String propName = null) {
    if (map?.containsKey(keyName)) {
      this."${propName ?: keyName}" = loadStoreContents(map."${keyName}" as String)
    }
  }

}
