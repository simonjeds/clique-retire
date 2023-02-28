package com.clique.retire.repository.drogatel;

import java.util.List;

import com.clique.retire.dto.ItemPedidoSIACDTO;
import com.clique.retire.model.drogatel.PrePedidoSiac;

public interface SiacRepositoryCustom {
	
	void atualizarNumeroPrePeddio(PrePedidoSiac prePedido);
	
	List<ItemPedidoSIACDTO> obterItensValorMaiorQueSIAC(Integer numeroPedido);

}
