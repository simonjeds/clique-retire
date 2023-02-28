package com.clique.retire.service.drogatel;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.model.drogatel.PedidoFracionado;
import com.clique.retire.repository.drogatel.PedidoFracionadoRepository;

@Service
public class PedidoFracionadoService {
	
	@Autowired
	private PedidoFracionadoRepository repository;
	
	public String obterSenhaPeloNumeroPedido(Integer numeroPedido) {
		Optional<PedidoFracionado> optionalPedidoFracionado = repository.findByIdAndFracionado(numeroPedido, SimNaoEnum.S);
		return optionalPedidoFracionado.isPresent() ? optionalPedidoFracionado.get().getSenha() : "";
	}
	
	public boolean isFracionado(Integer numeroPedido) {
		return StringUtils.isNotBlank(this.obterSenhaPeloNumeroPedido(numeroPedido));
	}

}
