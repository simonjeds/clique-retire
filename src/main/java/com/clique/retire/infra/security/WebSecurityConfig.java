package com.clique.retire.infra.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.clique.retire.infra.filter.JWTAuthenticationFilter;

/**
 * 
 * @author everton.gomes Classe responsável por definir os filtros de segurança
 *         da aplicação
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.anyRequest().authenticated().and()
				// filtra as requisições para verificar a presenca do JWT no header
				.addFilterBefore(getJwtAuthenticationFilter(), BasicAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/rest/login/**", "/rest/test/**", "/error", "/v2/api-docs", "/actuator/**", 
				"/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/rest/pedido/reportar-novo-pedido",
				"/test/sinalizador/**", "/sinalizador", "/rest/entrega/confirmar-entrega-pedido-motociclista", 
				"/rest/entrega/confirmar-pedido-nao-entregue-motociclista", "/filial/*",
				"/rest/separacao/finalizar-separacao-pedido-coletor",
				"/rest/transferencia/confirmar-recebimento-nota-automatica",
				"/rest/separacao/obter-quantidade-pedidos-aguardando-separacao",
				"/rest/separacao/iniciar-separacao-pedido-coletor",
				"/rest/entrega/retornar-pedido-expedicao",
				"/rest/pedido/cancelar-transferencia","/rest/emitir-nota-fiscal/habilitar-fluxo-sap",
				"/rest/emitir-nota-fiscal/consultar-status-contingencia",
				"/rest/pedido/buscar-pedidos-pendente",
				"/rest/emitir-nota-fiscal/consultar-pedido",
				"/rest/panel/consulta-panel-operacional",
				"/rest/pedido/cancelar-transferencia",
				"/rest/pedido/buscar-pedidos-pendente",
				"/rest/lojas-preproducao",
				"/rest/pedido/registrar-fase"
				);
		

	}

	private JWTAuthenticationFilter getJwtAuthenticationFilter() {
		return new JWTAuthenticationFilter();
	}

}
