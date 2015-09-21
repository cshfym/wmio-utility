package com.webmetaio.configuration.security

import javax.naming.ldap.LdapName

class DistinguishedNamePermissionsMapper {

  private final List<PermissionsMapping> permissionsMappings
  private final Map<LdapName,Set<String>> ldapNamePermissionsMap

  DistinguishedNamePermissionsMapper(List<PermissionsMapping> permissionsMappings) {
    ldapNamePermissionsMap = new HashMap<>(permissionsMappings.size())
    permissionsMappings.each { PermissionsMapping permissionsMapping ->
      // consistent sort so we can do a map lookup
      permissionsMapping.identity = new LdapName(permissionsMapping.identity.rdns.sort(false))

      permissionsMapping.permissions = permissionsMapping.permissions.collect { String permission ->
        permission.toUpperCase()
      }

      def mappedPermissions = ldapNamePermissionsMap.get(permissionsMapping.identity, new LinkedHashSet())
      mappedPermissions.addAll(permissionsMapping.permissions)
    }
    this.permissionsMappings = permissionsMappings
  }

  boolean hasPermission(String distinguishedName, String permissionExpression, boolean withDnContains = false) {

    def matchingPermissions = findMatchingPermissions(distinguishedName, withDnContains)

    // TODO Resolve WildcardMatcher
    WildcardMatcher.matchesAny(permissionExpression, matchingPermissions)
  }

  Set<String> findMatchingPermissions(String distinguishedName, boolean withDnContains = false) {
    findMatchingPermissionsClosure(distinguishedName, withDnContains)
  }

  private Closure<Set<String>> findMatchingPermissionsClosure = { String distinguishedName, boolean withDnContains ->
    def ldapName = new LdapName(distinguishedName)

    def matchingPermissions
    if (withDnContains) {
      matchingPermissions = permissionsMappings.findAll { PermissionsMapping expression ->
        ldapName.rdns.containsAll(expression.identity.rdns)
      }.collect { PermissionsMapping expression ->
        expression.permissions
      }.flatten() as Set
    } else {
      // if we require an exact dn name match then this will be quickest:
      // normalize order as we don't care about the order that the elements of the dn are in
      ldapName = new LdapName(ldapName.rdns.sort(false))
      matchingPermissions = ldapNamePermissionsMap[ldapName]
    }

    matchingPermissions

  }.memoizeAtLeast(10)
}
