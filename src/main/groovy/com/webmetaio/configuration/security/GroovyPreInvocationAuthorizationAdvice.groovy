package com.webmetaio.configuration.security

import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.access.prepost.PreInvocationAttribute
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice
import org.springframework.security.core.Authentication

class GroovyPreInvocationAuthorizationAdvice implements PreInvocationAuthorizationAdvice {

  protected static final Set<String> NON_SECURABLE_METHODS =
    ['invokeMethod', 'getMetaClass', 'setMetaClass', 'getProperty', 'setProperty', 'isTransactional',
     'getTransactional', 'setTransactional']

  private PreInvocationAuthorizationAdvice platformAdvice

  GroovyPreInvocationAuthorizationAdvice(PreInvocationAuthorizationAdvice pre) {
    platformAdvice = pre
  }

  @Override
  boolean before(Authentication authentication, MethodInvocation methodInvocation, PreInvocationAttribute preInvocationAttribute) {
    if (NON_SECURABLE_METHODS.contains(methodInvocation.getMethod().getName())) {
      return true
    } else {
      return platformAdvice.before(authentication, methodInvocation, preInvocationAttribute)
    }
  }
}
