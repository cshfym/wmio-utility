package com.webmetaio.configuration.security

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.webmetaio.tools.json.JacksonObjectMapperFactory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Configurable

import javax.annotation.PostConstruct

@Slf4j
@Configurable
class DistinguishedNamePermissionsService {

  @Delegate DistinguishedNamePermissionsMapper permissionsMapper

  String mappingFilename
  final ObjectMapper objectMapper = JacksonObjectMapperFactory.createObjectMapper()

  DistinguishedNamePermissionsService(String mappingFilename) {
    this.mappingFilename = mappingFilename
    permissionsMapper = loadMapper()
  }

  DistinguishedNamePermissionsService() { }

  private DistinguishedNamePermissionsMapper loadMapper() {
    def file = new File(mappingFilename)
    List<PermissionsMapping> mappings
    try {
      mappings = objectMapper.readValue(file, new TypeReference<List<PermissionsMapping>>(){})

      return new DistinguishedNamePermissionsMapper(mappings)
    } catch (IOException e) {
      log.error("Unable to load mapper", e)
    }

    null
  }

  @PostConstruct
  void initialize() {
    if (!mappingFilename) {
      throw new IllegalStateException("mappingFilename not configured")
    }
    if (!permissionsMapper) {
      permissionsMapper = loadMapper()
    }
    new FileChangeWatcher(mappingFilename, {
      def mapper = loadMapper()
      if (!mapper) {
        log.error("Reload for ${mappingFilename} failed")
      } else {
        permissionsMapper = mapper
      }
    })
  }
}
