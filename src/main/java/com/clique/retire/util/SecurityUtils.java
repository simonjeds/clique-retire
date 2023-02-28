package com.clique.retire.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

  public static Integer getCodigoUsuarioLogado() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return Integer.valueOf((String) authentication.getPrincipal());
  }

}
