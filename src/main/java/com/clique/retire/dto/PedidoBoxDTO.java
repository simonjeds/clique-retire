package com.clique.retire.dto;


import java.util.List;

import lombok.Data;

@Data
public class PedidoBoxDTO {

	Integer numeroPedido;
	List<ItemPedidoBoxDTO>  itens;
}
