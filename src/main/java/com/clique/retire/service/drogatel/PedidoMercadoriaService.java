package com.clique.retire.service.drogatel;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.repository.drogatel.PedidoMercadoriaRepositoryImpl;

@Service
public class PedidoMercadoriaService {

	@Autowired
	private PedidoMercadoriaRepositoryImpl repository;

	@Transactional
	public void removerReferenciaPorItensPedido(List<Integer> idsItensPedido) {
		if (CollectionUtils.isNotEmpty(idsItensPedido)) {
			repository.removerReferenciaPorItensPedido(idsItensPedido);
		}
	}

}
