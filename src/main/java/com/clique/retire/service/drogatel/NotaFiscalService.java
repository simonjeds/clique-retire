package com.clique.retire.service.drogatel;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ItemNotaFiscalDTO;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.cosmos.NotaFiscalRepositoryCustom;

@Service
public class NotaFiscalService {

	@Autowired
	private NotaFiscalRepositoryCustom notaFiscalRepository;

	@Transactional
	public void removerItensNotaPelosItensPedidos(List<Integer> idsItensPedido) {
		if (CollectionUtils.isNotEmpty(idsItensPedido)) {
			notaFiscalRepository.removerItensNotaPeloItemPedido(idsItensPedido);
		}
	}

	@Transactional
	public void atualizarValorNotaEItens(List<ItemNotaFiscalDTO> itens, Pedido pedido) {
		if (CollectionUtils.isNotEmpty(itens)) {
			notaFiscalRepository.atualizarValorNotaEItens(itens, pedido);
		}
	}

}
