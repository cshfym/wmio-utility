package com.webmetaio.configuration.file

public interface ExternalConfig {

  ConfigObject getConfig()

  String getConfigBaseName()

  /**
   * @param key config key
   * @return key value
   * @throws IllegalConfigurationException if key is not found
   */
  Object requiredValue(String key) throws IllegalConfigurationException

  /**
   * TODO returning an empty map is not terribly helpful here. We should probably not expose this aspect of
   * ConfigObject's behavior to users of this method.
   *
   * @param key config key to look up
   * @return the config value, or an empty map if not found.
   */
  Object optionalValue(String key)

}
