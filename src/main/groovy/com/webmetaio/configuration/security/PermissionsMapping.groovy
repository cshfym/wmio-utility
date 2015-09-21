package com.webmetaio.configuration.security

import javax.naming.ldap.LdapName

class PermissionsMapping {
  String identityType
  LdapName identity
  List<String> permissions
}
