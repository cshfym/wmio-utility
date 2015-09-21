package com.webmetaio.tools.json

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule

class JacksonModule extends SimpleModule {

  JacksonModule() {

    super('JacksonModule', new Version(1, 1, 0, ''))

    this.addDeserializer(Locale, new LocaleDeserializer())
    this.addSerializer(Locale, new LocaleSerializer())

    this.addDeserializer(TimeZone, new TimeZoneDeserializer())
    this.addSerializer(TimeZone, new TimeZoneSerializer())

    this.addSerializer(GString, new GroovyStringSerializer())

  }

}
