package com.clique.retire.service.drogatel;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;

public class GeraToken {
	
	private static final String ERRO_RETORNAR_TOKEN = "Erro ao retornar o token";
	private static final String ERRO_RETORNAR_OAUTH_TOKEN = "Erro ao retornar o oauth token";
	private static final String URI_TOKEN_01 = "/auth/oauth/token?grant_type=client_credentials";
	private static final String URI_TOKEN_02 = "/auth/api/v1/authentication/authenticate-guest?grant_type=client_credentials&guestId=painel_clique_retire";	
	private static final String HOSTNAME_APP_NAO_ENCONTRADO = "Hostname do app não encontrado";

	
	@Autowired
	private DrogatelParametroRepository drogatelParametroRepository;


	public String getHostNameApp() {
		DrogatelParametro parametro = drogatelParametroRepository
				.findByNome(ParametroEnum.APPARAUJO_COM_BR.getDescricao());
		if (parametro == null) {
			throw new BusinessException(HOSTNAME_APP_NAO_ENCONTRADO);
		}
		return parametro.getValor();
	}	

	@Transactional("drogatelTransactionManager")
	public String getAuthToken() {

		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("Authorization", "Bearer " + getAuth());
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<Object> response = restTemplate.exchange(getHostNameApp() + URI_TOKEN_02, HttpMethod.POST,
					entity, Object.class);
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) response.getBody();
			if(map != null) {
				return map.get("access_token");
			} else {
				throw new BusinessException(ERRO_RETORNAR_TOKEN);
			}
			
		} catch (Exception e) {
			throw new BusinessException(ERRO_RETORNAR_TOKEN);
		}

	}

	@Transactional("drogatelTransactionManager")
	public String getAuth() {

		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("Authorization", "Basic bW9iaWxlLWNsaWVudDptb2JpbGUtY2xpZW50");
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<Object> response = restTemplate.exchange(getHostNameApp() + URI_TOKEN_01, HttpMethod.POST,
					entity, Object.class);
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) response.getBody();
			if(map != null) {
				return map.get("access_token");
			} else {
				throw new BusinessException(ERRO_RETORNAR_OAUTH_TOKEN);
			}
		} catch (Exception e) {
			throw new BusinessException(ERRO_RETORNAR_OAUTH_TOKEN);
		}

	}


}