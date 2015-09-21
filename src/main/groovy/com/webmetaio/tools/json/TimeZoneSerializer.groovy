package com.webmetaio.tools.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializerProvider


class TimeZoneSerializer extends com.fasterxml.jackson.databind.JsonSerializer<TimeZone> {

  void serialize(TimeZone timezone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    jsonGenerator.writeString(timezone.ID)
  }

}
