package com.webmetaio.configuration.security

import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

@Configurable(autowire = Autowire.BY_TYPE)
class PermissionsCheck {

  @Autowired
  DistinguishedNamePermissionsService distinguishedNamePermissionsService

  boolean allow(String expression) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
    def dn = authentication.principal.username
    distinguishedNamePermissionsService.hasPermission(dn, expression)
  }
}
