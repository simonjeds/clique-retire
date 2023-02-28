package com.clique.retire.infra.service;

import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.clique.retire.infra.exception.ForbiddenException;
import com.clique.retire.util.DateUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {
	
	private TokenAuthenticationService() {}

	static final String SECRET = "psw#frameworkSwagger2017;";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	
	public static String addAuthentication(String codUsuario) {
		// Token expira em 30 minutos
		Date date = recuperaDataExpiracao();
		
		return Jwts.builder()
				.setSubject(codUsuario)
				.setExpiration(date)
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
	}
	
	public static Authentication getAuthentication(HttpServletRequest request) {
		try {
			String token = request.getHeader(HEADER_STRING);
			if (token != null) {
				// faz parse do token
				String user = Jwts.parser()
								.setSigningKey(SECRET)
								.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
								.getBody()
								.getSubject();
				
				if (user != null) {
					return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
				}
			}
		}catch(Exception e) {
			throw new ForbiddenException();
		}
		return null;
	}

	private static Date recuperaDataExpiracao() {
		return DateUtils.em30Minutos();
	}
	
}
