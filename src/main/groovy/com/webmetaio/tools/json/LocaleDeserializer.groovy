package com.webmetaio.tools.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

import java.text.ParseException

class LocaleDeserializer extends JsonDeserializer<Locale> {

  Locale deserialize(JsonParser jp, DeserializationContext ctxt) {
    if (!jp.currentToken == JsonToken.VALUE_STRING) {
      throw ctxt.mappingException("Expect string, but got ${jp.currentToken}")
    }

    if (!jp.text) {
      return null
    }

    try {
      return LocaleTools.getLocaleFromString(jp.text)
    } catch (ParseException e) {
      throw ctxt.mappingException("Couldn't create locale from value [${jp.text}]")
    }

  }

}
