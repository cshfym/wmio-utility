package com.webmetaio.utility.converters

interface ClassPropertyTypeResolver {

  /**
   * For a given property name on a class, this returns the type of the
   * contained type of a collection if the property is a list, set or such. If
   * the property is a map, the class of the value is returned.
   *
   * @param clazz         a {@code Class} for which the type of one of its
   *                      properties should be determined
   * @param propertyName  a {@code String} containing the name of the property
   *                      for which to determine the type
   * @return  a {@code Class} representing the contained type of the property,
   *          or {@code null} if none was determined
   */
  Class resolveContainedType(Class clazz, String propertyName)

  /**
   * For a given property name on a class, this returns the type of the
   * property.
   *
   * @param clazz         a {@code Class} for which the type of one of its
   *                      properties should be determined
   * @param propertyName  a {@code String} containing the name of the property
   *                      for which to determine the type
   * @return  a {@code Class} representing the type of the property, or
   *          {@code null} if none was determined
   */
  Class resolveType(Class clazz, String propertyName)

}
