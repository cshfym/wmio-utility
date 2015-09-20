package com.webmetaio.utility.converters

abstract class CoreDataConverter {

  /**
   * To convert from POGO to GORM domain object, grails application instance has to be provided.
   *
   * @return the grails application
   */
  def getGrailsApplication() {
    null
  }

  ClassPropertyTypeResolver getTypeResolver() {

    if (getGrailsApplication() == null) {
      return new EmptyClassPropertyTypeResolver()
    }

    new ClassPropertyTypeResolver() {
      @Override
      Class resolveContainedType(Class clazz, String propertyName) {
        getGrailsApplication()?.getDomainClass(clazz.name)?.associationMap?.get(propertyName)
      }

      @Override
      Class resolveType(Class clazz, String propertyName) {
        getGrailsApplication()?.getDomainClass(clazz.name)?.getPropertyByName(propertyName)?.type
      }
    }

  }

  abstract Class getDomainClass()

  abstract Class getCoreDataClass()

  abstract Map<String,String> getMappedProperties()

  void additionalCopyToCoreData(domain, coreData, Map objectMap) {}

  void additionalCopyToDomain(coreData, domain, Map objectMap) {}

  /**
   * Splits string by "[]".
   *
   * <pre>
   * splitByBracket("a.b[].c.d[].e") -> ["a.b", "c.d[].e"]
   * </pre>
   *
   * @param accessor  the string
   * @return a pair of strings separated by "[]"
   */
  def splitByBracket(String accessor) {
    def pos = accessor.indexOf('[]')
    if (pos == -1) {
      return [accessor, null]
    } else {
      // + 3 to skip "[]."
      return [accessor[0..pos - 1], accessor[pos + 3..-1]]
    }
  }

  /**
   * Sets the value from src object to dst object by the given accessor. This method is called recursively to set all
   * values accordingly.
   *
   * When converting from Set to List, the order of elements is not guaranteed. Therefore an object map is used to
   * keep the relation of elements.
   *
   * @param src          the source
   * @param srcAccessor  the accessor to be copied from
   * @param dst          the destination
   * @param dstAccessor  the accessor to be copied to
   * @param state        the {@code ConversionState} containing a map of
   *                     relations between elements of src lists and dst lists
   */
  def setValue(src, String srcAccessor, dst, String dstAccessor, ConversionState state) {

    def (listAccessor, remainingAccessor) = splitByBracket(dstAccessor)
    if (!remainingAccessor) {// a.b
      setValue(dst, dstAccessor, getValue(src, srcAccessor))
    } else {
      def (srcListAccessor, srcRemainingAccessor) = splitByBracket(srcAccessor)
      def srcList = getValue(src, srcListAccessor)// list a.b from a.b[]

      if (!getValue(dst, listAccessor) || state.requiresUpdate(listAccessor)) { // the dst list is not created yet
        state.reportUpdate(listAccessor)
        def listOwner = getValue(dst, splitByDot(listAccessor)[0])// the owner of a.b[] will be a
        def addTo = "addTo${splitByDot(listAccessor)[1].capitalize()}"
        // if it has addTo method then we're converting from core data to domain
        def attrName = splitByDot(listAccessor)[1]
        if (listOwner.getMetaClass().respondsTo(listOwner, addTo)) {
          listOwner."${attrName}"?.clear()
          def collectionType = typeResolver?.resolveContainedType(listOwner.getClass(), attrName)
          srcList.each {
            listOwner."$addTo"(collectionType?.newInstance() ?: [:])
          }
          getValue(dst, listAccessor).eachWithIndex { entry, idx ->
            state.objectMap.put(srcList[idx], entry)
          }
        } else {
          def dstList = []
          srcList.each { entry ->
            def newElem = typeResolver?.resolveContainedType(listOwner.getClass(), attrName)?.newInstance() ?: [:]
            dstList << newElem
            state.objectMap.put(entry, newElem)
          }
          setValue(dst, listAccessor, dstList)
        }
      }

      srcList.each { srcElem ->
        if (state.objectMap.containsKey(srcElem)) {// for update case the map might not contain the new element
          setValue(srcElem, srcRemainingAccessor, state.objectMap.get(srcElem), remainingAccessor, state)
        }
      }
    }
  }

  /**
   * Splits String by dots.
   *
   * <pre>
   * splitByDot("a") -> ["", "a"]
   * splitByDot("a.b.c") -> ["a.b", "c"]
   * </pre>
   */
  def splitByDot(String listAccessor) {
    def pos = listAccessor.lastIndexOf('.')
    if (pos == -1) {
      ["", listAccessor]
    } else {
      [listAccessor[0..pos - 1], listAccessor[pos + 1..-1]]
    }
  }

  /**
   * Gets the value by the given accessor.
   *
   * @param obj       the object to get value from
   * @param accessor  the accessor to the value
   * @return the value
   */
  def getValue(obj, String accessor) {
    def varIterator = obj
    accessor.tokenize('.').each {
      varIterator = varIterator?."${it}"
    }
    varIterator
  }

  def setValue(obj, String accessor, value) {
    def varIterator = obj

    List accessorList = accessor.tokenize('.')
    for (int i = 0; i < accessorList.size(); i++) {
      def it = accessorList[i]

      if (i != accessorList.size() - 1) {
        if (varIterator."${it}" == null) {
          // don't create unnecessary object if the value is null
          if (value == null) {
            return null
          }
          // use grailsApplication to check attr type since reflection only returns type Object
          varIterator."${it}" = typeResolver?.resolveType(varIterator.getClass(), it)?.newInstance() ?: [:]
        }
      } else {
        if (value == null) {
          try {
            varIterator."${it}" = null
          } catch (IllegalArgumentException e) {
            // hack for resetting primitive types, since null is not accepted
            varIterator."${it}" = 0
          }
        } else if (varIterator.getMetaClass().getMetaProperty(it)?.type?.isEnum()) {
          // handles the conversion between enum and string. Use metaClass to check vars inherited from superclass
          varIterator."${it}" = varIterator.getMetaClass().getMetaProperty(it)?.type?.invokeMethod('valueOf', value)
        } else if (value.getClass().isEnum()) {
          varIterator."${it}" = value.name()
        } else {
          varIterator."${it}" = value
        }
      }

      varIterator = varIterator."${it}"
    }

    varIterator
  }

  public <T> T toCoreData(domain) {
    def state = new ConversionState()
    def coreData = coreDataClass.newInstance()
    mappedProperties.each { k, v ->
      setValue(domain, k, coreData, v, state)
    }
    additionalCopyToCoreData(domain, coreData, state.objectMap)
    coreData
  }

  public <T> T toDomain(coreData, domainInstance = null, List propList = null, boolean isInclusive = true) {
    def state = new ConversionState()
    def domain = domainInstance ?: domainClass.newInstance()
    def filtered = propList == null ? mappedProperties : mappedProperties.findAll { k, v ->
      if (propList.any { prefix -> k.startsWith(prefix) }) {
        return isInclusive
      }
      !isInclusive
    }
    filtered.each { k, v ->
      setValue(coreData, v, domain, k, state)
    }
    additionalCopyToDomain(coreData, domain, state.objectMap)
    domain
  }

  public <T> T updateCoreData(coreDataSource, coreDataDelta) {
    mappedProperties.each { k, v ->
      def (accessor, remainingAccessor) = splitByBracket(v)

      def newValue = getValue(coreDataDelta, accessor)
      if (newValue != null) {
        setValue(coreDataSource, accessor, newValue)
      }
    }
    coreDataSource
  }

}
