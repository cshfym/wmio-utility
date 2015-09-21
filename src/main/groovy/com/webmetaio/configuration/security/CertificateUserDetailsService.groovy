package com.webmetaio.configuration.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.*
import org.springframework.stereotype.Service

import javax.naming.ldap.LdapName
import javax.naming.ldap.Rdn

@Service
class CertificateUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<Authentication> {

  @Override
  UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
    return loadUserByUsername(authentication.principal)
  }

  @Override
  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (!username) {
      throw new UsernameNotFoundException('<null>')
    }

    // All trusted certs get ROLE_USER. It's the most basic level
    List<SimpleGrantedAuthority> roles = [new SimpleGrantedAuthority('ROLE_USER')]
    try {
      LdapName dn = new LdapName(username)
      for (Rdn part : dn.getRdns()) {
        // Loop through the parts of the DN and extract the roles,
        // country, and political zone
        String typ = part.getType()?.toUpperCase()
        String val = ((String) part.getValue())?.toUpperCase()
        if (!typ || !val) {
          continue
        }

        if (typ == 'C') {
          //TODO  Country. Map it to it's political zone (EU, US) and country
          // These will look like ZONE_EU, COUNTRY_BG
          //String zone = Country.zoneForAbbreviation(val)
          //  if (zone) {
          //    roles.add(new SimpleGrantedAuthority(zone))
          //  }
          roles.add(new SimpleGrantedAuthority('ROLE_COUNTRY_'+val))
        } else if (typ == 'O') {
          roles.add(new SimpleGrantedAuthority('ROLE_COMPANY_'+val))
        } else if (typ == 'OU') {
          // OUs are real roles for us like ROLE_APPSERVER
          roles.add(new SimpleGrantedAuthority('ROLE_'+val))
        }
      }

    } catch (Exception e) {
      throw new UsernameNotFoundException('Bad username: '+username)
    }

    new User(username, 'pwd', true, true, true, true, roles)
  }
}
