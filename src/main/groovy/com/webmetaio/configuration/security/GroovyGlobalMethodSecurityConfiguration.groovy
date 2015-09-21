package com.webmetaio.configuration.security

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.annotation.Jsr250Voter
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter
import org.springframework.security.access.vote.AffirmativeBased
import org.springframework.security.access.vote.AuthenticatedVoter
import org.springframework.security.access.vote.RoleVoter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.util.Assert

/**
 * Default global method configuration providing groovy preauthorization advice to allow groovy metamethods to pass
 * through spring security when a class is annotated with @PreAuthorize('denyAll'). (shows up in autowiring secured
 * classes)
 */
@Configuration
@EnableGlobalMethodSecurity(proxyTargetClass = true, prePostEnabled = true)
class GroovyGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

  @Override
  PreInvocationAuthorizationAdvice preInvocationAuthorizationAdvice() {
    new GroovyPreInvocationAuthorizationAdvice(super.preInvocationAuthorizationAdvice())
  }

  //All this is unnecessary in an implementation where accessDecisionManager() calls preInvocationAuthorizationAdvice()
  //which is provided through the api to customize the pre advice. bug? 3.2 doesn't do this, had to override all things
  //it touched. These are lifted from the implementation, only changing line 38
  private AnnotationAttributes enableMethodSecurity

  @Override
  protected AccessDecisionManager accessDecisionManager() {
    List<AccessDecisionVoter> decisionVoters = new ArrayList<AccessDecisionVoter>()
    if (prePostEnabled()) {
      decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(
        preInvocationAuthorizationAdvice()))
    }
    if (jsr250Enabled()) {
      decisionVoters.add(new Jsr250Voter())
    }
    decisionVoters.add(new RoleVoter())
    decisionVoters.add(new AuthenticatedVoter())
    return new AffirmativeBased(decisionVoters)
  }

  private boolean prePostEnabled() {
    return enableMethodSecurity().getBoolean("prePostEnabled")
  }

  private boolean securedEnabled() {
    return enableMethodSecurity().getBoolean("securedEnabled")
  }

  private boolean jsr250Enabled() {
    return enableMethodSecurity().getBoolean("jsr250Enabled")
  }

  private int order() {
    return (Integer) enableMethodSecurity().get("order")
  }

  private AnnotationAttributes enableMethodSecurity() {
    if (enableMethodSecurity == null) {
      // if it is null look at this instance (i.e. a subclass was used)
      EnableGlobalMethodSecurity methodSecurityAnnotation = AnnotationUtils
        .findAnnotation(getClass(),
        EnableGlobalMethodSecurity.class)
      Assert.notNull(methodSecurityAnnotation,
        EnableGlobalMethodSecurity.class.getName() + " is required")
      Map<String, Object> methodSecurityAttrs = AnnotationUtils
        .getAnnotationAttributes(methodSecurityAnnotation)
      this.enableMethodSecurity = AnnotationAttributes
        .fromMap(methodSecurityAttrs)
    }
    return this.enableMethodSecurity
  }
}
