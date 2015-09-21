package com.webmetaio.tools.json

class LocaleTools {

  public static Locale getLocaleFromString(String localeString) {

    try {
      Locale locale = null
      if (localeString) {
        if (localeString.contains('_')) {
          def (language, country) = localeString.tokenize('_')
          locale = new Locale(language, country)
        } else {
          locale = new Locale(localeString)
        }
      }
      return locale
    } catch (Exception e) {
      return null
    }
  }

}
