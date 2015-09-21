package com.webmetaio.tools.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.SerializerProvider

class LocaleSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Locale> {

  void serialize(Locale locale, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    jsonGenerator.writeString(locale.toString())
  }

}
