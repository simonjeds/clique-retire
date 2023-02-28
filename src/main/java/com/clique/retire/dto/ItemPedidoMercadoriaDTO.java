package com.clique.retire.dto;

import lombok.Data;

@Data
public class ItemPedidoMercadoriaDTO {

	Integer codigoItemPedidoMercadoria;
	Integer quantidadeRecebida;
	Integer quantidadePedida;
	ItemPedidoDTO itemPedidoDTO; 
}
