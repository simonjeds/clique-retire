package com.clique.retire.infra.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.clique.retire.infra.service.TokenAuthenticationService;

/**
 * 
 * @author everton.gomes Filtro responsável por validar o token de acesso do
 *         usuário.
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

	private static final String OPTIONS = "OPTIONS";

	private static final String CABECALHO_DE_AUTORIZACAO_INVALIDO = "Authorization header must be provided";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		try {
			if (((HttpServletRequest) request).getMethod().equalsIgnoreCase(OPTIONS)) {
				filterChain.doFilter(request, response);
				return;
			}
			Authentication authentication = TokenAuthenticationService
					.getAuthentication((HttpServletRequest) request);
			if (authentication == null) {
				sendHttpForbidden(response);
			} else {
				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(request, response);
			}
		} catch (Exception e) {
			sendHttpForbidden(response);
		}
	}

	private void sendHttpForbidden(ServletResponse response) throws IOException {
		((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, CABECALHO_DE_AUTORIZACAO_INVALIDO);
	}
}
