package com.clique.retire.service.drogatel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.model.drogatel.ModalidadePagamento;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.drogatel.ModalidadePagamentoRepository;

@Service
public class ModalidadePagamentoService {

	@Autowired
	private ModalidadePagamentoRepository repository;
	
	public ModalidadePagamento obterPeloPedido(Pedido pedido) {
		return repository.findByPedido(pedido);
	}


}