package com.webmetaio.tools.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule

import java.text.DateFormat
import java.text.SimpleDateFormat


class JacksonObjectMapperFactory {

  static ObjectMapper createObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper()
    objectMapper.registerModule(new JacksonModule())
    objectMapper.registerModule(new JodaModule())
    objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
    objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    objectMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone('UTC')

    objectMapper.setDateFormat(format)
    objectMapper.setTimeZone(format.timeZone)
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    objectMapper

  }

}

