package com.oem.evwarranty.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Global controller advice to add common attributes to the model.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("userRoles")
    public Set<String> userRoles(Authentication auth) {
        if (auth == null) {
            return Collections.emptySet();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
