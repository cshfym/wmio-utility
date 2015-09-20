package com.webmetaio.utility.converters


class EmptyClassPropertyTypeResolver implements ClassPropertyTypeResolver {

  @Override
  Class resolveContainedType(Class clazz, String propertyName) {
    null
  }

  @Override
  Class resolveType(Class clazz, String propertyName) {
    null
  }

}

