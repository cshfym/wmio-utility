package com.webmetaio.configuration.file


class ResourceBasedDefaultProvider implements ExternalConfigDefaultsProvider {

  String resourceLocation

  ResourceBasedDefaultProvider(String location) {
    resourceLocation = location
  }

  @Override
  ConfigObject getDefaultConfigObject(ExternalConfig externalConfig) {
    new ConfigSlurper().parse(defaultConfigResource)
  }

  protected URL getDefaultConfigResource() {
    getClass().getResource(resourceLocation)
  }

}
