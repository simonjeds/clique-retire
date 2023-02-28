package com.clique.retire.service.drogatel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.repository.drogatel.MarketingPlaceRepositoryImpl;

@Service
public class MarketingPlaceService {
	
	@Autowired
	private MarketingPlaceRepositoryImpl repository;
	
	public String obterDescricao(String numeroPedidoEcommerce) {
		if (StringUtils.isBlank(numeroPedidoEcommerce)) {
			return null;
		}
		return repository.obterDescricao(numeroPedidoEcommerce);
	}
	
}
