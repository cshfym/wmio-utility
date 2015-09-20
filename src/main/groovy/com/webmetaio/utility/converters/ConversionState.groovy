package com.webmetaio.utility.converters

class ConversionState {

  Map objectMap = [:]

  Set<String> updatedCollections = []

  boolean requiresUpdate(String accessor) {
    !(updatedCollections.any { it.startsWith(accessor) })
  }

  void reportUpdate(String accessor) {
    updatedCollections << accessor
  }

}
