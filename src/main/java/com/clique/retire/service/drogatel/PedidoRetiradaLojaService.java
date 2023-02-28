package com.clique.retire.service.drogatel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.model.drogatel.PedidoRetiradaLoja;
import com.clique.retire.repository.drogatel.PedidoRetiradaLojaRepository;

@Service
public class PedidoRetiradaLojaService {	
	
	@Autowired
	private PedidoRetiradaLojaRepository pedidoRetLojaRespository;
	
	@Transactional("drogatelTransactionManager")
	public void cadastrarPedidoRetiradaLoja(Long numeroPedido, String codigoAberturaArmario) {
		PedidoRetiradaLoja pedidoBox = new PedidoRetiradaLoja(Integer.valueOf(1));
		pedidoBox.setCodigoAberturaArmario(codigoAberturaArmario);
		pedidoBox.setNumeroPedido(numeroPedido.intValue());
		pedidoRetLojaRespository.save(pedidoBox);
	}

}
