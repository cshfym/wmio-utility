package com.webmetaio.configuration.file


public abstract class AbstractExternalConfig implements ExternalConfig {

  protected String configBaseName

  protected AbstractExternalConfig(String configBaseName) {
    this.configBaseName = configBaseName
  }

  @Override
  Object requiredValue(String key) throws IllegalConfigurationException {

    def ret = optionalValue(key)
    if (ret instanceof ConfigObject && !ret) {
      throw new IllegalConfigurationException("Can not load configuration for key [${key}]")
    }
    ret

  }

  @Override
  Object optionalValue(String key) {
    key.tokenize('.').inject(config) { acc, namePart -> acc."$namePart" }
  }

  @Override
  String getConfigBaseName() {
    configBaseName
  }

}
