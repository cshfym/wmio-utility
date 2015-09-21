package com.webmetaio.tools.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext


class TimeZoneDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<TimeZone> {

  TimeZone deserialize(JsonParser jp, DeserializationContext ctxt) {

    if (!jp.currentToken == JsonToken.VALUE_STRING ) {
      throw ctxt.mappingException("Expect number, but got ${jp.currentToken}")
    }

    if (!jp.text) {
      return null
    }

    try {
      return TimeZone.getTimeZone(jp.text)
    } catch (Exception e) {
      throw ctxt.mappingException("Couldn't create timezone from value \"${jp.text}\"")
    }

  }

}
